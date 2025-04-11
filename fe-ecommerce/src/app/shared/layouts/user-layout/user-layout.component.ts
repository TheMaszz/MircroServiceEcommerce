import { Component } from '@angular/core';
import { NavigationEnd, Router, RouterModule, RouterOutlet } from '@angular/router';
import { NavbarComponent } from '../../components/navbar/navbar.component';
import { MaterialModules } from 'app/core/modules/material.module';
import { CommonModule } from '@angular/common';
interface NavChild {
  path: string;
  text: string;
}
interface NavSide {
  path: string;
  icon: string;
  text: string;
  isOpen?: boolean;
  children?: NavChild[];
}
@Component({
  selector: 'app-user-layout',
  standalone: true,
  imports: [
    RouterOutlet,
    NavbarComponent,
    RouterModule,
    MaterialModules,
    CommonModule,
  ],
  templateUrl: './user-layout.component.html',
})
export class UserLayoutComponent {
  
  constructor(private router: Router) {
    this.router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        this.updateDropdownState(event.url);
      }
    });
  }

  navSide: NavSide[] = [
    {
      path: '/user/account',
      icon: 'person',
      text: 'บัญชีของฉัน',
      isOpen: false, 
      children: [
        { path: '/user/account/profile', text: 'ประวัติ' },
        { path: '/user/account/address', text: 'ที่อยู่' },
        { path: '/user/account/change-password', text: 'เปลี่ยนรหัสผ่าน' },
      ],
    },
    {
      path: '/user/orders',
      icon: 'shopping_cart',
      text: 'การซื้อของฉัน',
    },
  ];

  toggleDropdown(nav: NavSide) {
    if (nav.children) {
      nav.isOpen = !nav.isOpen;
    }
  }

  private updateDropdownState(currentUrl: string) {
    this.navSide.forEach(nav => {
      if (nav.children) {
        nav.isOpen = nav.children.some(child => currentUrl.startsWith(child.path));
      }
    });
  }
}
