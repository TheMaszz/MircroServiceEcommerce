import { CommonModule, Location } from '@angular/common';
import {
  Component,
  ElementRef,
  OnDestroy,
  OnInit,
  QueryList,
  ViewChild,
  ViewChildren,
} from '@angular/core';
import { MaterialModules } from 'app/core/modules/material.module';
import { ActivatedRoute } from '@angular/router';
import { ImageUrlPipe } from 'app/core/pipe/imageUrlPipe';
import { CartService } from 'app/core/services/cart.service';
import { Subject } from 'rxjs';
import { ProductModel } from 'app/models/product.model';
import { CartItem } from 'app/models/cart.model';

@Component({
  selector: 'app-product',
  standalone: true,
  imports: [MaterialModules, CommonModule, ImageUrlPipe],
  templateUrl: './product.component.html',
  styleUrl: './product.component.scss',
})
export class ProductComponent implements OnInit, OnDestroy {
  constructor(
    private activatedRoute: ActivatedRoute,
    private cartService: CartService,
    private location: Location
  ) {}

  private unsubscribe$ = new Subject<void>();

  @ViewChildren('thumbnailItem') thumbnailItems!: QueryList<ElementRef>;
  @ViewChild('thumbnailContainer') thumbnailContainer!: ElementRef;

  currentIndex = 0;
  quantity = 1;
  product!: ProductModel;

  ngOnInit(): void {
    window.alertLoading();
    this.activatedRoute.data.subscribe(({ product }) => {
      this.product = product.data;
      window.closeLoading();
    });
    console.log('product: ', this.product);
  }

  ngOnDestroy(): void {
    this.unsubscribe$.next();
    this.unsubscribe$.complete();
  }

  nextHandler() {
    if (this.currentIndex < this.product.product_image.length - 1) {
      this.currentIndex++;
      this.scrollToThumbnail(this.currentIndex);
    }
  }

  prevHandler() {
    if (this.currentIndex > 0) {
      this.currentIndex--;
      this.scrollToThumbnail(this.currentIndex);
    }
  }

  indexHandler(index: number) {
    this.currentIndex = index;
    this.scrollToThumbnail(index);
  }

  scrollToThumbnail(index: number) {
    const items = this.thumbnailItems.toArray();
    if (items[index]) {
      items[index].nativeElement.scrollIntoView({
        behavior: 'smooth',
        inline: 'center',
      });
    }
  }

  quantityHandler(condition: 'plus' | 'minus') {
    if(condition==='plus' && this.product.qty > this.quantity){
      this.quantity++;
    }
    if(condition==='minus' && this.quantity > 1){
      this.quantity--;
    }
  }

  addToCart() {
    const newProduct: CartItem = {
      id: this.product.id,
      name: this.product.name,
      price: this.product.price,
      qtyInStock: this.product.qty,
      qty: this.quantity,
      imageUrl: this.product.product_image[0].image_url,
      created_by: this.product.created_by,
      created_user: this.product.created_user,
      selected: false
    }
    this.cartService.addToCart(newProduct);
  }

  backNavigate(){
    this.location.back();
  }
}
