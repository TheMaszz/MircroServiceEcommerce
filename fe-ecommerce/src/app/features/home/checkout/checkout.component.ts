import { CommonModule, Location } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MaterialModules } from 'app/core/modules/material.module';
import { ImageUrlPipe } from 'app/core/pipe/imageUrlPipe';
import { CartService } from 'app/core/services/cart.service';
import { OrderService } from 'app/core/services/order.service';
import { PaymentService } from 'app/core/services/payment.service';
import { UserService } from 'app/core/services/user.service';
import { CartGroup } from 'app/models/cart.model';
import { OrderRequest } from 'app/models/order.model';
import { ResponseModel } from 'app/models/response.model';
import { StripeRequest, StripeResponse } from 'app/models/stripe.model';
import { Address } from 'app/models/user.model';
import { ModalCheckoutComponent } from './components/modal-checkout/modal-checkout.component';

@Component({
  selector: 'app-checkout',
  standalone: true,
  imports: [CommonModule, ImageUrlPipe, MaterialModules, FormsModule, ModalCheckoutComponent],
  templateUrl: './checkout.component.html',
  styleUrl: './checkout.component.scss',
})
export class CheckoutComponent implements OnInit {
  constructor(
    private location: Location,
    private orderService: OrderService,
    private userService: UserService,
    private paymentService: PaymentService,
    private cartService: CartService,
    private dialog: MatDialog
  ) {}

  selectedItems: CartGroup[] = [];
  myAddress: Address[] = [];
  selectedAddress!: Address;

  ngOnInit(): void {
    window.alertLoading();
    this.selectedItems = JSON.parse(
      localStorage.getItem('checkoutItems') || '[]'
    );

    if (this.selectedItems.length === 0) {
      this.location.back();
    } else {
      this.userService.getMyAddress().subscribe((res) => {
        console.log(res.data);
        this.myAddress = res.data;
        this.selectedAddress = this.myAddress[0];
        console.log('my address: ', this.myAddress);
        window.closeLoading;
      });
    }
  }

  calcTotalPriceOfShop(cart: CartGroup): number {
    return cart.products.reduce((sum, product) => sum + product.totalPrice!, 0);
  }

  calcTotalPrice(): number {
    return this.selectedItems
      .flatMap((cart) => cart.products)
      .reduce((sum, product) => sum + product.totalPrice!, 0);
  }

  backNavigate() {
    this.location.back();
  }

  checkoutHandler(): void {
    if (this.selectedAddress === null || this.selectedAddress === undefined) {
      window.alertInfo('กรุณาเลือกที่อยู่การจัดส่ง');
      return;
    }

    const products = this.selectedItems
      .flatMap((cart) => cart.products)
      .map((product) => ({ product_id: product.id }));

    const normalizeData: OrderRequest = {
      address_id: this.selectedAddress.id,
      stage: 'Pending',
      total_amount: this.calcTotalPrice(),
      products: products,
    };

    this.orderService
      .create(normalizeData)
      .subscribe((orderRes: ResponseModel) => {
        console.log('orderRes: ', orderRes);
        if (orderRes.response_desc === 'success') {
          const normalizeProduct: StripeRequest[] = this.selectedItems
            .flatMap((cart) => cart.products)
            .map((product) => ({
              amount: product.price,
              quantity: product.qty,
              name: product.name,
              currency: 'THB',
            }));

          this.paymentService
            .checkoutProducts(orderRes.data.orderId, normalizeProduct)
            .subscribe((paymentRes: StripeResponse) => {
              console.log('paymentRes: ', paymentRes);
              if (paymentRes.status === 'SUCCESS') {
                localStorage.setItem("checkoutItems", "[]");
                this.cartService.removeSelected();

                this.dialog.open(
                  ModalCheckoutComponent,
                  {
                    disableClose: true,
                    width: '400px',
                    data: {paymentUrl: paymentRes.sessionUrl}
                  }
                )
              }
            });
        }
      });
  }
}
