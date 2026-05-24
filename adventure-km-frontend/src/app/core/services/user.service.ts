import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UserLevelResponse } from '../models/user.model';
import { AdventureSummaryResponse } from '../models/adventure.model';

@Injectable({ providedIn: 'root' })
export class UserApiService {
  constructor(private http: HttpClient) {}

  getProfile(username: string): Observable<UserLevelResponse> {
    return this.http.get<UserLevelResponse>(`/api/users/${username}`);
  }

  getUserAdventures(username: string): Observable<AdventureSummaryResponse[]> {
    return this.http.get<AdventureSummaryResponse[]>(`/api/users/${username}/adventures`);
  }

  getLeaderboard(sortBy: string = 'score'): Observable<UserLevelResponse[]> {
    return this.http.get<UserLevelResponse[]>(`/api/leaderboard?sortBy=${sortBy}`);
  }

  updateTheme(theme: string): Observable<void> {
    return this.http.patch<void>('/api/users/me/theme', { theme });
  }
}
