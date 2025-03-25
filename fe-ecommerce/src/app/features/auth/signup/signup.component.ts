import { Component } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../auth.service';
import { MaterialModules } from 'app/core/modules/material.module';

@Component({
  selector: 'app-signup',
  standalone: true,
  imports: [RouterModule, MaterialModules, ReactiveFormsModule, CommonModule],
  templateUrl: './signup.component.html',
  styleUrl: './signup.component.scss',
})
export class SignupComponent {
  constructor(
    private _formBuilder: FormBuilder,
    private authService: AuthService,
    private _router: Router
  ) {
    this.signupFormGroup = this._formBuilder.group({
      email: ['', [Validators.required, Validators.email]],
      username: ['', Validators.required],
      password: ['', [Validators.required, Validators.minLength(8)]],
      cfPassword: ['', [Validators.required, Validators.minLength(8)]],
    });
  }

  signupFormGroup: FormGroup;

  onSubmitHandler() {
    if (
      this.signupFormGroup.get('password')?.value !==
      this.signupFormGroup.get('cfPassword')?.value
    ) {
      console.log(
        this.signupFormGroup.get('password')?.value,
        this.signupFormGroup.get('cfPassword')?.value
      );

      alert('Password Not Match');
    }
    const data = {
      email: this.signupFormGroup.get('email')?.value,
      username: this.signupFormGroup.get('username')?.value,
      password: this.signupFormGroup.get('password')?.value,
    };
    this.authService.signup(data).subscribe((res: any) => {
      if (res.response_desc == 'success') {
        console.log('signup success: ', res);
        this._router.navigate(["/signin"]);
      } else {
        console.error(res.error);
      }
    });
  }
}
