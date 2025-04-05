import { CommonModule } from '@angular/common';
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
import { UserService } from 'app/core/services/user.service';
import { UserModel } from 'app/models/user.model';

@Component({
  selector: 'app-change-password',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule, MaterialModules],
  templateUrl: './change-password.component.html',
})
export class ChangePasswordComponent implements OnInit {
  constructor(
    private _formBuilder: FormBuilder,
    private router: Router,
    private authService: AuthService,
    private userService: UserService
  ) {
    this.changePassFormGroup = this._formBuilder.group({
      old_password: ['', [Validators.required, Validators.minLength(8)]],
      new_password: ['', [Validators.required, Validators.minLength(8)]],
      cf_new_password: ['', [Validators.required, Validators.minLength(8)]],
    });
  }

  changePassFormGroup: FormGroup;
  profile!: UserModel;

  ngOnInit(): void {
    this.userService.getMyProfile().subscribe({
      next: (response) => {
        console.log('res: ', response);
        this.profile = response.data;
      },
      error: (error) => {
        console.log('res Error: ', error);
      },
    });
  }

  submitHandler() {
    const formData = this.changePassFormGroup.getRawValue();

    if (formData.new_password != formData.cf_new_password) {
      window.alertFail('กรุณากรอกรหัสผ่านใหม่ให่้เหมือนกัน');
      return;
    }

    this.authService.changePassword(formData).subscribe({
      next: (response) => {
        console.log('res: ', response);
        sessionStorage.removeItem('jwt');
        window.alertSuccess('แก้ไขรหัสผ่านเรียบร้อย ลองเข้าสู่ระบบใหม่');
        this.router.navigate(['/auth/signin']);
      },
      error: (error) => {
        console.log('res Error: ', error);
      },
    });
  }
}
