import { Component, OnInit, signal, input, AfterViewInit, ElementRef, ViewChild } from '@angular/core';
import { AdventureApiService } from '../../core/services/adventure.service';
import { AdventureResponse } from '../../core/models/adventure.model';
import { StatBadgeComponent } from '../../shared/components/stat-badge/stat-badge.component';
import { MarkdownComponent } from 'ngx-markdown';
import { CommonModule } from '@angular/common';

declare const L: any;

@Component({
  selector: 'app-adventure-detail',
  standalone: true,
  imports: [CommonModule, StatBadgeComponent, MarkdownComponent],
  templateUrl: './adventure-detail.component.html',
  styleUrl: './adventure-detail.component.css'
})
export class AdventureDetailComponent implements OnInit, AfterViewInit {
  id = input.required<string>();
  adventure = signal<AdventureResponse | null>(null);

  @ViewChild('mapContainer') mapContainer!: ElementRef;

  constructor(private api: AdventureApiService) {}

  ngOnInit(): void {
    this.api.getById(+this.id()).subscribe(data => {
      this.adventure.set(data);
    });
  }

  ngAfterViewInit(): void {}

  initMap(geojsonStr: string): void {
    if (typeof L === 'undefined' || !this.mapContainer?.nativeElement) return;
    const map = L.map(this.mapContainer.nativeElement).setView([45, 6], 10);
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '© OpenStreetMap'
    }).addTo(map);

    const geojson = JSON.parse(geojsonStr);
    const layer = L.geoJSON(geojson, {
      style: { color: '#22c55e', weight: 3 }
    }).addTo(map);
    map.fitBounds(layer.getBounds());
  }

  formatDuration(minutes: number | undefined): string {
    if (!minutes) return '—';
    const h = Math.floor(minutes / 60);
    const m = minutes % 60;
    return `${h}h${m.toString().padStart(2, '0')}`;
  }
}
