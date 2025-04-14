import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { OrderRequest } from 'app/models/order.model';
import { ResponseModel } from 'app/models/response.model';
import { environment } from 'environments/environment';
import { BehaviorSubject, Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class OrderService {
  private serviceUrl = 'apiendpoint/order';

  constructor(private http: HttpClient) {}

  private orders = new BehaviorSubject<ResponseModel | null>(null);

  orders$ = this.orders.asObservable();

  private stages: any[] = [
    {
      text: 'ทั้งหมด',
      value: 'All',
      icon: 'list_alt',
    },
    {
      text: 'กำลังดำเนินการ',
      value: 'Pending',
      icon: 'pending_actions',
    },
    {
      text: 'ที่ต้องชำระ',
      value: 'Payment',
      icon: 'payment',
    },
    {
      text: 'ที่ต้องจัดส่ง',
      value: 'Preparing',
      icon: 'inventory',
    },
    {
      text: 'ที่ต้องได้รับ',
      value: 'Shipping||Delivered',
      icon: 'local_shipping',
    },
    {
      text: 'สำเร็จ',
      value: 'Completed',
      icon: 'check_circle',
    },
    {
      text: 'ยกเลิก',
      value: 'Cancelled',
      icon: 'cancel',
    },
  ];

  getAllOrders(
    params: {
      search?: string;
      page_number?: number;
      page_size?: number;
      sort?: string;
      sort_type?: 'asc' | 'desc';
      stage?: string;
    } = {
      search: '',
      page_number: 1,
      page_size: 10,
      sort: 'created_at',
      sort_type: 'desc',
      stage: 'All',
    }
  ): Observable<ResponseModel> {
    let httpParams = new HttpParams();
    Object.entries(params).forEach(([key, value]) => {
      if (value !== undefined && value !== null) {
        httpParams = httpParams.set(key, String(value));
      }
    });

    return this.http.get<ResponseModel>(
      `${environment.apiUrl}/${this.serviceUrl}/getAll`,
      { params: httpParams }
    );
  }

  create(data: OrderRequest[]): Observable<ResponseModel> {
    return this.http.post<ResponseModel>(
      `${environment.apiUrl}/${this.serviceUrl}/create`,
      data
    );
  }

  getMyOrders(
    params: {
      search?: string;
      page_number?: number;
      page_size?: number;
      sort?: string;
      sort_type?: 'asc' | 'desc';
      stage?: string;
    } = {
      search: '',
      page_number: 1,
      page_size: 10,
      sort: 'created_at',
      sort_type: 'desc',
      stage: 'All',
    }
  ): Observable<ResponseModel> {
    let httpParams = new HttpParams();
    Object.entries(params).forEach(([key, value]) => {
      if (value !== undefined && value !== null) {
        httpParams = httpParams.set(key, String(value));
      }
    });

    return this.http.get<ResponseModel>(
      `${environment.apiUrl}/${this.serviceUrl}/getMyOrders`,
      { params: httpParams }
    );
  }

  getMyShopOrders(
    params: {
      search?: string;
      page_number?: number;
      page_size?: number;
      sort?: string;
      sort_type?: 'asc' | 'desc';
      stage?: string;
    } = {
      search: '',
      page_number: 1,
      page_size: 10,
      sort: 'created_at',
      sort_type: 'desc',
      stage: 'All',
    }
  ): Observable<ResponseModel> {
    let httpParams = new HttpParams();
    Object.entries(params).forEach(([key, value]) => {
      if (value !== undefined && value !== null) {
        httpParams = httpParams.set(key, String(value));
      }
    });

    return this.http.get<ResponseModel>(
      `${environment.apiUrl}/${this.serviceUrl}/getMyShopOrders`,
      { params: httpParams }
    );
  }

  getStages(): any[] {
    return this.stages;
  }

  getOrderById(id: number): Observable<ResponseModel> {
    return this.http.get<ResponseModel>(
      `${environment.apiUrl}/${this.serviceUrl}/${id}`
    );
  }

  checkStage2Text(value: string) {
    const stage = this.stages.find((stage) => {
      if (stage.value === value) {
        return true;
      } else if (
        stage.value === 'Shipping||Delivered' &&
        (value === 'Shipping' || value === 'Delivered')
      ) {
        return true;
      }
      return false;
    });

    return stage ? stage.text : '';
  }

  checkStage2Icon(value: string) {
    const stage = this.stages.find((stage) => {
      if (stage.value === value) {
        return true;
      } else if (
        stage.value === 'Shipping||Delivered' &&
        (value === 'Shipping' || value === 'Delivered')
      ) {
        return true;
      }
      return false;
    });

    return stage ? stage.icon : 'help';
  }

  checkStageColor(value: string): string {
    switch (value) {
      case 'Pending':
        return 'text-yellow-500';
      case 'Payment':
        return 'text-blue-500';
      case 'Preparing':
        return 'text-purple-500';
      case 'Shipping':
      case 'Delivered':
        return 'text-green-500';
      case 'Completed':
        return 'text-teal-500';
      case 'Cancelled':
        return 'text-red-500';
      default:
        return 'text-gray-500';
    }
  }

  updateStage(id: number, stage: string): Observable<ResponseModel> {
    return this.http.put<ResponseModel>(
      `${environment.apiUrl}/${this.serviceUrl}/updateStage/${id}`,
      { stage: stage }
    );
  }
}
