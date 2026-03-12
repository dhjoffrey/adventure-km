import { Routes } from '@angular/router';
import { HomeComponent } from './features/home/home.component';
import { ActivitiesComponent } from './features/activities/activities.component';

export const routes: Routes = [
  { path: 'home', component: HomeComponent },
  { path: 'activities', component: ActivitiesComponent },
  { path: '', redirectTo: 'home', pathMatch: 'full' }
];