import { inject } from '@angular/core';
import { Router, type CanActivateFn } from '@angular/router';
import { TokenStorageService } from './token-storage.service';

export const authGuard: CanActivateFn = () => {
  const tokenStorage = inject(TokenStorageService);
  const router = inject(Router);
  if (tokenStorage.isLoggedIn()) return true;
  router.navigate(['/login']);
  return false;
};
