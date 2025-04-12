import { CommonModule } from '@angular/common';
import { Component, OnInit, ViewChild } from '@angular/core';
import {
  FormControl,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
} from '@angular/forms';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { MaterialModules } from 'app/core/modules/material.module';
import { ImageUrlPipe } from 'app/core/pipe/imageUrlPipe';
import { OrderService } from 'app/core/services/order.service';
import { OrderModel } from 'app/models/order.model';
import { Subject, takeUntil } from 'rxjs';

@Component({
  selector: 'app-orders',
  standalone: true,
  imports: [
    MaterialModules,
    CommonModule,
    ImageUrlPipe,
    ReactiveFormsModule,
    FormsModule,
  ],
  templateUrl: './orders.component.html',
})
export class OrdersComponent implements OnInit {
  constructor(private orderService: OrderService) {}

  private unsubscribe$ = new Subject<void>();

  searchInputControl: FormControl = new FormControl('');
  displayedColumns: string[] = [
    'id',
    'description',
    'total_amount',
    'stage',
    'payment_status',
    'created_at',
    'updated_at',
    'actions',
  ];
  pageSizeOptions: number[] = [10, 20, 50, 100];
  pagination: any;
  isLoading: boolean = false;
  searching: boolean = false;

  stageList: any[] = this.orderService.getStages();
  readonly range = new FormGroup({
    start: new FormControl<Date | null>(null),
    end: new FormControl<Date | null>(null),
  });

  dataSource = new MatTableDataSource<OrderModel>();
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  selectedPayment: string = 'All';
  selectedStage: string = 'All';

  ngOnInit(): void {
    this.isLoading = true;
    this.loadOrders();
  }

  loadOrders(params?: any) {
    this.isLoading = true;
    this.orderService
      .getMyShopOrders(params)
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

  onGetParams() {
    let sortActive = this.sort.active ? this.sort.active : 'created_at';
    let sortDirection = this.sort.direction ? this.sort.direction : 'desc';

    if (!this.sort.direction) {
      sortActive = 'created_at';
      sortDirection = 'desc';
    }

    const start_date = this.formatDateLocal(this.range.value.start || null);
    const end_date = this.formatDateLocal(this.range.value.end || null);

    let params: any = {
      sort: sortActive,
      sort_type: sortDirection,
      page_number: this.paginator?.pageIndex + 1 || 1,
      page_size: this.paginator?.pageSize || 10,
      stage: this.selectedStage,
      start_date: start_date,
      end_date: end_date,
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

    this.orderService
      .getMyShopOrders(params)
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

  applyFilter(event: KeyboardEvent) {
    if (event.key === 'Enter' || event.key === 'numpadEnter') {
      let keyword = this.searchInputControl.value.trim().toLowerCase();
      this.onGetSearch(keyword);
    }
  }

  onStageChange(event: any) {
    this.selectedStage = event.value;
    this.onGetSearch(this.searchInputControl.value);
  }

  clearRangeDate() {
    this.range.reset();
    this.onGetSearch(this.searchInputControl.value);
  }

  onRangeDateSubmit() {
    const startDate = this.range.value.start;
    const endDate = this.range.value.end;

    if (startDate && endDate) {
      this.onGetSearch(this.searchInputControl.value);
    }
  }

  formatDateLocal(date: Date | null): string | null {
    if (!date) return null;
    const d = new Date(date);
    const year = d.getFullYear();
    const month = ('0' + (d.getMonth() + 1)).slice(-2);
    const day = ('0' + d.getDate()).slice(-2);
    return `${year}-${month}-${day}`;
  }

  clearFilter() {
    this.searchInputControl.setValue('');
    this.range.reset();
    this.selectedStage = 'All';
    this.onGetSearch('');
  }

  changeStage(id: number, stage: string) {
    this.orderService.updateStage(id, stage).subscribe({
      next: (response) => {
        window.alertSuccess('อัพเดตสถานะการสั่งซื้อเรียบร้อยแล้ว');
        this.loadOrders(this.onGetParams());
      },
      error: (error) => {
        console.log('res Error: ', error);
        if (error.error?.status === 409) {
          window.alertFail('พบข้อมูลซ้ำ!');
        } else if (error.status === 401) {
          window.alertFail('กรุณาเข้าสู่ระบบใหม่!');
        } else {
          window.alertFail('เกิดข้อผิดพลาดในการแก้ไขข้อมูล!');
        }
      },
    });
  }


}
