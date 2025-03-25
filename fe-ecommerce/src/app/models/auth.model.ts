export interface SignUpRequest {
  email: string;
  username: string;
  password: string;
}

export interface SignInRequest {
  usernameOrEmail: string;
  password: string;
}

export interface AuthResponse {
  id: number;
  username: string;
  email: string;
  token: string;
}