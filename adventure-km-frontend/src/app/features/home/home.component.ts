import { Component, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AdventureApiService } from '../../core/services/adventure.service';
import { AdventureSummaryResponse } from '../../core/models/adventure.model';
import { AdventureCardComponent } from '../../shared/components/adventure-card/adventure-card.component';
import { AuthService } from '../../core/auth/auth.service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [RouterLink, AdventureCardComponent],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent implements OnInit {
  adventures = signal<AdventureSummaryResponse[]>([]);

  constructor(
    private api: AdventureApiService,
    protected auth: AuthService
  ) {}

  ngOnInit(): void {
    this.api.listPublished().subscribe(data => this.adventures.set(data.slice(0, 6)));
  }
}
