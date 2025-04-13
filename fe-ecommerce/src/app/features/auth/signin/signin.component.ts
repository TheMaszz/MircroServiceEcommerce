import { Component } from '@angular/core';
import { Router, RouterLink, RouterModule } from '@angular/router';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { CommonModule } from '@angular/common';
import { MaterialModules } from 'app/core/modules/material.module';
import { AuthService } from 'app/core/services/auth.service';

@Component({
  selector: 'app-signin',
  standalone: true,
  imports: [RouterModule, MaterialModules, ReactiveFormsModule, CommonModule],
  templateUrl: './signin.component.html',
})
export class SigninComponent {
  constructor(
    private _formBuilder: FormBuilder, 
    private authService: AuthService,
    private _router: Router
  ) {
    this.signinFormGroup = this._formBuilder.group({
      usernameOrEmail: ['', Validators.required],
      password: ['', [Validators.required, Validators.minLength(8)]],
    });
  }

  signinFormGroup: FormGroup;

  onSubmitHandler(){
    const data = {
      usernameOrEmail: this.signinFormGroup.get('usernameOrEmail')?.value,
      password: this.signinFormGroup.get('password')?.value
    }
    this.authService.signin(data).subscribe((res: any)=>{
      if(res.response_desc == "success"){
        console.log("signin success: ", res);

        sessionStorage.setItem('jwt', res.data.token);
        sessionStorage.setItem('user', JSON.stringify(res.data));

        this._router.navigate(["/"]);
      }else{
        console.error(res.error);
      }
    });
  }
}
