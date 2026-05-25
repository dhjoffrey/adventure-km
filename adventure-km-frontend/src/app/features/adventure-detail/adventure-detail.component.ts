import { Component, OnDestroy, signal, input, effect, ElementRef, ViewChild, PLATFORM_ID, inject, computed, type Signal } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { RouterLink } from '@angular/router';
import { switchMap, tap } from 'rxjs';
import { toObservable, toSignal } from '@angular/core/rxjs-interop';
import { AdventureApiService } from '../../core/services/adventure.service';
import { AdventureResponse, WaypointDto } from '../../core/models/adventure.model';
import { MarkdownComponent } from 'ngx-markdown';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../core/auth/auth.service';

interface ElevationPoint { distanceKm: number; altitudeM: number; }
interface HoveredPoint { distanceKm: number; altitudeM: number; svgX: number; }

const SVG_W = 1000;
const SVG_H = 160;

@Component({
  selector: 'app-adventure-detail',
  standalone: true,
  imports: [CommonModule, MarkdownComponent, RouterLink],
  templateUrl: './adventure-detail.component.html',
  styleUrl: './adventure-detail.component.css'
})
export class AdventureDetailComponent implements OnDestroy {
  id = input.required<string>();

  private readonly api = inject(AdventureApiService);
  private readonly isBrowser = isPlatformBrowser(inject(PLATFORM_ID));
  private readonly auth = inject(AuthService);
  private mapInitializedForId: string | null = null;

  @ViewChild('mapContainer') mapContainer!: ElementRef;

  // Derived from id via switchMap: never passes through null between navigations.
  // This prevents Angular @if from destroying/recreating the embedded view on each load,
  // which caused a double-render (orphaned view + new view simultaneously in the DOM).
  readonly adventure: Signal<AdventureResponse | null>;

  readonly loaded: Signal<boolean>;
  readonly isOwner: Signal<boolean>;

  // Elevation profile
  elevationPoints = signal<ElevationPoint[]>([]);
  hoveredPoint = signal<HoveredPoint | null>(null);
  waypoints = signal<WaypointDto[]>([]);
  elevationFillPath = '';
  elevationLinePath = '';
  private geojsonCoords: [number, number][] = [];
  private leafletMapRef: any = null;
  private elevationMarker: any = null;
  private baseLayerStandard: any = null;
  private baseLayerTopo: any = null;
  private waypointMarkers: any[] = [];

  // Map controls
  mapFollow = signal(true);
  mapLayer = signal<'osm' | 'topo'>('osm');
  mapReady = signal(false);

  // Segment selection
  selectionStartRatio = signal<number | null>(null);
  selectionEndRatio = signal<number | null>(null);
  private isDragging = false;
  private selectionLayerRef: any = null;

  readonly selectionRect = computed(() => {
    const s = this.selectionStartRatio();
    const e = this.selectionEndRatio();
    if (s === null || e === null) return null;
    const x1 = Math.min(s, e) * SVG_W;
    const x2 = Math.max(s, e) * SVG_W;
    return { x: x1, width: x2 - x1 };
  });

  readonly selectionStats = computed(() => {
    const s = this.selectionStartRatio();
    const e = this.selectionEndRatio();
    const pts = this.elevationPoints();
    if (s === null || e === null || pts.length < 2) return null;
    const n = pts.length - 1;
    const i0 = Math.round(Math.min(s, e) * n);
    const i1 = Math.round(Math.max(s, e) * n);
    let gain = 0, loss = 0;
    for (let i = i0 + 1; i <= i1; i++) {
      const diff = pts[i].altitudeM - pts[i - 1].altitudeM;
      if (diff > 0) gain += diff; else loss -= diff;
    }
    return {
      distanceKm: pts[i1].distanceKm - pts[i0].distanceKm,
      gain: Math.round(gain),
      loss: Math.round(loss)
    };
  });

  readonly waypointTicks = computed(() => {
    const wpts = this.waypoints();
    const pts = this.elevationPoints();
    if (!wpts.length || pts.length < 2) return [];
    const maxDist = pts[pts.length - 1].distanceKm;
    return wpts.map(w => ({ x: (w.distanceKm / maxDist) * SVG_W, name: w.name }));
  });

  constructor() {
    this.adventure = toSignal(
      toObservable(this.id).pipe(
        tap(() => this.resetOnIdChange()),
        switchMap(id => this.api.getById(+id))
      ),
      { initialValue: null }
    );
    this.loaded = computed(() => this.adventure() !== null);
    this.isOwner = computed(() => {
      const adv = this.adventure();
      return !!adv && adv.author.username === this.auth.currentUsername();
    });

    // Loads GPX + initializes map once per adventure
    effect(() => {
      const adv = this.adventure();
      if (adv?.gpxPath && this.isBrowser && this.mapInitializedForId !== this.id()) {
        this.mapInitializedForId = this.id();
        this.api.getGpxData(adv.id).subscribe(gpxData => {
          this.elevationPoints.set(gpxData.elevationPoints);
          this.waypoints.set(gpxData.waypoints ?? []);
          this.buildElevationPaths(gpxData.elevationPoints);
          setTimeout(() => this.initMap(gpxData.geojson, gpxData.waypoints ?? []), 50);
        });
      }
    });
  }

