import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';

export const jwtInterceptor: HttpInterceptorFn = (req, next) => {
  const router = inject(Router);

  const token = sessionStorage.getItem('jwt');
  const clonedReq = token
    ? req.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`,
        },
      })
    : req;

  return next(clonedReq).pipe(
    catchError((error) => {
      if (error.status === 401) {
        sessionStorage.removeItem("jwt");
        router.navigate(['/auth/signin']); 
      }
      return throwError(() => error);
    })
  );
};
