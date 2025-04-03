import { inject } from '@angular/core';
import {
  ResolveFn,
  ActivatedRouteSnapshot,
  RouterStateSnapshot,
} from '@angular/router';
import { OrderService } from 'app/core/services/order.service';
import { Observable, of } from 'rxjs';


export const getOrderById: ResolveFn<any> = (
  route: ActivatedRouteSnapshot,
  state: RouterStateSnapshot
): Observable<any> => {
  const id = route.paramMap.get('id');
  if (id) {
    return inject(OrderService).getOrderById(Number.parseInt(id));
  } else {
    return of(null);
  }
};
