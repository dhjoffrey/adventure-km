import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  AdventureResponse,
  AdventureSummaryResponse,
  AdventureCreateRequest,
  GpxDataResponse
} from '../models/adventure.model';

@Injectable({ providedIn: 'root' })
export class AdventureApiService {
  private readonly API = '/api/adventures';

  constructor(private http: HttpClient) {}

  listPublished(): Observable<AdventureSummaryResponse[]> {
    return this.http.get<AdventureSummaryResponse[]>(this.API);
  }

  getById(id: number): Observable<AdventureResponse> {
    return this.http.get<AdventureResponse>(`${this.API}/${id}`);
  }

  create(request: AdventureCreateRequest): Observable<AdventureResponse> {
    return this.http.post<AdventureResponse>(this.API, request);
  }

  update(id: number, request: Partial<AdventureCreateRequest>): Observable<AdventureResponse> {
    return this.http.put<AdventureResponse>(`${this.API}/${id}`, request);
  }

  publish(id: number): Observable<AdventureResponse> {
    return this.http.post<AdventureResponse>(`${this.API}/${id}/publish`, {});
  }

  uploadGpx(id: number, file: File): Observable<AdventureResponse> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<AdventureResponse>(`${this.API}/${id}/gpx`, formData);
  }

  uploadPhoto(id: number, file: File, caption: string): Observable<AdventureResponse> {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('caption', caption);
    return this.http.post<AdventureResponse>(`${this.API}/${id}/photos`, formData);
  }

  getGpxData(id: number): Observable<GpxDataResponse> {
    return this.http.get<GpxDataResponse>(`${this.API}/${id}/gpx/data`);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.API}/${id}`);
  }
}
