import { inject } from '@angular/core';
import { CanActivateFn } from '@angular/router';
import { Router } from '@angular/router';

export const AuthGuard: CanActivateFn = (route, state) => {
  const router = inject(Router);
  const token = sessionStorage.getItem('jwt');
  if (token) {
    return true;
  } else {
    router.navigate(['/auth/signin']);
    return false;
  }
};
