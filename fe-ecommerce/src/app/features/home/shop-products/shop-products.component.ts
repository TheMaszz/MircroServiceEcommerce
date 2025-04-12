import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ProductModel } from 'app/models/product.model';
import { ShopDetail } from 'app/models/user.model';
import { Subject } from 'rxjs';
import { ProductCardComponent } from '../../../shared/components/product-card/product-card.component';
import { PaginationComponent } from '../../../shared/components/pagination/pagination.component';
import { CommonModule } from '@angular/common';
import { ProductService } from 'app/core/services/product.service';
import { ImageUrlPipe } from 'app/core/pipe/imageUrlPipe';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-shop-products',
  standalone: true,
  imports: [
    ProductCardComponent,
    PaginationComponent,
    CommonModule,
    ImageUrlPipe,
    FormsModule,
  ],
  templateUrl: './shop-products.component.html',
})
export class ShopProductsComponent implements OnInit, OnDestroy {
  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private productService: ProductService
  ) {}

  private unsubscribe$ = new Subject<void>();

  products!: ProductModel[];
  shopDetail!: ShopDetail;

  currentPage: number = 1;
  totalPages: number = 0;
  userId!: number;
  searchTerm: string = '';

  ngOnInit(): void {
    this.userId = Number(this.route.snapshot.paramMap.get('userId'));
    this.route.queryParams.subscribe((params) => {
      this.currentPage = params['page'] ? Number(params['page']) : 1;
      this.searchTerm = params['search'] || '';

      this.fetchProducts();
    });
  }

  ngOnDestroy(): void {
    this.unsubscribe$.next();
    this.unsubscribe$.complete();
  }

  fetchProducts() {
    window.alertLoading();

    this.productService
      .getShopProductsByUserId(this.userId, {
        search: this.searchTerm,
        page_number: this.currentPage,
        page_size: 10,
        sort: 'created_at',
        sort_type: 'desc',
      })
      .subscribe({
        next: (response) => {
          this.products = response.data.products;
          this.shopDetail = response.data.shopDetail;
          this.currentPage = response.paginate.page;
          this.totalPages = Math.ceil(
            response.paginate.total / response.paginate.limit
          );
          window.closeLoading();
        },
        error: (error) => {
          console.error('Error fetching products:', error);
          window.closeLoading();
        },
      });
  }

  onPageChanged(page: number) {
    this.route.queryParams.subscribe((params) => {
      const updatedParams = {
        ...params,
        page: page,
      };
      this.router.navigate(['/shop/', this.userId], {
        queryParams: updatedParams,
      });
    });
  }

  onSearchTermChange(event: any) {
    this.searchTerm = event.target.value;
  }

  onSearch() {
    this.currentPage = 1;

    this.router.navigate(['/shop/', this.userId], {
      queryParams: {
        page: this.currentPage,
        search: this.searchTerm,
      },
    });
  }

  onClearSearch() {
    this.searchTerm = '';
    this.currentPage = 1;

    this.router.navigate(['/shop/', this.userId], {
      queryParams: {
        page: this.currentPage,
        search: this.searchTerm,
      },
    });
  }
}