  ngOnDestroy(): void {
    this.cleanupMap();
  }

  private resetOnIdChange(): void {
    this.elevationPoints.set([]);
    this.hoveredPoint.set(null);
    this.elevationFillPath = '';
    this.elevationLinePath = '';
    this.cleanupMap();
  }

  private cleanupMap(): void {
    if (this.selectionLayerRef) {
      this.selectionLayerRef.remove();
      this.selectionLayerRef = null;
    }
    this.waypointMarkers.forEach(m => m.remove());
    this.waypointMarkers = [];
    if (this.leafletMapRef) {
      this.leafletMapRef.remove();
      this.leafletMapRef = null;
    }
    this.elevationMarker = null;
    this.baseLayerStandard = null;
    this.baseLayerTopo = null;
    this.geojsonCoords = [];
    this.mapInitializedForId = null;
    this.selectionStartRatio.set(null);
    this.selectionEndRatio.set(null);
    this.isDragging = false;
    this.mapFollow.set(true);
    this.mapLayer.set('osm');
    this.mapReady.set(false);
    this.waypoints.set([]);
  }

  // ── Elevation profile ─────────────────────────────────────────────────────

  private buildElevationPaths(points: ElevationPoint[]): void {
    if (points.length < 2) return;
    const alts = points.map(p => p.altitudeM);
    const minAlt = Math.min(...alts) - 5;
    const maxAlt = Math.max(...alts) + 5;
    const maxDist = points[points.length - 1].distanceKm;
    const tx = (d: number) => (d / maxDist) * SVG_W;
    const ty = (a: number) => SVG_H - ((a - minAlt) / (maxAlt - minAlt)) * SVG_H;

    const coords = points.map(p => `${tx(p.distanceKm).toFixed(1)},${ty(p.altitudeM).toFixed(1)}`);
    this.elevationLinePath = `M ${coords.join(' L ')}`;
    this.elevationFillPath = `M 0,${SVG_H} L ${coords.join(' L ')} L ${SVG_W},${SVG_H} Z`;
  }

  onElevationMouseDown(event: MouseEvent): void {
    if (event.button !== 0) return;
    const rect = (event.currentTarget as Element).getBoundingClientRect();
    const ratio = Math.max(0, Math.min(1, (event.clientX - rect.left) / rect.width));
    this.isDragging = true;
    this.selectionStartRatio.set(ratio);
    this.selectionEndRatio.set(ratio);
    event.preventDefault();
  }

  onElevationMouseMove(event: MouseEvent): void {
    const points = this.elevationPoints();
    if (!points.length) return;
    const rect = (event.currentTarget as Element).getBoundingClientRect();
    const ratio = Math.max(0, Math.min(1, (event.clientX - rect.left) / rect.width));
    const maxDist = points[points.length - 1].distanceKm;
    const targetDist = ratio * maxDist;

    // Find nearest point by cumulative distance, track its index for map sync
    let nearestIdx = 0;
    let nearestDiff = Infinity;
    for (let i = 0; i < points.length; i++) {
      const diff = Math.abs(points[i].distanceKm - targetDist);
      if (diff < nearestDiff) { nearestDiff = diff; nearestIdx = i; }
    }
    const nearest = points[nearestIdx];
    this.hoveredPoint.set({ ...nearest, svgX: ratio * SVG_W });

    if (this.geojsonCoords.length && this.elevationMarker) {
      // Use the same index as the elevation point — guarantees map/profile alignment
      const coord = this.geojsonCoords[Math.min(nearestIdx, this.geojsonCoords.length - 1)];
      this.elevationMarker.setLatLng([coord[1], coord[0]]);
      this.elevationMarker.setStyle({ opacity: 1, fillOpacity: 0.95 });
      if (this.mapFollow() && this.leafletMapRef) {
        this.leafletMapRef.panTo(
          this.elevationMarker.getLatLng(),
          { animate: true, duration: 0.15, noMoveStart: true }
        );
      }
    }

    if (this.isDragging) {
      this.selectionEndRatio.set(ratio);
      this.updateMapSelection();
    }
  }

  onElevationMouseUp(event: MouseEvent): void {
    if (!this.isDragging) return;
    this.isDragging = false;
    const rect = (event.currentTarget as Element).getBoundingClientRect();
    const ratio = Math.max(0, Math.min(1, (event.clientX - rect.left) / rect.width));
    this.selectionEndRatio.set(ratio);
    const start = this.selectionStartRatio()!;
    if (Math.abs(ratio - start) < 0.01) {
      this.clearSelection();
    } else {
      this.updateMapSelection();
    }
  }

