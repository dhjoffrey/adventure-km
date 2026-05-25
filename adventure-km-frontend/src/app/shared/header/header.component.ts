import { Component, HostListener, OnInit, effect, inject, signal } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../core/auth/auth.service';
import { UserApiService } from '../../core/services/user.service';
import { PixelAvatarComponent } from '../components/pixel-avatar/pixel-avatar.component';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [RouterLink, RouterLinkActive, PixelAvatarComponent],
  templateUrl: './header.component.html',
  styleUrl: './header.component.css'
})
export class HeaderComponent implements OnInit {
  protected readonly auth = inject(AuthService);
  readonly userApi = inject(UserApiService);

  menuOpen = signal(false);

  constructor() {
    effect(() => {
      if (this.auth.isLoggedIn()) {
        const username = this.auth.currentUsername();
        if (username) {
          this.userApi.getProfile(username).subscribe(p =>
            this.userApi.currentAvatarSpriteId.set(p.avatarSpriteId)
          );
        }
      } else {
        this.userApi.currentAvatarSpriteId.set(1);
        this.menuOpen.set(false);
      }
    });
  }

  ngOnInit(): void {}

  toggleMenu(event: Event): void {
    event.stopPropagation();
    this.menuOpen.update(v => !v);
  }

  @HostListener('document:click')
  closeMenu(): void {
    this.menuOpen.set(false);
  }
}
