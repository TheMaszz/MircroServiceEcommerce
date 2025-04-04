import { CommonModule } from '@angular/common';
import { Component, Inject, OnInit } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { MaterialModules } from 'app/core/modules/material.module';
import { UserService } from 'app/core/services/user.service';
import { Address } from 'app/models/user.model';

@Component({
  selector: 'app-modal-address',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule, MaterialModules],
  templateUrl: './modal-address.component.html',
})
export class ModalAddressComponent implements OnInit {
  constructor(
    private _formBuilder: FormBuilder,
    private userService: UserService,
    public dialogRef: MatDialogRef<ModalAddressComponent>,
    @Inject(MAT_DIALOG_DATA)
    public data: {
      condition: string;
      id?: number;
      address?: Address;
    }
  ) {
    this.addressFormGroup = this._formBuilder.group({
      id: [''],
      name: ['', Validators.required],
      fullname: ['', Validators.required],
      address: ['', Validators.required],
      description: [''],
      phone: ['', Validators.required],
    });
  }

  addressFormGroup: FormGroup;

  ngOnInit(): void {
    if (this.data.condition === 'update') {
      this.addressFormGroup.patchValue({
        id: this.data.address?.id,
        name: this.data.address?.name,
        fullname: this.data.address?.fullname,
        address: this.data.address?.address,
        description: this.data.address?.description,
        phone: this.data.address?.phone,
      });
    }
  }

  closeHandler() {
    this.dialogRef.close();
  }

  submitHandler() {
    if (this.data.condition === 'create') {
      this.createHandler();
    }

    if (this.data.condition === 'update') {
      this.updateHandler();
    }

    if (this.data.condition === 'delete') {
      this.deleteHandler();
    }
  }

  createHandler() {
    const address = this.addressFormGroup.getRawValue();
    
    this.userService.createAddress(address).subscribe({
      next: (response) => {
        console.log('res: ', response);
        this.dialogRef.close();
        window.alertSuccess("เพิ่มข้อมูลสำเร็จ");
      },
      error: (error) => {
        console.log('res Error: ', error);
      },
    });
  }

  updateHandler() {
    const id = this.data.id;
    if(!id) return;

    const address = this.addressFormGroup.getRawValue();

    this.userService.updateAddress(id, address).subscribe({
      next: (response) => {
        console.log('res: ', response);
        this.dialogRef.close();
        window.alertSuccess("แก้ไขข้อมูลสำเร็จ");
      },
      error: (error) => {
        console.log('res Error: ', error);
      },
    });
  }

  deleteHandler() {
    const id = this.data.id;
    if(!id) return;

    this.userService.deleteAddress(id).subscribe({
      next: (response) => {
        console.log('res: ', response);
        this.dialogRef.close();
        window.alertSuccess("ลบข้อมูลสำเร็จ");
      },
      error: (error) => {
        console.log('res Error: ', error);
      },
    });
  }
}
