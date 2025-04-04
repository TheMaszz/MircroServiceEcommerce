import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ResponseModel } from 'app/models/response.model';
import { Address } from 'app/models/user.model';
import { environment } from 'environments/environment';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private serviceUrl = 'apiendpoint/user';

  constructor(private http: HttpClient) {}

  getMyAddress(): Observable<ResponseModel> {
    return this.http.get<ResponseModel>(
      `${environment.apiUrl}/${this.serviceUrl}/address/my`
    );
  }

  getMyProfile(): Observable<ResponseModel> {
    return this.http.get<ResponseModel>(
      `${environment.apiUrl}/${this.serviceUrl}/my`
    );
  }

  updateProfile(id: number, formData: any): Observable<ResponseModel> {
    return this.http.put<ResponseModel>(
      `${environment.apiUrl}/${this.serviceUrl}/update/${id}`,
      formData
    );
  }

  createAddress(data: Address): Observable<ResponseModel> {
    return this.http.post<ResponseModel>(
      `${environment.apiUrl}/${this.serviceUrl}/address/create`,
      data
    );
  }

  updateAddress(id: number, data: Address): Observable<ResponseModel> {
    return this.http.put<ResponseModel>(
      `${environment.apiUrl}/${this.serviceUrl}/address/update/${id}`,
      data
    );
  }

  deleteAddress(id: number): Observable<ResponseModel> {
    return this.http.delete<ResponseModel>(
      `${environment.apiUrl}/${this.serviceUrl}/address/delete/${id}`
    );
  }

  defaultAddress(id: number): Observable<ResponseModel>{
    return this.http.put<ResponseModel>(
      `${environment.apiUrl}/${this.serviceUrl}/address/defaultAddress/${id}`,
      {}
    ); 
  }
}
