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

export interface ForgotPassRequest {
  email: string;
}

export interface ResetPassRequest {
  token_reset_password: string;
  new_password?: string;
}

export interface ChangePasswordRequest {
  old_password: string;
  new_password: string;
}