import { Routes } from '@angular/router';
import { UserComponent } from './user.component';
import { AccountComponent } from './account/account.component';
import { OrdersComponent } from './orders/orders.component';
import { OrderComponent } from './order/order.component';
import { getMyProfile, getOrderById } from './user.resolver';
import { AddressComponent } from './account/address/address.component';
import { ChangePasswordComponent } from './account/change-password/change-password.component';
import { ProfileComponent } from './account/profile/profile.component';

export const USER_ROUTES: Routes = [
  {
    path: '',
    component: UserComponent,
    children: [
      { path: '', redirectTo: 'account/profile', pathMatch: 'full' },
      {
        path: 'account',
        component: AccountComponent,
        children: [
          { path: '', redirectTo: 'profile', pathMatch: 'full' }, 
          {
            path: 'profile',
            component: ProfileComponent,
            resolve: { profile: getMyProfile },
          },
          {
            path: 'address',
            component: AddressComponent,
          },
          {
            path: 'change-password',
            component: ChangePasswordComponent,
          },
        ],
      },
      { path: 'orders', component: OrdersComponent },
      {
        path: 'orders/:id',
        component: OrderComponent,
        resolve: { order: getOrderById },
      },
    ],
  },
];
