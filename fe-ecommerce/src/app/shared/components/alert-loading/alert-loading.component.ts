import { Component } from '@angular/core';
import { MaterialModules } from 'app/core/modules/material.module';

@Component({
  selector: 'app-alert-loading',
  standalone: true,
  imports: [MaterialModules],
  templateUrl: './alert-loading.component.html',
})
export class AlertLoadingComponent {

}
