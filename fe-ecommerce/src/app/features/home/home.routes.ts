import { Routes } from '@angular/router';
import { HomeComponent } from './home.component';
import { ProductsComponent } from './products/products.component';
import { ProductComponent } from './product/product.component';
import { getOrderById, getProductById } from './home.resolver';
import { CartComponent } from './cart/cart.component';
import { OrdersComponent } from './orders/orders.component';
import { OrderComponent } from './order/order.component';
import { CheckoutComponent } from './checkout/checkout.component';

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
      { path: 'orders', component: OrdersComponent},
      {
        path: 'orders/:id',
        component: OrderComponent,
        resolve: { order: getOrderById }
      },
      {
        path: 'checkout',
        component: CheckoutComponent
      }
    ],
  },
];
