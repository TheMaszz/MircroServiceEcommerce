import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class GlobalService {
  private loading: boolean = false;

  constructor() {
    (window as any).alertLoading = this.alertLoading.bind(this);
    (window as any).closeLoading = this.closeLoading.bind(this);
    (window as any).alertInfo = this.alertInfo.bind(this);
    (window as any).alertSuccess = this.alertSuccess.bind(this);
    (window as any).alertFail = this.alertFail.bind(this);
  }

  alertLoading() {
    this.loading = true;
    console.log('Loading started...');
  }

  closeLoading() {
    this.loading = false;
    console.log('Loading finished.');
  }

  alertInfo(message: string) {
    console.log(`Message: ${message}`);
    alert(message);
  }

  alertSuccess(message: string) {
    console.log(`Message: ${message}`);
    alert(message);
  }

  alertFail(message: string) {
    console.log(`Message: ${message}`);
    alert(message);
  }


}
