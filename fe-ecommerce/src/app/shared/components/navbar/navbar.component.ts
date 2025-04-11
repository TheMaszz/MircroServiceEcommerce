import { CommonModule } from '@angular/common';
import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { MatAutocompleteTrigger } from '@angular/material/autocomplete';
import {
  ActivatedRoute,
  Router,
  RouterLink,
  RouterModule,
} from '@angular/router';
import { MaterialModules } from 'app/core/modules/material.module';
import { ImageUrlPipe } from 'app/core/pipe/imageUrlPipe';
import { CartService } from 'app/core/services/cart.service';
import { ProductService } from 'app/core/services/product.service';
import { UserService } from 'app/core/services/user.service';
import { CartGroup, CartItem } from 'app/models/cart.model';
import { UserModel } from 'app/models/user.model';
import {
  debounceTime,
  distinctUntilChanged,
  map,
  Observable,
  startWith,
  Subject,
  switchMap,
} from 'rxjs';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [
    MaterialModules,
    ReactiveFormsModule,
    CommonModule,
    RouterLink,
    ImageUrlPipe,
  ],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.scss',
})
export class NavbarComponent implements OnInit, OnDestroy {
  constructor(
    private productService: ProductService,
    private router: Router,
    private route: ActivatedRoute,
    private cartService: CartService,
    private userService: UserService
  ) {}

  private unsubscribe$ = new Subject<void>();

  @ViewChild(MatAutocompleteTrigger) autocomplete!: MatAutocompleteTrigger;

  searchProduct = new FormControl('');
  filteredOptions$!: Observable<any[]>;
  cartItemCount = 0;
  cartItems: CartItem[] = [];
  currentUser!: UserModel;

  ngOnInit() {
    this.route.queryParams.subscribe((params) => {
      this.searchProduct.setValue(params['search'] || '');
    });

    this.userService.getMyProfile().subscribe();
    this.userService.userDetails$.subscribe({
      next: (response) => {
        this.currentUser = response!;
      },
      error: (error) => {
        console.log('res Error:', error);
      },
    });

    this.filteredOptions$ = this.searchProduct.valueChanges.pipe(
      startWith(''),
      debounceTime(300),
      distinctUntilChanged(),
      switchMap((value) => this.getAutoCompleteOptions(value || ''))
    );

    this.cartService.cartItems$.subscribe((carts) => {
      this.cartItemCount = carts.reduce(
        (sum: number, group: CartGroup) => sum + group.products.length,
        0
      );
      this.cartItems = carts.reduce((acc: CartItem[], group: CartGroup) => {
        return acc.concat(group.products);
      }, []);
    });
    console.log('cartItems:', this.cartItems);
  }

  ngOnDestroy(): void {
    this.unsubscribe$.next();
    this.unsubscribe$.complete();
  }

  getAutoCompleteOptions(search: string): Observable<any[]> {
    return this.productService
      .getAutoCompleteProduct({ search: search, limit: 10 })
      .pipe(map((response) => response.data));
  }

  onSearchSelected(event: any) {
    const selectedValue = event.option.value;
    this.router.navigateByUrl(
      `/products?search=${encodeURIComponent(selectedValue)}&page=1`
    );
  }

  onSearchEnter(event: any): void {
    if (event.code === 'Enter' || event.code === 'NumpadEnter') {
      event.preventDefault();
      let keyword = this.searchProduct.value?.trim().toLowerCase();
      this.autocomplete.closePanel();
      this.searchProduct.setValue(keyword || '');
      this.router.navigateByUrl(`/products?search=${keyword}&page=1`);
    }
  }

  navigateToCart() {
    this.router.navigate(['/carts']);
  }

  logout() {
    sessionStorage.removeItem('jwt');
    this.router.navigate(['/auth/signin']);
  }
}
