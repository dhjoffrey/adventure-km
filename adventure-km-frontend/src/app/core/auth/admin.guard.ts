import { inject } from '@angular/core';
import { Router, type CanActivateFn } from '@angular/router';
import { TokenStorageService } from './token-storage.service';

export const adminGuard: CanActivateFn = () => {
  const tokenStorage = inject(TokenStorageService);
  const router = inject(Router);
  const token = tokenStorage.getAccessToken();
  if (!token) { router.navigate(['/login']); return false; }
  try {
    const payload = JSON.parse(atob(token.split('.')[1]));
    if (payload.role === 'ADMIN') return true;
  } catch { /* ignore */ }
  router.navigate(['/']);
  return false;
};
