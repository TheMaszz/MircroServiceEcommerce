import { Component } from '@angular/core';
import { RouterModule, RouterOutlet } from '@angular/router';


@Component({
  selector: 'app-home',
  standalone: true,
  imports: [RouterOutlet, RouterModule],
  templateUrl: './home.component.html',
})
export class HomeComponent {

}
