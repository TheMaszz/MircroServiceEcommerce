import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'environments/environment';
import {
  AuthResponse,
  SignInRequest,
  SignUpRequest,
} from 'app/models/auth.model';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private serviceUrl = 'apiendpoint/auth';

  constructor(private http: HttpClient) {}

  signup(data: SignUpRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.serviceUrl}/signup`, data);
  }

  signin(data: SignInRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(
      `${environment.apiUrl}/${this.serviceUrl}/signin`,
      data
    );
  }
}
