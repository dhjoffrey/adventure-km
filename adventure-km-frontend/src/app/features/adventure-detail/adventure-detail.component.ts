import { Component, OnInit, signal, input, effect, ElementRef, ViewChild, PLATFORM_ID, inject } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { AdventureApiService } from '../../core/services/adventure.service';
import { AdventureResponse } from '../../core/models/adventure.model';
import { StatBadgeComponent } from '../../shared/components/stat-badge/stat-badge.component';
import { MarkdownComponent } from 'ngx-markdown';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-adventure-detail',
  standalone: true,
  imports: [CommonModule, StatBadgeComponent, MarkdownComponent],
  templateUrl: './adventure-detail.component.html',
  styleUrl: './adventure-detail.component.css'
})
export class AdventureDetailComponent implements OnInit {
  id = input.required<string>();
  adventure = signal<AdventureResponse | null>(null);
  private mapInitialized = false;

  @ViewChild('mapContainer') mapContainer!: ElementRef;

  private readonly isBrowser = isPlatformBrowser(inject(PLATFORM_ID));

  constructor(private api: AdventureApiService) {
    effect(() => {
      const adv = this.adventure();
      if (adv?.gpxPath && this.isBrowser && !this.mapInitialized) {
        this.api.getGpxData(+this.id()).subscribe(gpxData => {
          setTimeout(() => this.initMap(gpxData.geojson), 50);
        });
      }
    });
  }

  ngOnInit(): void {
    this.api.getById(+this.id()).subscribe(data => this.adventure.set(data));
  }

  initMap(geojsonStr: string): void {
    if (!this.mapContainer?.nativeElement || this.mapInitialized) return;
    this.mapInitialized = true;

    import('leaflet').then(L => {
      const map = L.map(this.mapContainer.nativeElement).setView([45, 6], 10);
      L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '© OpenStreetMap'
      }).addTo(map);

      const geojson = JSON.parse(geojsonStr);
      const layer = L.geoJSON(geojson, {
        style: { color: '#22c55e', weight: 3 }
      }).addTo(map);
      map.fitBounds(layer.getBounds());
    });
  }

  formatDuration(minutes: number | undefined): string {
    if (!minutes) return '—';
    const h = Math.floor(minutes / 60);
    const m = minutes % 60;
    return `${h}h${m.toString().padStart(2, '0')}`;
  }
}
