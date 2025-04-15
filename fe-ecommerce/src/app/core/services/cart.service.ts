import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { CartGroup, CartItem } from 'app/models/cart.model';
import { ResponseModel } from 'app/models/response.model';
import { environment } from 'environments/environment';
import { BehaviorSubject, Observable, tap } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class CartService {
  private serviceUrl = 'apiendpoint/cart';

  constructor(private http: HttpClient) {}

  private cartItems = new BehaviorSubject<CartGroup[]>([]);

  cartItems$ = this.cartItems.asObservable();

  getCart(): Observable<CartGroup[]> {
    return this.http
      .get<CartGroup[]>(`${environment.apiUrl}/${this.serviceUrl}/get`)
      .pipe(
        tap(
          (cart: CartGroup[]) => {
            this.cartItems.next(cart);
          },
          (error) => {
            console.error('Error fetching cart:', error);
          }
        )
      );
  }

  addToCart(product: CartItem): Observable<ResponseModel> {
    return this.http.post<ResponseModel>(
      `${environment.apiUrl}/${this.serviceUrl}/add`,
      product
    );
  }

  updateCart(cart: CartGroup[]): Observable<ResponseModel> {
    return this.http.put<ResponseModel>(
      `${environment.apiUrl}/${this.serviceUrl}/update`,
      cart
    );
  }

  removeFromCart(productId: number): Observable<ResponseModel> {
    return this.http.delete<ResponseModel>(
      `${environment.apiUrl}/${this.serviceUrl}/remove/${productId}`
    );
  }

  clearCart(): Observable<ResponseModel> {
    return this.http.delete<ResponseModel>(
      `${environment.apiUrl}/${this.serviceUrl}/clear`
    );
  }

  updateQty(product: CartItem): Observable<ResponseModel> {
    return this.http.put<ResponseModel>(
      `${environment.apiUrl}/${this.serviceUrl}/update/product/qty/${product.id}?qty=${product.qty}`,
      {}
    );
  }

  updateSelectProduct(product: CartItem): Observable<ResponseModel> {
    return this.http.put<ResponseModel>(
      `${environment.apiUrl}/${this.serviceUrl}/update/selectProduct/${product.id}?selected=${product.selected}`,
      {}
    );
  }

  updateSelectAll(selected: boolean): Observable<ResponseModel> {
    return this.http.put<ResponseModel>(
      `${environment.apiUrl}/${this.serviceUrl}/update/selectAll?selected=${selected}`,
      {}
    );
  }

  updateSelectCart(cart: CartGroup): Observable<ResponseModel> {
    return this.http.put<ResponseModel>(
      `${environment.apiUrl}/${this.serviceUrl}/update/selectCart/${cart.created_by}?selected=${cart.selected}`,
      {}
    );
  }

  getCheckoutItems(): Observable<ResponseModel> {
    return this.http.get<ResponseModel>(
      `${environment.apiUrl}/${this.serviceUrl}/getCheckoutItems`
    );
  }

  removeCheckoutItems(): Observable<ResponseModel> {
    return this.http.put<ResponseModel>(
      `${environment.apiUrl}/${this.serviceUrl}/removeCheckoutItems`,
      {}
    );
  }
}
