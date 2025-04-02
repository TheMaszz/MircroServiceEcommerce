import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';
import { MaterialModules } from 'app/core/modules/material.module';

@Component({
  selector: 'app-checkout-cancel',
  standalone: true,
  imports: [MaterialModules],
  templateUrl: './checkout-cancel.component.html'
})
export class CheckoutCancelComponent {

}
