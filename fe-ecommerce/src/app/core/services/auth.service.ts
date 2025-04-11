import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'environments/environment';
import {
  AuthResponse,
  ChangePasswordRequest,
  ForgotPassRequest,
  ResetPassRequest,
  SignInRequest,
  SignUpRequest,
} from 'app/models/auth.model';
import { ResponseModel } from 'app/models/response.model';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private serviceUrl = 'apiendpoint/auth';

  constructor(private http: HttpClient) {}

  signup(data: SignUpRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(
      `${environment.apiUrl}/${this.serviceUrl}/signup`, 
      data
    );
  }

  signin(data: SignInRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(
      `${environment.apiUrl}/${this.serviceUrl}/signin`,
      data
    );
  }

  sendTokenResetPassword(data: ForgotPassRequest): Observable<ResponseModel> {
    return this.http.post<ResponseModel>(
      `${environment.apiUrl}/${this.serviceUrl}/send-token-reset-password`,
      data
    );
  }

  resendTokenResetPassword(data: ForgotPassRequest): Observable<ResponseModel> {
    return this.http.post<ResponseModel>(
      `${environment.apiUrl}/${this.serviceUrl}/resend-token-reset-password`,
      data
    );
  }

  resetPassword(data: ResetPassRequest): Observable<ResponseModel> {
    return this.http.put<ResponseModel>(
      `${environment.apiUrl}/${this.serviceUrl}/reset-password`,
      data
    );
  }

  changePassword(data: ChangePasswordRequest): Observable<ResponseModel>{
    return this.http.put<ResponseModel>(
      `${environment.apiUrl}/${this.serviceUrl}/change-password`,
      data
    );
  }
}
