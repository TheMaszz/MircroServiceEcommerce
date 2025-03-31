import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { CheckOutDTO, StripeResponse } from 'app/models/stripe.model';
import { environment } from 'environments/environment';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class PaymentService {
  private serviceUrl = 'apiendpoint/payment';

  constructor(private http: HttpClient) {}

  checkoutProducts(
    data: CheckOutDTO
  ): Observable<StripeResponse> {
    return this.http.post<StripeResponse>(
      `${environment.apiUrl}/${this.serviceUrl}/checkout`,
      data
    );
  }
}
