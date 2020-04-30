import { Component, Inject } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { bidPointList, bidTypeList } from '../../../shared/models/collection-game';
import { colorList } from '../../../shared/models/play';
import { DialogData } from '../play-game/play-game.component';

@Component({
  selector: 'app-dialog-bid',
  templateUrl: './dialog-bid.component.html',
  styleUrls: ['./dialog-bid.component.scss']
})
export class DialogBidComponent {
  bidForm: FormGroup;
  bidPoints = bidPointList;
  bidColors = colorList;
  bidTypes = bidTypeList;

  /*
    get bidTypeControl(): FormControl {
      return this.bidForm.get('bidType') as FormControl;
    }*/

  constructor(public dialogRef: MatDialogRef<DialogBidComponent>,
              @Inject(MAT_DIALOG_DATA) public data: DialogData) {
    this.bidForm = new FormGroup({
      points: new FormControl(data.points),
      color: new FormControl(data.color),
      type: new FormControl(data.type)
    });
    this.bidForm.valueChanges.subscribe(val => {
      this.data = val;
    });
  }

  onNoClick(): void {
    this.dialogRef.close();
  }

}
