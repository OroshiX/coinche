import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { DialogData } from '../all-games.component';


@Component({
  selector: 'app-create-dialog',
  templateUrl: './create-game-dialog.component.html',
  styleUrls: ['./create-game-dialog.component.scss']
})
export class CreateGameDialogComponent implements OnInit{
  public form: FormGroup;

  constructor(public dialogRef: MatDialogRef<CreateGameDialogComponent>,
              @Inject(MAT_DIALOG_DATA) public data: DialogData,
              private fb: FormBuilder) {}

  ngOnInit(): void {
        this.form = this.fb.group({
          gameId: this.fb.control(''),
          nicknameCreator: this.fb.control('')
        })
    }

  onNoClick(): void {
    this.dialogRef.close();
  }

  save(): void {
    this.dialogRef.close(this.form.value);
  }

}
