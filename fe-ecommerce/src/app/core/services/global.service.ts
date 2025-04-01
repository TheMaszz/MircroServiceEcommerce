import { Injectable } from '@angular/core';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { AlertFailComponent } from 'app/shared/components/alert-fail/alert-fail.component';
import { AlertInfoComponent } from 'app/shared/components/alert-info/alert-info.component';
import { AlertLoadingComponent } from 'app/shared/components/alert-loading/alert-loading.component';
import { AlertSuccessComponent } from 'app/shared/components/alert-success/alert-success.component';

@Injectable({
  providedIn: 'root',
})
export class GlobalService {

  constructor(private dialog: MatDialog) {
    (window as any).alertLoading = this.alertLoading.bind(this);
    (window as any).closeLoading = this.closeLoading.bind(this);
    (window as any).alertInfo = this.alertInfo.bind(this);
    (window as any).alertSuccess = this.alertSuccess.bind(this);
    (window as any).alertFail = this.alertFail.bind(this);
  }

  private loadingDialogRef?: MatDialogRef<AlertLoadingComponent>;
  private startTime!: any;

  alertLoading() {
    this.loadingDialogRef = this.dialog.open(AlertLoadingComponent, {
      disableClose: true,
      width: '400px',
    });
  
    this.startTime = Date.now(); 
  }
  
  closeLoading() {
    const elapsedTime = Date.now() - this.startTime; 
    const minLoadingTime = 500; 
    
    if (elapsedTime < minLoadingTime) {
      setTimeout(() => {
        this.loadingDialogRef?.close();
      }, minLoadingTime - elapsedTime);
    } else {
      this.loadingDialogRef?.close();
    }
  }

  alertInfo(message: string) {
    console.log(`Message: ${message}`);
    this.loadingDialogRef = this.dialog.open(AlertInfoComponent, {
      disableClose: true,
      width: '400px',
      data: { message: message },
    });
  }

  alertSuccess(message: string) {
    console.log(`Message: ${message}`);
    this.loadingDialogRef = this.dialog.open(AlertSuccessComponent, {
      disableClose: true,
      width: '400px',
      data: { message: message },
    });
  }

  alertFail(message: string) {
    console.log(`Message: ${message}`);
    this.loadingDialogRef = this.dialog.open(AlertFailComponent, {
      disableClose: true,
      width: '400px',
      data: { message: message },
    });
  }
}
