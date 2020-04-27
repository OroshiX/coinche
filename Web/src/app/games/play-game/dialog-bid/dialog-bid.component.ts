import { Component, Inject } from '@angular/core';
import { FormControl } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

export interface DialogData {
  animal: string;
  name: string;
}

@Component({
  selector: 'app-dialog-bid',
  templateUrl: './dialog-bid.component.html',
  styleUrls: ['./dialog-bid.component.scss']
})
export class DialogBidComponent {

  constructor(public dialogRef: MatDialogRef<DialogBidComponent>,
              @Inject(MAT_DIALOG_DATA) public data: DialogData) {
  }
  controlInp: FormControl = new FormControl();
  onNoClick(): void {
    this.dialogRef.close();
  }

}
