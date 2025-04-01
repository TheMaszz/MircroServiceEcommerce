import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-orders',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './orders.component.html',
  styleUrl: './orders.component.scss',
})
export class OrdersComponent implements OnInit {
  constructor() {}

  currentIndexStage: number = 0;

  stages = [
    {
      text: 'ทั้งหมด',
      value: 'All',
    },
    {
      text: 'กำลังดำเนินการ',
      value: 'Pending',
    },
    {
      text: 'ที่ต้องชำระ',
      value: 'Payment',
    },
    {
      text: 'ที่ต้องจัดส่ง',
      value: 'Preparing',
    },
    {
      text: 'ที่ต้องได้รับ',
      value: 'Shipping||Delivered',
    },
    {
      text: 'ทั้งหมสำเร็จ',
      value: 'Completed',
    },
    {
      text: 'ยกเลิก',
      value: 'Cancelled',
    },
  ];

  ngOnInit(): void {
    // window.alertLoading();

    // window.closeLoading();


  }

  changeStage(i: number) {
    this.currentIndexStage = i;
  }
}
