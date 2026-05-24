import { Injectable, signal, computed } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class TokenStorageService {
  private readonly ACCESS_KEY = 'akm_access_token';
  private readonly REFRESH_KEY = 'akm_refresh_token';

  private accessTokenSignal = signal<string | null>(this.getStored(this.ACCESS_KEY));

  readonly isLoggedIn = computed(() => this.accessTokenSignal() !== null);

  getAccessToken(): string | null {
    return this.accessTokenSignal();
  }

  getRefreshToken(): string | null {
    return this.getStored(this.REFRESH_KEY);
  }

  saveTokens(accessToken: string, refreshToken: string): void {
    localStorage.setItem(this.ACCESS_KEY, accessToken);
    localStorage.setItem(this.REFRESH_KEY, refreshToken);
    this.accessTokenSignal.set(accessToken);
  }

  clear(): void {
    localStorage.removeItem(this.ACCESS_KEY);
    localStorage.removeItem(this.REFRESH_KEY);
    this.accessTokenSignal.set(null);
  }

  private getStored(key: string): string | null {
    if (typeof localStorage === 'undefined') return null;
    return localStorage.getItem(key);
  }
}
