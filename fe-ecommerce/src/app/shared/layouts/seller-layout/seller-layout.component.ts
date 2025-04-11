import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import {
  NavigationEnd,
  Router,
  RouterModule,
  RouterOutlet,
} from '@angular/router';
import { MaterialModules } from 'app/core/modules/material.module';
import { NavbarComponent } from 'app/shared/components/navbar/navbar.component';

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
  selector: 'app-seller-layout',
  standalone: true,
  imports: [
    RouterOutlet,
    NavbarComponent,
    RouterModule,
    MaterialModules,
    CommonModule,
  ],
  templateUrl: './seller-layout.component.html',
})
export class SellerLayoutComponent {
  constructor(private router: Router) {
    this.router.events.subscribe((event) => {
      if (event instanceof NavigationEnd) {
        this.updateDropdownState(event.url);
      }
    });
  }

  navSide: NavSide[] = [
    {
      path: '/seller/products',
      icon: 'shopping_cart',
      text: 'สินค้าของฉัน',
    },
    {
      path: '/seller/orders',
      icon: 'shopping_cart',
      text: 'การสั่งซื้อของฉัน',
    },
  ];

  toggleDropdown(nav: NavSide) {
    if (nav.children) {
      nav.isOpen = !nav.isOpen;
    }
  }


  private updateDropdownState(currentUrl: string) {
    this.navSide.forEach((nav) => {
      if (nav.children) {
        nav.isOpen = nav.children.some((child) =>
          currentUrl.startsWith(child.path)
        );
      }
    });
  }
}
