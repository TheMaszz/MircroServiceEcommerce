import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ResponseModel } from 'app/models/response.model';
import { Address, UserModel } from 'app/models/user.model';
import { environment } from 'environments/environment';
import { BehaviorSubject, catchError, Observable, tap, throwError } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private serviceUrl = 'apiendpoint/user';

  constructor(private http: HttpClient) {}

  private userDetails = new BehaviorSubject<UserModel | null>(null);
  private myAddress = new BehaviorSubject<Address[] | null>(null);

  userDetails$ = this.userDetails.asObservable();
  myAddress$ = this.myAddress.asObservable();

  getMyAddress(): Observable<ResponseModel<Address[]>> {
    return this.http
      .get<ResponseModel<Address[]>>(
        `${environment.apiUrl}/${this.serviceUrl}/address/my`
      )
      .pipe(
        tap((res: ResponseModel) => {
          this.myAddress.next(res.data);
        }),
        catchError((error: any) => {
          console.error('Error:', error);
          return throwError(() => error);
        })
      );
  }

  getMyProfile(): Observable<ResponseModel<UserModel>> {
    return this.http
      .get<ResponseModel<UserModel>>(
        `${environment.apiUrl}/${this.serviceUrl}/my`
      )
      .pipe(
        tap((res: ResponseModel) => {
          this.userDetails.next(res.data);
        }),
        catchError((error: any) => {
          console.error('Error:', error);
          return throwError(() => error);
        })
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

  defaultAddress(id: number): Observable<ResponseModel> {
    return this.http.put<ResponseModel>(
      `${environment.apiUrl}/${this.serviceUrl}/address/defaultAddress/${id}`,
      {}
    );
  }

  refreshAddress() {
    this.getMyAddress().subscribe();
  }

  getDashboardData(): Observable<ResponseModel> {
    return this.http.get<ResponseModel>(
      `${environment.apiUrl}/${this.serviceUrl}/admin/dashboard`
    );
  }
}
