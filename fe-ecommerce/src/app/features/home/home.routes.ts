import { Routes } from '@angular/router';
import { HomeComponent } from './home.component';
import { ProductsComponent } from './products/products.component';
import { ProductComponent } from './product/product.component';
import { getProductById } from './home.resolver';
import { CartComponent } from './cart/cart.component';
import { CheckoutComponent } from './checkout/checkout.component';
import { CheckoutSuccessComponent } from './checkout-success/checkout-success.component';
import { CheckoutCancelComponent } from './checkout-cancel/checkout-cancel.component';
import { getMyAddress } from '../user/user.resolver';

export const HOME_ROUTES: Routes = [
  {
    path: '',
    component: HomeComponent,
    children: [
      { path: '', redirectTo: 'products', pathMatch: 'full' },
      { path: 'products', component: ProductsComponent },
      {
        path: 'products/:id',
        component: ProductComponent,
        resolve: { product: getProductById },
      },
      { path: 'carts', component: CartComponent },
      {
        path: 'checkout',
        component: CheckoutComponent,
        resolve: { address: getMyAddress },
      },
      {
        path: 'checkout/success',
        component: CheckoutSuccessComponent,
      },
      {
        path: 'checkout/cancel',
        component: CheckoutCancelComponent,
      },
    ],
  },
];
