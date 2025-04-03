import { Routes } from '@angular/router';
import { UserComponent } from './user.component';
import { AccountComponent } from './account/account.component';
import { OrdersComponent } from './orders/orders.component';
import { OrderComponent } from './order/order.component';
import { getOrderById } from './user.resolver';



export const USER_ROUTES: Routes = [
  {
    path: '',
    component: UserComponent,
    children: [
      { path: '', redirectTo: 'account', pathMatch: 'full' },
      { path: 'account', component: AccountComponent },
      { path: 'orders', component: OrdersComponent},
      {
        path: 'orders/:id',
        component: OrderComponent,
        resolve: { order: getOrderById }
      },
      
    ],
  },
];
