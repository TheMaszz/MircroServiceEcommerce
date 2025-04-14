import { Component } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { MaterialModules } from 'app/core/modules/material.module';
import { AuthService } from 'app/core/services/auth.service';

@Component({
  selector: 'app-signup',
  standalone: true,
  imports: [RouterModule, MaterialModules, ReactiveFormsModule, CommonModule],
  templateUrl: './signup.component.html',
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

    this.authService.signup(data).subscribe({
      next: (response) => {
        console.log('res: ', response);
        window.alertSuccess('สมัครสมาชิกสำเร็จ!');
        this._router.navigate(['/auth/signin']);
      },
      error: (error) => {
        console.log('res Error: ', error);
        if (error.error?.status === 409) {
          window.alertFail('พบข้อมูลซ้ำ!');
        } else if (error.status === 401) {
          window.alertFail('กรุณาเข้าสู่ระบบใหม่!');
        } else {
          window.alertFail('เกิดข้อผิดพลาดในสมัคสมาชิก! โปรดลองใหม่อีกครั้งภายหลัง');
        }
      },
    });
  }
}
