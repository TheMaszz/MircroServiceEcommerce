import { Component } from '@angular/core';
import { RouterModule, RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-auth',
  standalone: true,
  imports: [RouterOutlet, RouterModule],
  templateUrl: './auth.component.html',
})
export class AuthComponent {

}
