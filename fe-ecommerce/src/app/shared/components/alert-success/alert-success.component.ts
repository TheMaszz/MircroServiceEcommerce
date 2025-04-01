import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { MaterialModules } from 'app/core/modules/material.module';

@Component({
  selector: 'app-alert-success',
  standalone: true,
  imports: [MaterialModules],
  templateUrl: './alert-success.component.html',
})
export class AlertSuccessComponent implements OnInit {

    constructor(
      public dialogRef: MatDialogRef<AlertSuccessComponent>,
      @Inject(MAT_DIALOG_DATA) public data: { message: string }
    ) {}
  
    ngOnInit(): void {

    }
  
    closeDialog(){
      this.dialogRef.close();
    }
  
    confirmAction(){
      this.dialogRef.close();
    }

}
