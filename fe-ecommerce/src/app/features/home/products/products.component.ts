import { Component, OnInit, OnDestroy } from '@angular/core';
import { ProductCardComponent } from "../../../shared/components/product-card/product-card.component";
import { HomeService } from '../home.service';
import { CommonModule } from '@angular/common';
import { PaginationComponent } from "../../../shared/components/pagination/pagination.component";
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-products',
  standalone: true,
  imports: [ProductCardComponent, CommonModule, PaginationComponent],
  templateUrl: './products.component.html',
  styleUrl: './products.component.scss'
})
export class ProductsComponent implements OnInit, OnDestroy {
  products: any[] = [];
  currentPage: number = 1;
  totalPages: number = 0;
  searchProduct: string = "";
  
  private paramsSubscription: Subscription | null = null;

  constructor(
    private homeService: HomeService, 
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.paramsSubscription = this.route.queryParams.subscribe((params) => {
      this.searchProduct = params['search'] || '';
      this.currentPage = params['page'] ? Number(params['page']) : 1;

      this.fetchProducts();
    });
  }


  fetchProducts() {
    window.alertLoading();

    this.homeService.getProducts({
      search: this.searchProduct,
      page_number: this.currentPage,
      page_size: 10,
      sort: 'created_at',
      sort_type: 'desc',
    }).subscribe({
      next: (response) => {
        this.products = response.data;
        this.currentPage = response.paginate.page;
        this.totalPages = Math.ceil(response.paginate.total / response.paginate.limit);
        window.closeLoading();
      },
      error: (error) => {
        console.error('Error fetching products:', error);
        window.closeLoading();
      }
    });
  }

  onPageChanged(page: number) {
    this.route.queryParams.subscribe(params => {
      const updatedParams = { 
        ...params, 
        page: page 
      };
      this.router.navigate(["/products/"], {queryParams: updatedParams});
    });
  }

  ngOnDestroy(): void {
    if (this.paramsSubscription) {
      this.paramsSubscription.unsubscribe();
    }
  }
}