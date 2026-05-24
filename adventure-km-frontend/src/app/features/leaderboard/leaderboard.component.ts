import { Component, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { UserApiService } from '../../core/services/user.service';
import { UserLevelResponse } from '../../core/models/user.model';
import { PixelAvatarComponent } from '../../shared/components/pixel-avatar/pixel-avatar.component';

@Component({
  selector: 'app-leaderboard',
  standalone: true,
  imports: [RouterLink, PixelAvatarComponent],
  template: `
    <div class="container">
      <h1 class="pixel-title">Classement</h1>
      <div class="sort-tabs">
        <button [class.active]="sortBy() === 'score'" (click)="sort('score')">Score RPG</button>
        <button [class.active]="sortBy() === 'km'" (click)="sort('km')">Kilomètres</button>
        <button [class.active]="sortBy() === 'elevation'" (click)="sort('elevation')">Dénivelé</button>
        <button [class.active]="sortBy() === 'count'" (click)="sort('count')">Aventures</button>
      </div>
      <div class="leaderboard-list">
        @for (user of users(); track user.userId; let i = $index) {
          <a [routerLink]="['/profile', user.username]" class="card lb-row">
            <span class="rank">#{{ i + 1 }}</span>
            <app-pixel-avatar [spriteId]="user.avatarSpriteId" [size]="40" />
            <span class="lb-name">{{ user.username }}</span>
            <span class="lb-level">Nv.{{ user.level }}</span>
            <span class="stat-value lb-value">{{ getDisplayValue(user) }}</span>
          </a>
        }
      </div>
    </div>
  `,
  styles: [`
    h1 { margin-bottom: 16px; }
    .sort-tabs { display: flex; gap: 8px; margin-bottom: 24px; }
    .sort-tabs button { font-size: 0.75rem; padding: 6px 14px; }
    .sort-tabs button.active { background: var(--green-primary); color: var(--bg-deep); }
    .lb-row {
      display: flex; align-items: center; gap: 16px;
      margin-bottom: 8px; padding: 12px 16px;
      text-decoration: none; color: inherit;
      transition: border-color 0.2s;
    }
    .lb-row:hover { border-color: var(--green-primary); }
    .rank { font-weight: 700; color: var(--gold-accent); min-width: 36px; }
    .lb-name { flex: 1; font-weight: 600; }
    .lb-level { color: var(--text-secondary); font-size: 0.8rem; }
    .lb-value { min-width: 80px; text-align: right; }
  `]
})
export class LeaderboardComponent implements OnInit {
  users = signal<UserLevelResponse[]>([]);
  sortBy = signal('score');

  constructor(private userApi: UserApiService) {}

  ngOnInit(): void { this.sort('score'); }

  sort(criteria: string): void {
    this.sortBy.set(criteria);
    this.userApi.getLeaderboard(criteria).subscribe(data => this.users.set(data));
  }

  getDisplayValue(user: UserLevelResponse): string {
    switch (this.sortBy()) {
      case 'km': return user.totalKm + ' km';
      case 'elevation': return user.totalElevationM + ' m';
      case 'count': return user.adventureCount + '';
      default: return user.rpgScore + ' pts';
    }
  }
}
