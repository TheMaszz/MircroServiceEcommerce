import { CdkDragDrop, moveItemInArray } from '@angular/cdk/drag-drop';
import { CommonModule } from '@angular/common';
import { Component, Inject, OnInit } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import {
  MAT_DIALOG_DATA,
  MatDialog,
  MatDialogRef,
} from '@angular/material/dialog';
import { MaterialModules } from 'app/core/modules/material.module';
import { ImageUrlPipe } from 'app/core/pipe/imageUrlPipe';
import { ProductService } from 'app/core/services/product.service';
import { ProductModel } from 'app/models/product.model';
import { ModalCropImageComponent } from 'app/shared/components/modal-crop-image/modal-crop-image.component';

@Component({
  selector: 'app-modal-product',
  standalone: true,
  imports: [
    CommonModule,
    MaterialModules,
    ReactiveFormsModule,
    FormsModule,
    ImageUrlPipe,
  ],
  templateUrl: './modal-product.component.html',
})
export class ModalProductComponent implements OnInit {
  constructor(
    private formBuilder: FormBuilder,
    public dialogRef: MatDialogRef<ModalProductComponent>,
    @Inject(MAT_DIALOG_DATA)
    public data: {
      condition: string;
      product?: ProductModel;
    },
    private dialog: MatDialog,
    private productService: ProductService
  ) {
    this.productFormGroup = this.formBuilder.group({
      id: [''],
      name: ['', Validators.required],
      description: [''],
      qty: ['', Validators.required],
      price: ['', Validators.required],
      product_image: [],
    });
  }

  productFormGroup: FormGroup;

  isDragging = false;
  images: any[] = [];
  imagePreview: string | ArrayBuffer | null = null;
  fileList: File[] = [];

  ngOnInit(): void {
    if (this.data.condition === 'update') {
      this.productFormGroup.patchValue({
        id: this.data.product?.id,
        name: this.data.product?.name,
        description: this.data.product?.description,
        qty: this.data.product?.qty,
        price: this.data.product?.price,
        product_image: this.data.product?.product_image,
      });
      this.images = [...(this.data.product?.product_image || [])];
    }
  }

  // Form submit handler
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
    const formData = new FormData();
    const productJson = JSON.stringify(this.productFormGroup.getRawValue());
    formData.append(
      'product',
      new Blob([productJson], { type: 'application/json' })
    );

    if (this.images.length === 0 && this.fileList.length === 0) {
      window.alertFail('กรุณาอัปโหลดไฟล์ภาพ!');
      return;
    }

    this.fileList.forEach((file) => {
      formData.append('files', file);
    });

    this.productService.createProduct(formData).subscribe({
      next: (res) => {
        window.alertSuccess('สร้างสินค้าใหม่เรียบร้อยแล้ว');
        this.dialogRef.close(res);
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

  updateHandler() {
    const formData = new FormData();
    const productJson = JSON.stringify(this.productFormGroup.getRawValue());
    formData.append(
      'product',
      new Blob([productJson], { type: 'application/json' })
    );

    if (this.images.length === 0 && this.fileList.length === 0) {
      window.alertFail('กรุณาอัปโหลดไฟล์ภาพ!');
      return;
    }

    this.fileList.forEach((file) => {
      formData.append('files', file);
    });

    this.productService
      .updateProduct(this.data.product?.id!, formData)
      .subscribe({
        next: (res) => {
          window.alertSuccess('แก้ไขข้อมูลสินค้าเรียบร้อยแล้ว');
          this.dialogRef.close(res);
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

  deleteHandler() {
    this.productService.deleteProduct(this.data.product?.id!).subscribe({
      next: (res) => {
        window.alertSuccess('ลบข้อมูลสินค้าเรียบร้อยแล้ว');
        this.dialogRef.close(res);
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

  // Drag and drop file upload
  onDragOver(event: DragEvent) {
    event.preventDefault();
    this.isDragging = true;
  }

  onDragLeave(event: DragEvent) {
    event.preventDefault();
    this.isDragging = false;
  }

  onDrop(event: DragEvent) {
    event.preventDefault();
    this.isDragging = false;

    const files = event.dataTransfer?.files;
    if (files && files.length > 0) {
      const fileArray: File[] = Array.from(files);
      this.handleFiles(fileArray);
    }
  }

  onFileChange(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files) {
      const fileList: FileList = input.files;
      const files: File[] = Array.from(fileList);

      if (
        files.length > 5 ||
        this.images.length >= 5 ||
        this.fileList.length + files.length >= 5
      ) {
        window.alertFail('คุณสามารถอัปโหลดไฟล์ได้สูงสุด 5 ไฟล์');
        return;
      }

      this.handleFiles(files);
    }
  }

  handleFiles(files: File[]) {
    this.fileList = [...this.fileList, ...files];
    this.previewImage(files);
  }

  // Image actions
  removeImage(index: number) {
    this.images.splice(index, 1);
    this.fileList.splice(index, 1);
    if (this.data.condition === 'update') {
      this.productFormGroup.patchValue({
        product_image: this.images,
      });
    }
    console.log('images', this.images);
    console.log('productFormGroup', this.productFormGroup.getRawValue());
  }

  editImage(index: number) {
    if (this.data.condition === 'update') {
      window.alertFail('ไม่สามารถแก้ไขภาพได้ในโหมดนี้');
      return;
    }
    const file = this.fileList[index];
    this.dialog
      .open(ModalCropImageComponent, {
        disableClose: true,
        width: '800px',
        minHeight: '600px',
        data: {
          file: file,
        },
      })
      .afterClosed()
      .subscribe((result) => {
        if (result) {
          this.fileList[index] = result.file;
          this.images[index].image_url = result.url;
        }
      });
  }

  previewImage(files: File[]) {
    files.forEach((file) => {
      const reader = new FileReader();
      reader.onload = () => {
        const imageUrl = reader.result as string;
        this.images.push({
          image_url: imageUrl,
          id: null,
          product_id: null,
        });
      };
      reader.readAsDataURL(file);
    });
  }

  // Image Drag Drop
  drop(event: CdkDragDrop<string[]>) {
    moveItemInArray(this.images, event.previousIndex, event.currentIndex);
    moveItemInArray(this.fileList, event.previousIndex, event.currentIndex);
  }
}
