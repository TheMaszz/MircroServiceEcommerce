import { CommonModule, Location } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { Router } from '@angular/router';
import { MaterialModules } from 'app/core/modules/material.module';
import { AuthService } from 'app/core/services/auth.service';

@Component({
  selector: 'app-forgot-password',
  standalone: true,
  imports: [MaterialModules, CommonModule, ReactiveFormsModule],
  templateUrl: './forgot-password.component.html',
})
export class ForgotPasswordComponent implements OnInit {
  constructor(
    private _formBuider: FormBuilder,
    private location: Location,
    private router: Router,
    private authService: AuthService
  ) {
    this.forgotPassFormGroup = this._formBuider.group({
      email: ['', [Validators.required, Validators.email]],
    });
  }

  forgotPassFormGroup: FormGroup;
  sended: boolean = false;
  token!: string;

  ngOnInit(): void {}

  backNavigate() {
    this.location.back();
  }

  submitHandler(){
    
    const data = this.forgotPassFormGroup.getRawValue();

    this.authService.sendTokenResetPassword(data).subscribe({
      next: (response) => {
        console.log('res: ', response);
        this.sended = true;
        this.token = response.data;
        // this.router.navigate(['/new-password']);
      },
      error: (error) => {
        console.log('res Error: ', error);
      },
    });
  }

  resendEmail(){
    // this.authService.resendTokenResetPassword().subscribe({
    //   next: (response) => {
    //     console.log('res: ', response);
    //   },
    //   error: (error) =>{
    //     console.log("res Error: ", error);
        
    //   }
    // })
  }
}
