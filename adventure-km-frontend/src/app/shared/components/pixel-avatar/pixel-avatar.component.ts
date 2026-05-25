import { Component, computed, input } from '@angular/core';

@Component({
  selector: 'app-pixel-avatar',
  standalone: true,
  template: `
    <div class="pixel-avatar" [style.width.px]="size()" [style.height.px]="size()">
      <img [src]="spriteSrc()" [alt]="'Avatar ' + spriteId()" class="sprite-img" />
    </div>
  `,
  styles: [`
    .pixel-avatar {
      display: flex;
      align-items: flex-start;
      justify-content: center;
      background: var(--bg-deep);
      border-radius: 50%;
      border: 2px solid var(--gold-accent);
      overflow: hidden;
    }
    .sprite-img {
      width: auto;
      height: 142%;
      margin-top: 4%;
      object-fit: contain;
      image-rendering: pixelated;
      flex-shrink: 0;
    }
  `]
})
export class PixelAvatarComponent {
  spriteId = input<number>(1);
  size = input<number>(96);

  readonly spriteSrc = computed(() => {
    const id = ((this.spriteId() - 1) % 10) + 1;
    return `/avatars/avatar_${id}.png`;
  });
}
