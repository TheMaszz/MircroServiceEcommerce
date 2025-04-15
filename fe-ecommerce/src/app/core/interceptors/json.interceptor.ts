import { HttpInterceptorFn } from '@angular/common/http';
import { catchError } from 'rxjs/operators';
import { throwError } from 'rxjs';

export const jsonInterceptor: HttpInterceptorFn = (req, next) => {
  const jsonReq = req.clone({
    setHeaders: {
      'Content-Type': 'application/json',
      'Accept': 'application/json'
    }
  });

  return next(jsonReq).pipe(
    catchError(error => {
      if (typeof error.error === 'string') {
        try {
          error.error = JSON.parse(error.error);
        } catch {
          error.error = { message: error.error };
        }
      }
      return throwError(() => error);
    })
  );
};