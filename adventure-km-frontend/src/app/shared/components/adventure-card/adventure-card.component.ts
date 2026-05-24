import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AdventureSummaryResponse } from '../../../core/models/adventure.model';
import { StatBadgeComponent } from '../stat-badge/stat-badge.component';

@Component({
  selector: 'app-adventure-card',
  standalone: true,
  imports: [RouterLink, StatBadgeComponent],
  templateUrl: './adventure-card.component.html',
  styleUrl: './adventure-card.component.css'
})
export class AdventureCardComponent {
  adventure = input.required<AdventureSummaryResponse>();

  formatDuration(minutes: number): string {
    const h = Math.floor(minutes / 60);
    const m = minutes % 60;
    return h > 0 ? `${h}h${m > 0 ? m.toString().padStart(2, '0') : ''}` : `${m}min`;
  }

  difficultyStars(): number[] {
    return Array.from({ length: this.adventure().difficulty ?? 0 }, (_, i) => i);
  }
}
