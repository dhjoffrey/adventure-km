import { Injectable, PLATFORM_ID, effect, inject, signal } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { DOCUMENT } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { catchError, EMPTY } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class ThemeService {
  private readonly document = inject(DOCUMENT);
  private readonly http = inject(HttpClient);
  private readonly isBrowser = isPlatformBrowser(inject(PLATFORM_ID));

  readonly theme = signal<'light' | 'dark'>(
    this.isBrowser ? ((localStorage.getItem('theme') as 'light' | 'dark') ?? 'light') : 'light'
  );

  constructor() {
    effect(() => {
      const t = this.theme();
      if (this.isBrowser) {
        this.document.documentElement.setAttribute('data-theme', t);
        localStorage.setItem('theme', t);
      }
    });
  }

  applyFromProfile(theme: string): void {
    if (theme === 'light' || theme === 'dark') {
      this.theme.set(theme);
    }
  }

  toggle(): void {
    const next = this.theme() === 'light' ? 'dark' : 'light';
    this.theme.set(next);
    this.http.patch('/api/users/me/theme', { theme: next })
      .pipe(catchError(() => EMPTY))
      .subscribe();
  }
}
