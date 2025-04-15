import { CommonModule } from '@angular/common';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { MaterialModules } from 'app/core/modules/material.module';
import { ImageUrlPipe } from 'app/core/pipe/imageUrlPipe';
import { CartService } from 'app/core/services/cart.service';
import { CartGroup, CartItem } from 'app/models/cart.model';
import { Subject } from 'rxjs';

@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [MaterialModules, ImageUrlPipe, CommonModule, RouterLink],
  templateUrl: './cart.component.html',
})
export class CartComponent implements OnInit, OnDestroy {
  constructor(private cartService: CartService, private router: Router) {}

  private unsubscribe$ = new Subject<void>();

  carts: CartGroup[] = [];
  selectedItems: CartGroup[] = [];
  totalSelectedPrice: number = 0;
  totalSelectedItems: number = 0;

  ngOnInit(): void {
    window.alertLoading();
    this.cartService.getCart().subscribe();
    this.cartService.cartItems$.subscribe({
      next: (carts) => {
        this.carts = carts;
        this.calculateTotals();
        window.closeLoading();
      },
      error: (error) => {
        console.error('Error fetching cart:', error);
        window.closeLoading();
      },
    });
  }

  ngOnDestroy(): void {
    this.unsubscribe$.next();
    this.unsubscribe$.complete();
  }

  calculateTotals(): void {
    this.totalSelectedPrice = 0;
    this.totalSelectedItems = 0;
    this.selectedItems = [];

    const groupedItems = new Map<number, CartGroup>();

    this.carts.forEach((originalCart) => {
      let group = groupedItems.get(originalCart.created_by);
      if (!group) {
        group = {
          created_by: originalCart.created_by,
          created_user: originalCart.created_user,
          products: [],
          selected: false,
        };
        groupedItems.set(originalCart.created_by, group);
      }

      originalCart.products.forEach((product) => {
        if (product.selected) {
          this.totalSelectedPrice += product.totalPrice!;
          this.totalSelectedItems++;

          group!.products.push({ ...product });
        }
      });

      group.selected = group.products.length > 0;
    });

    this.selectedItems = Array.from(groupedItems.values()).filter(
      (group) => group.products.length > 0
    );
  }

  toggleProductSelection(cart: CartGroup, product: CartItem): void {
    product.selected = !product.selected;

    this.cartService.updateSelectProduct(product).subscribe({
      next: (response) => {
        console.log('Update response:', response);
        this.carts = response.data;
        this.calculateTotals();
      },
      error: (error) => {
        console.log('Error updating product selection:', error);
      },
    });
  }

  toggleCartSelection(cart: CartGroup): void {
    const newState = !cart.selected;
    cart.selected = newState;
    this.cartService.updateSelectCart(cart).subscribe({
      next: (response) => {
        console.log('Update response:', response);
        this.carts = response.data;
        this.calculateTotals();
      },
      error: (error) => {
        console.log('Error updating cart selection:', error);
      },
    });
  }

  toggleAllSelection(): void {
    const allSelected = this.isAllSelected();
    this.cartService.updateSelectAll(!allSelected).subscribe({
      next: (response) => {
        console.log('Update response:', response);
        this.carts = response.data;
        this.calculateTotals();
      },
      error: (error) => {
        console.log('Error updating all selection:', error);
      },
    });
  }

  isAllSelected(): boolean {
    if (this.carts.length === 0) return false;
    return this.carts.every((cart) =>
      cart.products.every((product) => product.selected)
    );
  }

  removeProduct(productId: number) {
    this.cartService.removeFromCart(productId).subscribe({
      next: (response) => {
        console.log('Remove response:', response);
        this.carts = response.data;
        this.calculateTotals();
        this.cartService.getCart().subscribe();
      },
      error: (error) => {
        console.log('Error removing product:', error);
      },
    });
  }

  quantityHandler(condition: string, product: CartItem) {
    if (condition === 'plus' && product.qtyInStock > product.qty) {
      product.qty++;
    }
    if (condition === 'minus' && product.qty > 1) {
      product.qty--;
    }

    product.totalPrice = product.price * product.qty;

    this.cartService.updateQty(product).subscribe({
      next: (response) => {
        console.log('Update response:', response);
        this.carts = response.data;
        this.calculateTotals();
      },
      error: (error) => {
        console.log('Error updating quantity:', error);
      },
    });
  }

  clearCart() {
    this.cartService.clearCart().subscribe({
      next: (response) => {
        console.log('Update response:', response);
        this.carts = response.data;
        this.calculateTotals();
        this.cartService.getCart().subscribe();
      },
      error: (error) => {
        console.log('Error clear cart:', error);
      },
    });
  }

  trackByProductId(index: number, product: CartItem): number {
    return product.id;
  }

  trackByCartId(index: number, cart: any): number {
    return cart.created_by;
  }

  checkoutHandler() {
    this.router.navigate(['/checkout']);
  }
}
