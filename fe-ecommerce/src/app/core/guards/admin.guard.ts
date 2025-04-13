import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

export const AdminGuard: CanActivateFn = (route, state) => {
  const router = inject(Router);
  const user = JSON.parse(sessionStorage.getItem('user') || '{}');

  if (user && user.role === 1) {  // check if user is admin (role 1)
    return true;
  } else {
    window.alertFail("คุณไม่มีสิทธิ์เข้าถึงหน้านี้");
    router.navigate(['/']);
    return false;
  }
};
