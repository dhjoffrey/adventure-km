import { Component, signal, OnInit } from '@angular/core';
import { AdventureCardComponent } from '../../shared/components/adventure-card/adventure-card.component';
import { AdventureApiService } from '../../core/services/adventure.service';
import { AdventureSummaryResponse } from '../../core/models/adventure.model';

@Component({
  selector: 'app-adventures',
  standalone: true,
  imports: [AdventureCardComponent],
  template: `
    <div class="container">
      <h1 class="pixel-title">Aventures</h1>
      <div class="adventure-grid">
        @for (adventure of adventures(); track adventure.id) {
          <app-adventure-card [adventure]="adventure" />
        }
      </div>
      @if (adventures().length === 0) {
        <p class="empty">Aucune aventure publiée pour le moment.</p>
      }
    </div>
  `,
  styles: [`
    h1 { margin-bottom: 24px; }
    .adventure-grid {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
      gap: 20px;
    }
    .empty { color: var(--text-secondary); text-align: center; margin-top: 40px; }
  `]
})
export class AdventuresComponent implements OnInit {
  adventures = signal<AdventureSummaryResponse[]>([]);

  constructor(private api: AdventureApiService) {}

  ngOnInit(): void {
    this.api.listPublished().subscribe(data => this.adventures.set(data));
  }
}
