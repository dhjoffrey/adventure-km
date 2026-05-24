import { Component, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../core/auth/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent {
  username = '';
  email = '';
  password = '';
  invitationToken = '';
  error = signal<string | null>(null);
  loading = signal(false);

  constructor(private auth: AuthService, private router: Router, private route: ActivatedRoute) {
    this.invitationToken = this.route.snapshot.queryParamMap.get('token') ?? '';
  }

  onSubmit(): void {
    this.loading.set(true);
    this.error.set(null);
    this.auth.register({
      username: this.username,
      email: this.email,
      password: this.password,
      invitationToken: this.invitationToken
    }).subscribe({
      next: () => { this.router.navigate(['/']); },
      error: (err) => {
        this.error.set(err.error?.detail ?? 'Erreur lors de l\'inscription');
        this.loading.set(false);
      }
    });
  }
}
