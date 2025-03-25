import { Component } from '@angular/core';
import { RouterModule, RouterOutlet } from '@angular/router';
import { GlobalService } from './core/services/global.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, RouterModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
})
export class AppComponent {
  title = 'fe-ecommerce';

  constructor(private global: GlobalService) {}
}
