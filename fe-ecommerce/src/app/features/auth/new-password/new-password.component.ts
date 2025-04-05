import { CommonModule, Location } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { MaterialModules } from 'app/core/modules/material.module';
import { AuthService } from 'app/core/services/auth.service';

@Component({
  selector: 'app-new-password',
  standalone: true,
  imports: [MaterialModules, CommonModule, ReactiveFormsModule],
  templateUrl: './new-password.component.html',
})
export class NewPasswordComponent implements OnInit {
  constructor(
    private _formBuider: FormBuilder,
    private location: Location,
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private authService: AuthService
  ) {
    this.newPassFormGroup = this._formBuider.group({
      password: ['', [Validators.required, Validators.minLength(8)]],
      cfPassword: ['', [Validators.required, Validators.minLength(8)]],
    });
  }

  newPassFormGroup: FormGroup;
  token!: string;

  ngOnInit(): void {
    this.activatedRoute.params.subscribe((params) => {
      this.token = params['token'];
    });
  }

  backNavigate() {
    this.location.back();
  }

  submitHandler() {
    const password = this.newPassFormGroup.get('password')?.value;
    const cfPassword = this.newPassFormGroup.get('cfPassword')?.value;

    if (password != cfPassword) {
      window.alertFail('กรุณากรอกรหัสผ่านให้เหมือนกัน');
      return;
    }

    const formdata = this.newPassFormGroup.getRawValue();
    const newData = {
      new_password: formdata.password,
      token_reset_password: this.token,
    };

    this.authService.resetPassword(newData).subscribe({
      next: (response) => {
        console.log('res: ', response);
        window.alertSuccess('แก้ไขรหัสผ่านเรียบร้อย ลองเข้าสู่ระบบใหม่');
        this.router.navigate(['/auth/signin']);
      },
      error: (error) => {
        console.log('res Error: ', error);
      },
    });
  }
}
