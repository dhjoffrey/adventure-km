import { Component, OnInit, signal, input } from '@angular/core';
import { UserApiService } from '../../core/services/user.service';
import { UserLevelResponse } from '../../core/models/user.model';
import { AdventureSummaryResponse } from '../../core/models/adventure.model';
import { PixelAvatarComponent } from '../../shared/components/pixel-avatar/pixel-avatar.component';
import { StatBadgeComponent } from '../../shared/components/stat-badge/stat-badge.component';
import { AdventureCardComponent } from '../../shared/components/adventure-card/adventure-card.component';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [PixelAvatarComponent, StatBadgeComponent, AdventureCardComponent],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.css'
})
export class ProfileComponent implements OnInit {
  username = input.required<string>();
  profile = signal<UserLevelResponse | null>(null);
  adventures = signal<AdventureSummaryResponse[]>([]);

  constructor(private userApi: UserApiService) {}

  ngOnInit(): void {
    this.userApi.getProfile(this.username()).subscribe(p => this.profile.set(p));
    this.userApi.getUserAdventures(this.username()).subscribe(a => this.adventures.set(a));
  }

  xpProgress(): number {
    const p = this.profile();
    if (!p) return 0;
    const currentLevelScore = p.level * p.level * 10;
    const nextLevelScore = (p.level + 1) * (p.level + 1) * 10;
    return ((p.rpgScore - currentLevelScore) / (nextLevelScore - currentLevelScore)) * 100;
  }
}
