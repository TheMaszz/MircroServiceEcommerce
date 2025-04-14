import { CommonModule } from '@angular/common';
import { Component, OnInit, ViewChild } from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { MaterialModules } from 'app/core/modules/material.module';
import { ImageUrlPipe } from 'app/core/pipe/imageUrlPipe';
import { ProductService } from 'app/core/services/product.service';
import { ModalProductComponent } from 'app/features/seller/products/components/modal-product/modal-product.component';
import { ProductModel } from 'app/models/product.model';
import { map, merge, Subject, switchMap, takeUntil } from 'rxjs';

@Component({
  selector: 'app-products',
  standalone: true,
  imports: [MaterialModules, CommonModule, ImageUrlPipe, ReactiveFormsModule],
  templateUrl: './products.component.html',
})
export class ProductsComponent implements OnInit {
  constructor(
    private productService: ProductService,
    private dialog: MatDialog
  ) {}

  private unsubscribe$ = new Subject<void>();

  searchInputControl: FormControl = new FormControl('');
  displayedColumns: string[] = [
    'id',
    'img',
    'name',
    'price',
    'qty',
    'created_at',
    'created_user',
    'updated_at',
    'updated_user',
    'actions',
  ];
  pageSizeOptions: number[] = [10, 20, 50, 100];
  pagination: any;
  isLoading: boolean = false;
  searching: boolean = false;

  dataSource = new MatTableDataSource<ProductModel>();
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  ngOnInit(): void {
    this.isLoading = true;
    this.loadProducts();
  }

  loadProducts(params?: any) {
    this.isLoading = true;
    this.productService
      .getProducts(params)
      .pipe(takeUntil(this.unsubscribe$))
      .subscribe({
        next: (response) => {
          if (response) {
            this.dataSource.data = response.data;
            this.pagination = response.paginate;
          }
          this.isLoading = false;
        },
        error: (error) => {
          console.error('Error fetching products:', error);
          this.isLoading = false;
        },
      });
  }

  ngAfterViewInit() {
    this.sort.sortChange.pipe(takeUntil(this.unsubscribe$)).subscribe(() => {
      this.paginator.pageIndex = 0;
    });

    merge(this.sort.sortChange, this.paginator.page)
      .pipe(
        takeUntil(this.unsubscribe$),
        switchMap(() => {
          this.isLoading = true;
          let params = this.onGetParams();
          return this.productService.getMyProducts(params);
        }),
        map((response) => {
          if (response) {
            this.dataSource.data = response.data;
            this.pagination = response.paginate;
          }
          this.isLoading = false;
        })
      )
      .subscribe();
  }

  ngOnDestroy(): void {
    this.unsubscribe$.next();
    this.unsubscribe$.complete();
  }

  onGetParams() {
    let sortActive = this.sort.active ? this.sort.active : 'created_at';
    let sortDirection = this.sort.direction ? this.sort.direction : 'desc';

    if (!this.sort.direction) {
      sortActive = 'created_at';
      sortDirection = 'desc';
    }

    let params: any = {
      sort: sortActive,
      sort_type: sortDirection,
      page_number: this.paginator?.pageIndex + 1 || 1,
      page_size: this.paginator?.pageSize || 10,
    };

    if (this.searching && this.searchInputControl.value) {
      params.search = this.searchInputControl.value.trim().toLowerCase();
    }

    return params;
  }

  onGetSearch(keyword: string) {
    if (!keyword) {
      this.searching = false;
    } else {
      this.searching = true;
    }

    this.isLoading = true;
    const params = {
      ...this.onGetParams(),
      page_number: 1,
    };

    this.productService
      .getMyProducts(params)
      .pipe(takeUntil(this.unsubscribe$))
      .subscribe({
        next: (response) => {
          if (response) {
            this.dataSource.data = response.data;
            this.pagination = response.paginate;
          }
          this.isLoading = false;
        },
        error: (error) => {
          console.error('Error fetching products:', error);
          this.isLoading = false;
        },
      });
  }

  createDialog() {
    const dialogRef = this.dialog.open(ModalProductComponent, {
      width: '800px',
      data: {
        condition: 'create',
      },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.loadProducts(this.onGetParams());
      }
    });
  }

  updateDialog(product: ProductModel) {
    const dialogRef = this.dialog.open(ModalProductComponent, {
      width: '800px',
      data: {
        condition: 'update',
        product: product,
      },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.loadProducts(this.onGetParams());
      }
    });
  }

  deleteDialog(product: ProductModel) {
    const dialogRef = this.dialog.open(ModalProductComponent, {
      width: '800px',
      data: {
        condition: 'delete',
        product: product,
      },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.loadProducts(this.onGetParams());
      }
    });
  }

  applyFilter(event: KeyboardEvent) {
    if (event.key === 'Enter' || event.key === 'numpadEnter') {
      let keyword = this.searchInputControl.value.trim().toLowerCase();
      this.onGetSearch(keyword);
    }
  }
}
