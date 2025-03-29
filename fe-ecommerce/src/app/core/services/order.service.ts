import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { OrderRequest } from 'app/models/order.model';
import { ResponseModel } from 'app/models/response.model';
import { environment } from 'environments/environment';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class OrderService {
  private serviceUrl = 'apiendpoint/order';

  constructor(private http: HttpClient) {}

  create(data: OrderRequest): Observable<ResponseModel> {
    return this.http.post<ResponseModel>(
      `${environment.apiUrl}/${this.serviceUrl}/create`,
      data
    );
  }

  getOrderById(id: number): Observable<ResponseModel> {
    return this.http.get<ResponseModel>(
      `${environment.apiUrl}/${this.serviceUrl}/${id}`
    );
  }
}
