import { Component } from '@angular/core';

@Component({
  selector: 'app-footer',
  standalone: true,
  template: `
    <footer class="footer">
      <div class="container">
        <span class="pixel-title">Adventure-KM</span>
        <span class="footer-text">— trails, ultras & pixels</span>
      </div>
    </footer>
  `,
  styles: [`
    .footer {
      border-top: 1px solid var(--border-color);
      padding: 24px 0;
      margin-top: 60px;
      text-align: center;
    }
    .footer-text {
      color: var(--text-muted);
      font-size: 0.75rem;
      margin-left: 8px;
    }
  `]
})
export class FooterComponent {}
