import { Component, input } from '@angular/core';

@Component({
  selector: 'app-pixel-avatar',
  standalone: true,
  template: `
    <div class="pixel-avatar pixel-border" [style.width.px]="size()" [style.height.px]="size()">
      <div class="sprite" [attr.data-sprite]="spriteId()">
        <span class="avatar-char">{{ avatarChar() }}</span>
      </div>
    </div>
  `,
  styles: [`
    .pixel-avatar {
      display: flex;
      align-items: center;
      justify-content: center;
      background: var(--bg-surface);
      image-rendering: pixelated;
    }
    .sprite {
      display: flex;
      align-items: center;
      justify-content: center;
      width: 100%;
      height: 100%;
    }
    .avatar-char {
      font-family: var(--font-pixel);
      font-size: 2rem;
      color: var(--gold-accent);
    }
  `]
})
export class PixelAvatarComponent {
  spriteId = input<number>(1);
  size = input<number>(96);

  avatarChar(): string {
    const chars = ['⚔', '🏔', '🗡', '🛡', '⚡', '🔥', '💎', '🌟', '👑', '🏆'];
    return chars[(this.spriteId() - 1) % chars.length];
  }
}
