import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { MaterialModules } from 'app/core/modules/material.module';

@Component({
  selector: 'app-alert-fail',
  standalone: true,
  imports: [MaterialModules],
  templateUrl: './alert-fail.component.html',
})
export class AlertFailComponent implements OnInit {

    constructor(
      public dialogRef: MatDialogRef<AlertFailComponent>,
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
