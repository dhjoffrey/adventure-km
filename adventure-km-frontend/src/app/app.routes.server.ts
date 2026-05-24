import { RenderMode, ServerRoute } from '@angular/ssr';

export const serverRoutes: ServerRoute[] = [
  {
    path: 'adventures/:id',
    renderMode: RenderMode.Server
  },
  {
    path: 'adventures/:id/edit',
    renderMode: RenderMode.Server
  },
  {
    path: 'profile/:username',
    renderMode: RenderMode.Server
  },
  {
    path: '**',
    renderMode: RenderMode.Prerender
  }
];
