import { Component, input } from '@angular/core';

@Component({
  selector: 'app-stat-badge',
  standalone: true,
  template: `
    <div class="stat-badge">
      <span class="stat-value">{{ value() }}</span>
      <span class="stat-label">{{ label() }}</span>
    </div>
  `,
  styles: [`
    .stat-badge { text-align: center; }
  `]
})
export class StatBadgeComponent {
  value = input.required<string | number>();
  label = input.required<string>();
}
