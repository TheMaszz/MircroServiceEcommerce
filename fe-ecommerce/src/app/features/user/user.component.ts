import { Component } from '@angular/core';
import { RouterModule, RouterOutlet } from '@angular/router';


@Component({
  selector: 'app-user',
  standalone: true,
  imports: [RouterOutlet, RouterModule],
  templateUrl: './user.component.html',
})
export class UserComponent {

}
