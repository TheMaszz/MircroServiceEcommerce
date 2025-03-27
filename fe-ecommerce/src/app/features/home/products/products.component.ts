import { Component, OnInit, OnDestroy } from '@angular/core';
import { ProductCardComponent } from '../../../shared/components/product-card/product-card.component';
import { CommonModule } from '@angular/common';
import { PaginationComponent } from '../../../shared/components/pagination/pagination.component';
import { ActivatedRoute, Router } from '@angular/router';
import { Subject, Subscription } from 'rxjs';
import { ProductService } from 'app/core/services/product.service';

@Component({
  selector: 'app-products',
  standalone: true,
  imports: [ProductCardComponent, CommonModule, PaginationComponent],
  templateUrl: './products.component.html',
  styleUrl: './products.component.scss',
})
export class ProductsComponent implements OnInit, OnDestroy {
  constructor(
    private productService: ProductService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  private unsubscribe$ = new Subject<void>();

  products: any[] = [];
  currentPage: number = 1;
  totalPages: number = 0;
  searchProduct: string = '';

  ngOnInit(): void {
    this.route.queryParams.subscribe((params) => {
      this.searchProduct = params['search'] || '';
      this.currentPage = params['page'] ? Number(params['page']) : 1;

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
      .getProducts({
        search: this.searchProduct,
        page_number: this.currentPage,
        page_size: 10,
        sort: 'created_at',
        sort_type: 'desc',
      })
      .subscribe({
        next: (response) => {
          this.products = response.data;
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
      this.router.navigate(['/products/'], { queryParams: updatedParams });
    });
  }
}