  onElevationMouseLeave(): void {
    this.hoveredPoint.set(null);
    this.elevationMarker?.setStyle({ opacity: 0, fillOpacity: 0 });
    if (this.isDragging) {
      this.isDragging = false;
      if (Math.abs((this.selectionEndRatio() ?? 0) - (this.selectionStartRatio() ?? 0)) < 0.01) {
        this.clearSelection();
      }
    }
  }

  onElevationTouchStart(event: TouchEvent): void {
    event.preventDefault();
    const t = event.touches[0];
    this.onElevationMouseDown({ button: 0, clientX: t.clientX, currentTarget: event.currentTarget, preventDefault: () => {} } as any);
  }

  onElevationTouchMove(event: TouchEvent): void {
    event.preventDefault();
    const t = event.touches[0];
    this.onElevationMouseMove({ clientX: t.clientX, currentTarget: event.currentTarget } as any);
  }

  onElevationTouchEnd(event: TouchEvent): void {
    const t = event.changedTouches[0];
    this.onElevationMouseUp({ clientX: t.clientX, currentTarget: event.currentTarget } as any);
  }

  clearSelection(): void {
    this.selectionStartRatio.set(null);
    this.selectionEndRatio.set(null);
    if (this.selectionLayerRef) {
      this.selectionLayerRef.remove();
      this.selectionLayerRef = null;
    }
  }

  private updateMapSelection(): void {
    if (!this.leafletMapRef || !this.geojsonCoords.length) return;
    const start = this.selectionStartRatio();
    const end = this.selectionEndRatio();
    if (start === null || end === null) return;

    const s = Math.min(start, end);
    const e = Math.max(start, end);
    const n = this.geojsonCoords.length - 1;
    const startIdx = Math.max(0, Math.round(s * n));
    const endIdx = Math.min(n, Math.round(e * n));
    const coords = this.geojsonCoords.slice(startIdx, endIdx + 1)
      .map(c => [c[1], c[0]] as [number, number]);

    if (this.selectionLayerRef) {
      this.selectionLayerRef.remove();
      this.selectionLayerRef = null;
    }

    import('leaflet').then(L => {
      if (!this.leafletMapRef) return;
      this.selectionLayerRef = L.polyline(coords, {
        color: '#ffd700',
        weight: 5,
        opacity: 0.9
      }).addTo(this.leafletMapRef);
    });
  }

  // ── Map ───────────────────────────────────────────────────────────────────

  switchLayer(layer: 'osm' | 'topo'): void {
    if (layer === this.mapLayer() || !this.leafletMapRef) return;
    if (layer === 'topo') {
      this.baseLayerStandard.remove();
      this.baseLayerTopo.addTo(this.leafletMapRef);
    } else {
      this.baseLayerTopo.remove();
      this.baseLayerStandard.addTo(this.leafletMapRef);
    }
    this.mapLayer.set(layer);
  }

  initMap(geojsonStr: string, wpts: WaypointDto[] = []): void {
    if (!this.mapContainer?.nativeElement) return;

    import('leaflet').then(L => {
      const map = L.map(this.mapContainer.nativeElement).setView([45, 6], 10);

      this.baseLayerStandard = L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '© OpenStreetMap'
      });
      this.baseLayerTopo = L.tileLayer('https://{s}.tile.opentopomap.org/{z}/{x}/{y}.png', {
        attribution: '© OpenTopoMap contributors'
      });
      this.baseLayerStandard.addTo(map);

      const geojson = JSON.parse(geojsonStr);
      const layer = L.geoJSON(geojson, {
        style: { color: '#22c55e', weight: 3 }
      }).addTo(map);
      map.fitBounds(layer.getBounds());

      // Backend returns a GeoJSON Feature; geometry.coordinates is the correct path
      const coords: [number, number][] = geojson.type === 'LineString'
        ? geojson.coordinates
        : geojson.geometry?.coordinates ?? [];
      this.geojsonCoords = coords;
      this.leafletMapRef = map;

      this.elevationMarker = L.circleMarker([0, 0], {
        radius: 7,
        fillColor: '#ffd700',
        fillOpacity: 0.95,
        color: '#1a1a1a',
        weight: 2
      }).addTo(map);
      this.elevationMarker.setStyle({ opacity: 0, fillOpacity: 0 });

      // Waypoint diamond markers
      this.waypointMarkers.forEach(m => m.remove());
      this.waypointMarkers = wpts.map(wpt => {
        const icon = L.divIcon({
          html: `<div class="wpt-marker" title="${wpt.name}"></div>`,
          className: '',
          iconSize: [14, 14],
          iconAnchor: [7, 7]
        });
        const marker = L.marker([wpt.lat, wpt.lon], { icon }).addTo(map);
        if (wpt.name) marker.bindTooltip(wpt.name, { permanent: false, direction: 'top' });
        return marker;
      });

      this.mapReady.set(true);
    });
  }

  formatDuration(minutes: number | undefined): string {
    if (!minutes) return '—';
    const h = Math.floor(minutes / 60);
    const m = minutes % 60;
    return `${h}h${m.toString().padStart(2, '0')}`;
  }
}
