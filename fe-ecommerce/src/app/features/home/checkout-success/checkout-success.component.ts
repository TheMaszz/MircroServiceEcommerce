import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';
import { MaterialModules } from 'app/core/modules/material.module';

@Component({
  selector: 'app-checkout-success',
  standalone: true,
  imports: [MaterialModules],
  templateUrl: './checkout-success.component.html'
})
export class CheckoutSuccessComponent {

}
