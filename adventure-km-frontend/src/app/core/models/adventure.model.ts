import { UserResponse } from './user.model';
import { EquipmentItemResponse } from './equipment.model';

export interface AdventureStatsResponse {
  distanceKm: number;
  elevationGainM: number;
  elevationLossM: number;
  durationMinutes: number;
  maxAltitudeM: number;
  minAltitudeM: number;
}

export interface PhotoResponse {
  id: number;
  filePath: string;
  caption: string;
  sortOrder: number;
}

export interface AdventureSummaryResponse {
  id: number;
  title: string;
  date: string;
  type: string;
  difficulty: number;
  status: string;
  author: UserResponse;
  stats: AdventureStatsResponse;
}

export interface AdventureResponse {
  id: number;
  title: string;
  date: string;
  content: string;
  type: string;
  difficulty: number;
  gpxPath: string;
  status: string;
  author: UserResponse;
  stats: AdventureStatsResponse;
  photos: PhotoResponse[];
  equipment: EquipmentItemResponse[];
}

export interface AdventureCreateRequest {
  title: string;
  date: string;
  content: string;
  type?: string;
  difficulty?: number;
  equipmentIds?: number[];
}

export interface GpxDataResponse {
  distanceKm: number;
  elevationGainM: number;
  elevationLossM: number;
  durationMinutes: number;
  maxAltitudeM: number;
  minAltitudeM: number;
  geojson: string;
  elevationPoints: { distanceKm: number; altitudeM: number }[];
}
