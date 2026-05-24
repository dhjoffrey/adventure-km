import { Injectable, computed, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { tap } from 'rxjs';
import { TokenStorageService } from './token-storage.service';
import { AuthResponse, LoginRequest, RegisterRequest } from '../models/user.model';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly API = '/api/auth';

  private readonly http = inject(HttpClient);
  private readonly tokenStorage = inject(TokenStorageService);
  private readonly router = inject(Router);

  readonly isLoggedIn = this.tokenStorage.isLoggedIn;
  readonly currentUsername = computed(() => {
    const token = this.tokenStorage.getAccessToken();
    if (!token) return null;
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      return payload.sub as string;
    } catch {
      return null;
    }
  });

  constructor() {}

  login(request: LoginRequest) {
    return this.http.post<AuthResponse>(`${this.API}/login`, request).pipe(
      tap(res => this.tokenStorage.saveTokens(res.accessToken, res.refreshToken))
    );
  }

  register(request: RegisterRequest) {
    return this.http.post<AuthResponse>(`${this.API}/register`, request).pipe(
      tap(res => this.tokenStorage.saveTokens(res.accessToken, res.refreshToken))
    );
  }

  refresh() {
    const refreshToken = this.tokenStorage.getRefreshToken();
    return this.http.post<AuthResponse>(`${this.API}/refresh`, { refreshToken }).pipe(
      tap(res => this.tokenStorage.saveTokens(res.accessToken, res.refreshToken))
    );
  }

  logout(): void {
    this.tokenStorage.clear();
    this.router.navigate(['/']);
  }
}
