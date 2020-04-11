import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormControl, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';


@Component({
  selector: 'app-create-nickname-dialog',
  templateUrl: './create-nickname-dialog.component.html',
  styleUrls: ['./create-nickname-dialog.component.scss']
})
export class CreateNicknameDialogComponent implements OnInit{
  public nicknameControl: FormControl;

  constructor(public dialogRef: MatDialogRef<CreateNicknameDialogComponent>,
              @Inject(MAT_DIALOG_DATA) public data: string,
              private fb: FormBuilder) {}

  ngOnInit(): void {
        this.nicknameControl = this.fb.control('', Validators.required);
    }

  onNoClick(): void {
    this.dialogRef.close();
  }

  save(): void {
    console.log(this.nicknameControl.value);
    this.dialogRef.close(this.nicknameControl.value);
  }

}
