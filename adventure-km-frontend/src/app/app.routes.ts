import { Routes } from '@angular/router';
import { authGuard } from './core/auth/auth.guard';
import { adminGuard } from './core/auth/admin.guard';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./features/home/home.component').then(m => m.HomeComponent)
  },
  {
    path: 'login',
    loadComponent: () => import('./features/login/login.component').then(m => m.LoginComponent)
  },
  {
    path: 'register',
    loadComponent: () => import('./features/register/register.component').then(m => m.RegisterComponent)
  },
  {
    path: 'adventures',
    loadComponent: () => import('./features/adventures/adventures.component').then(m => m.AdventuresComponent)
  },
  {
    path: 'adventures/new',
    loadComponent: () => import('./features/adventure-form/adventure-form.component').then(m => m.AdventureFormComponent),
    canActivate: [authGuard]
  },
  {
    path: 'adventures/:id',
    loadComponent: () => import('./features/adventure-detail/adventure-detail.component').then(m => m.AdventureDetailComponent)
  },
  {
    path: 'adventures/:id/edit',
    loadComponent: () => import('./features/adventure-form/adventure-form.component').then(m => m.AdventureFormComponent),
    canActivate: [authGuard]
  },
  {
    path: 'profile/:username',
    loadComponent: () => import('./features/profile/profile.component').then(m => m.ProfileComponent)
  },
  {
    path: 'leaderboard',
    loadComponent: () => import('./features/leaderboard/leaderboard.component').then(m => m.LeaderboardComponent)
  },
  {
    path: 'admin',
    loadComponent: () => import('./features/admin/admin.component').then(m => m.AdminComponent),
    canActivate: [adminGuard]
  },
  {
    path: 'admin/invitations',
    redirectTo: 'admin'
  },
  { path: '**', redirectTo: '' }
];
