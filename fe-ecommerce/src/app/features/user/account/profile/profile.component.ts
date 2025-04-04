import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { MaterialModules } from 'app/core/modules/material.module';
import { ImageUrlPipe } from 'app/core/pipe/imageUrlPipe';
import { UserService } from 'app/core/services/user.service';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule, MaterialModules, ImageUrlPipe],
  templateUrl: './profile.component.html',
})
export class ProfileComponent implements OnInit {

  constructor(
    private _formBuilder: FormBuilder,
    private activatedRoute: ActivatedRoute,
    private userService: UserService
  ) {
    this.profileFormGroup = this._formBuilder.group({
      id: [''],
      username: ['', Validators.required],
      email: ['', Validators.required],
      profile_url: [''],
    });
  }

  previewUrl: string | null = null;
  file!: File;
  profileFormGroup: FormGroup;

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ profile }) => {
      this.profileFormGroup.patchValue({
        id: profile.data.id,
        username: profile.data.username,
        email: profile.data.email,
        profile_url: profile.data.profile_url,
      });
    });
    console.log('profile: ', this.profileFormGroup.getRawValue());
  }

  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      const file = input.files[0];

      if (file.size > 1024 * 1024) {
        alert('ไฟล์ต้องมีขนาดไม่เกิน 1MB');
        return;
      }

      this.file = file;

      const reader = new FileReader();
      reader.onload = (e) => {
        this.previewUrl = e.target?.result as string;
      };
      reader.readAsDataURL(file);
    }
  }

  submitHandler() {
    const formData = new FormData();
    const userJson = JSON.stringify(this.profileFormGroup.getRawValue());
    formData.append('user', new Blob([userJson], { type: 'application/json' }));

    if (this.file != null || this.file != undefined) {
      formData.append('file', this.file);
    }

    const userId = this.profileFormGroup.get('id')?.value;
    this.userService.updateProfile(userId, formData).subscribe({
      next: (response) => {
        console.log('res: ', response);
        window.alertSuccess('แก้ไขข้อมูลสำเร็จ!');
      },
      error: (error) => {
        console.log('res Error: ', error);
        if (error.error?.status === 409) {
          window.alertFail('พบข้อมูลซ้ำ!');
        } else if (error.status === 401) {
          window.alertFail('กรุณาเข้าสู่ระบบใหม่!');
        } else {
          window.alertFail('เกิดข้อผิดพลาดในการแก้ไขข้อมูล!');
        }
      },
    });
  }

}
