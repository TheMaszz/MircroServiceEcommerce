import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ResponseModel } from 'app/models/response.model';
import { StripeRequest, StripeResponse } from 'app/models/stripe.model';
import { environment } from 'environments/environment';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class PaymentService {
  private serviceUrl = 'apiendpoint/payment';

  constructor(private http: HttpClient) {}

  checkoutProducts(
    orderId: number,
    data: StripeRequest[]
  ): Observable<StripeResponse> {
    return this.http.post<StripeResponse>(
      `${environment.apiUrl}/${this.serviceUrl}/checkout/${orderId}`,
      data
    );
  }
}
