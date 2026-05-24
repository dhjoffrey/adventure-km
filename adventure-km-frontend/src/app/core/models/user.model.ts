export interface UserResponse {
  id: number;
  username: string;
  avatarSpriteId: number;
  role: string;
}

export interface UserLevelResponse {
  userId: number;
  username: string;
  avatarSpriteId: number;
  totalKm: number;
  totalElevationM: number;
  adventureCount: number;
  rpgScore: number;
  level: number;
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
  invitationToken: string;
}
