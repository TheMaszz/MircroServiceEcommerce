import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { MaterialModules } from 'app/core/modules/material.module';
import { ImageUrlPipe } from 'app/core/pipe/imageUrlPipe';
import { OrderService } from 'app/core/services/order.service';
import { OrderModel } from 'app/models/order.model';
import { Subject } from 'rxjs';
import { PaginationComponent } from '../../../shared/components/pagination/pagination.component';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

@Component({
  selector: 'app-orders',
  standalone: true,
  imports: [
    MaterialModules,
    CommonModule,
    ImageUrlPipe,
    PaginationComponent,
    RouterLink,
  ],
  templateUrl: './orders.component.html',
})
export class OrdersComponent implements OnInit {
  constructor(
    private orderService: OrderService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  private unsubscribe$ = new Subject<void>();

  currentPage: number = 1;
  totalPages: number = 0;
  currentIndexStage: number = 0;
  currentStage: string = 'All';
  searchOrder: string = '';
  orders: OrderModel[] = [];

  stages: any[] = [];

  ngOnInit(): void {
    this.stages = this.orderService.getStages();

    this.route.queryParams.subscribe((params) => {
      this.searchOrder = params['search'] || '';
      this.currentPage = params['page'] ? Number(params['page']) : 1;

      this.fetchOrders();
    });
  }

  ngOnDestroy(): void {
    this.unsubscribe$.next();
    this.unsubscribe$.complete();
  }

  fetchOrders() {
    window.alertLoading();
    this.orderService
      .getMyOrders({
        search: this.searchOrder,
        page_number: this.currentPage,
        page_size: 10,
        sort: 'created_at',
        sort_type: 'desc',
        stage: this.currentStage,
      })
      .subscribe({
        next: (response) => {
          this.orders = response.data;
          this.currentPage = response.paginate.page;
          this.totalPages = Math.ceil(
            response.paginate.total / response.paginate.limit
          );
          console.log('orders: ', this.orders);
          window.closeLoading();
        },
        error: (error) => {
          console.error('Error fetching orders:', error);
          window.closeLoading();
        },
      });
  }

  changeStage(i: number) {
    this.currentIndexStage = i;
    this.currentStage = this.stages[this.currentIndexStage].value;
    this.fetchOrders();
  }

  checkStage2Text(value: string): string {
    return this.orderService.checkStage2Text(value); 
  }
  
  checkStage2Icon(value: string): string {
    return this.orderService.checkStage2Icon(value);
  }
  
  checkStageColor(value: string): string {
    return this.orderService.checkStageColor(value); 
  }
  

  onPageChanged(page: number) {
    this.route.queryParams.subscribe((params) => {
      const updatedParams = {
        ...params,
        page: page,
      };
      this.router.navigate(['/orders/'], { queryParams: updatedParams });
    });
  }

  trackByOrder(index: number, order: any): number {
    return order.id;
  }

  trackByProduct(index: number, product: any): number {
    return product.product_id;
  }
}
