import { Component, PLATFORM_ID, inject } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { NavigationEnd, Router, RouterOutlet } from '@angular/router';
import { filter } from 'rxjs';
import { HeaderComponent } from './shared/header/header.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, HeaderComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  constructor() {
    const router = inject(Router);
    const isBrowser = isPlatformBrowser(inject(PLATFORM_ID));
    if (isBrowser) {
      router.events.pipe(filter(e => e instanceof NavigationEnd)).subscribe(() => {
        window.scrollTo(0, 0);
      });
    }
  }
}
