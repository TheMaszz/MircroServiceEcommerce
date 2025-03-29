import { Component, Inject, Input } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { MaterialModules } from 'app/core/modules/material.module';

@Component({
  selector: 'app-modal-checkout',
  standalone: true,
  imports: [MaterialModules],
  templateUrl: './modal-checkout.component.html',
})
export class ModalCheckoutComponent {
  constructor(
    private router: Router,
    public dialogRef: MatDialogRef<ModalCheckoutComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { paymentUrl: string }
  ) {}

  payNow() {
    this.dialogRef.close();
    window.location.href = this.data.paymentUrl;
  }

  payLater() {
    this.dialogRef.close();
    this.router.navigate(['/orders']);
  }
}
