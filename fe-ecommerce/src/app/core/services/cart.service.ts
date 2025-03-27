import { Injectable } from '@angular/core';
import { CartGroup, CartItem } from 'app/models/cart.model';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class CartService {
  private cartKey = 'cart';
  private cartItems = new BehaviorSubject<CartGroup[]>(
    this.getCartFromStorage()
  );

  cartItems$ = this.cartItems.asObservable();

  constructor() {}

  private getCartFromStorage(): CartGroup[] {
    return JSON.parse(localStorage.getItem(this.cartKey) || '[]');
  }

  updateCart(cart: CartGroup[]): void {
    localStorage.setItem(this.cartKey, JSON.stringify(cart));
    this.cartItems.next(cart);
  }

  addToCart(product: CartItem): void {
    let cart = this.getCartFromStorage();

    let createdByGroup = cart.find(
      (group: CartGroup) => group.created_by === product.created_by
    );

    if (!createdByGroup) {
      createdByGroup = {
        created_by: product.created_by,
        created_user: product.created_user,
        products: [],
        selected: false,
      };
      cart.push(createdByGroup);
    }

    const productIndex = createdByGroup.products.findIndex(
      (item: CartItem) => item.id === product.id
    );

    if (productIndex !== -1) {
      createdByGroup.products[productIndex].qty += product.qty;
      createdByGroup.products[productIndex].totalPrice =
        createdByGroup.products[productIndex].qty *
        createdByGroup.products[productIndex].price;
    } else {
      product.totalPrice = product.qty * product.price;
      createdByGroup.products.push(product);
    }

    this.updateCart(cart);
  }

  removeFromCart(productId: number): void {
    let cart = this.getCartFromStorage()
      .map((group: CartGroup) => {
        group.products = group.products.filter(
          (item: CartItem) => item.id !== productId
        );
        return group;
      })
      .filter((group: CartGroup) => group.products.length > 0);

    this.updateCart(cart);
  }

  clearCart(): void {
    this.updateCart([]);
  }

  getCartLength(): number {
    return this.cartItems.value.reduce(
      (sum: number, group: CartGroup) => sum + group.products.length,
      0
    );
  }

  updateQty(product: CartItem) {
    let cart = this.getCartFromStorage();

    let createdByGroup = cart.find(
      (group: CartGroup) => group.created_by === product.created_by
    );

    if (!createdByGroup) {
      createdByGroup = {
        created_by: product.created_by,
        created_user: product.created_user,
        products: [],
        selected: false,
      };
      cart.push(createdByGroup);
    }

    const productIndex = createdByGroup.products.findIndex(
      (item: CartItem) => item.id === product.id
    );

    if (productIndex !== -1) {
      createdByGroup.products[productIndex].qty = product.qty;
      createdByGroup.products[productIndex].totalPrice =
        createdByGroup.products[productIndex].qty *
        createdByGroup.products[productIndex].price;
    } else {
      product.totalPrice = product.qty * product.price;
      createdByGroup.products.push(product);
    }
    this.updateCart(cart);
  }
}
