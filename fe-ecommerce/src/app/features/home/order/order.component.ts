import { CommonModule, Location } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { MaterialModules } from 'app/core/modules/material.module';
import { ImageUrlPipe } from 'app/core/pipe/imageUrlPipe';
import { OrderService } from 'app/core/services/order.service';
import { OrderModel } from 'app/models/order.model';

@Component({
  selector: 'app-order',
  standalone: true,
  imports: [MaterialModules, CommonModule, ImageUrlPipe, RouterLink],
  templateUrl: './order.component.html',
})
export class OrderComponent implements OnInit {
  constructor(
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private location: Location,
    private orderService: OrderService
  ) {}

  order!: OrderModel;
  stages: any[] = [
    {
      text: 'มีคำสั่งซื้อใหม่',
      value: 'Pending',
      icon: 'pending_actions',
    },
    {
      text: 'รายการสั่งซื้อมีการชำระเงินแล้ว',
      value: 'Preparing',
      icon: 'payment',
    },
    {
      text: 'กำลังจัดส่ง',
      value: 'Shipping',
      icon: 'local_shipping',
    },
    {
      text: 'จัดส่งเรียบร้อย',
      value: 'Delivered',
      icon: 'inventory_2',
    },
    {
      text: 'คำสั่งซื้อสำเร็จ',
      value: 'Completed',
      icon: 'check_circle',
    },
  ];

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ order }) => {
      this.order = order.data;
    });
    console.log('order: ', this.order);
  }

  backNavigate() {
    this.location.back();
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

  getStageIndex(stage: string): number {
    const index = this.stages.findIndex((s) => s.value === stage);
    return index !== -1 ? index : 0;
  }

  markStage(currentOrderStage: string, stageToCheck: string): boolean {
    const currentIndex = this.getStageIndex(currentOrderStage);
    const checkIndex = this.getStageIndex(stageToCheck);
    
    return checkIndex <= currentIndex;
  }

  trackByProduct(index: number, product: any): number {
    return product.product_id;
  }

}
