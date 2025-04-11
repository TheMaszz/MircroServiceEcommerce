import { Component, Inject } from '@angular/core';
import {
  ImageCropperComponent,
  ImageCroppedEvent,
  LoadedImage,
} from 'ngx-image-cropper';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { MaterialModules } from 'app/core/modules/material.module';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-modal-crop-image',
  standalone: true,
  imports: [ImageCropperComponent, MaterialModules, CommonModule],
  templateUrl: './modal-crop-image.component.html',
})
export class ModalCropImageComponent {
  imageBase64: string = '';
  croppedImage: SafeUrl = '';
  croppedFile: File | null = null;

  constructor(
    private sanitizer: DomSanitizer,
    public dialogRef: MatDialogRef<ModalCropImageComponent>,
    @Inject(MAT_DIALOG_DATA)
    public data: {
      file: File;
    }
  ) {
    console.log('File:', this.data.file);
    
    this.convertFileToBase64(this.data.file);
  }

  convertFileToBase64(file: File) {
    const reader = new FileReader();
    reader.onload = () => {
      this.imageBase64 = reader.result as string;
    };
    reader.readAsDataURL(file);
  }

  imageCropped(event: ImageCroppedEvent) {
    this.croppedImage = this.sanitizer.bypassSecurityTrustUrl(event.objectUrl!);
    this.croppedFile = new File([event.blob!], this.data.file.name, {
      type: event.blob?.type,
    });
  }

  imageLoaded() {}
  cropperReady() {}
  loadImageFailed() {}

  onCrop() {
    this.dialogRef.close({
      url: this.croppedImage,
      file: this.croppedFile,
    });
  }

  onCancel() {
    this.dialogRef.close(null);
  }
}
