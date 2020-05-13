import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { GAME_TYPE_AUTO } from '../../../shared/models/game';

export interface DialogData {
  gameType: string,
  name: string,
  nicknameCreator: string
}

@Component({
  selector: 'app-create-dialog',
  templateUrl: './create-game-dialog.component.html',
  styleUrls: ['./create-game-dialog.component.scss']
})
export class CreateGameDialogComponent implements OnInit {
  public form: FormGroup;
  gameType = GAME_TYPE_AUTO;

  constructor(public dialogRef: MatDialogRef<CreateGameDialogComponent>,
              @Inject(MAT_DIALOG_DATA) public data: DialogData,
              private fb: FormBuilder) {
  }

  ngOnInit(): void {
    this.form = this.fb.group({
      gameType: this.fb.control(GAME_TYPE_AUTO),
      name: this.fb.control('', Validators.required),
      nicknameCreator: this.fb.control('')
    });
  }

  onNoClick(): void {
    this.dialogRef.close();
  }

  save(): void {
    this.dialogRef.close(this.form.value);
  }

}
