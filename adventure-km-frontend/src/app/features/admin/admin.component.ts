import { Component, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { UserResponse } from '../../core/models/user.model';

interface InvitationResponse {
  id: number;
  token: string;
  email: string;
  expiresAt: string;
  usedAt: string | null;
}

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [FormsModule],
  template: `
    <div class="container">
      <h1 class="pixel-title">Administration</h1>

      <section>
        <h2 class="pixel-title">Utilisateurs</h2>
        @for (user of users(); track user.id) {
          <div class="card" style="margin-bottom: 8px; padding: 12px;">
            <strong>{{ user.username }}</strong> — {{ user.role }}
          </div>
        }
      </section>

      <section style="margin-top: 32px;">
        <h2 class="pixel-title">Invitations</h2>
        <form (ngSubmit)="createInvitation()" style="display: flex; gap: 12px; margin-bottom: 16px;">
          <input [(ngModel)]="inviteEmail" name="email" placeholder="Email (optionnel)" />
          <button type="submit" class="btn-gold">Générer</button>
        </form>
        @for (inv of invitations(); track inv.id) {
          <div class="card" style="margin-bottom: 8px; padding: 12px; font-size: 0.8rem;">
            <code>{{ getInviteUrl(inv.token) }}</code>
            @if (inv.usedAt) {
              <span style="color: var(--text-muted); margin-left: 8px;">— utilisée</span>
            } @else {
              <span style="color: var(--green-primary); margin-left: 8px;">— active</span>
            }
          </div>
        }
      </section>
    </div>
  `
})
export class AdminComponent implements OnInit {
  users = signal<UserResponse[]>([]);
  invitations = signal<InvitationResponse[]>([]);
  inviteEmail = '';

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.http.get<UserResponse[]>('/api/admin/users').subscribe(u => this.users.set(u));
    this.http.get<InvitationResponse[]>('/api/admin/invitations').subscribe(i => this.invitations.set(i));
  }

  createInvitation(): void {
    this.http.post<InvitationResponse>('/api/admin/invitations', { email: this.inviteEmail || null })
      .subscribe(inv => {
        this.invitations.update(list => [inv, ...list]);
        this.inviteEmail = '';
      });
  }

  getInviteUrl(token: string): string {
    return `${window.location.origin}/register?token=${token}`;
  }
}
