import { AfterViewInit, Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { Trick } from '../../../shared/models/game';

export const FIELD1_WIDTH_SMALL = 95;

export interface DialogData {
  allTrickData: Trick[],
  nicknames: string[]
}

@Component({
  selector: 'app-show-all-trick-dialog',
  templateUrl: './show-all-tricks-dialog.component.html',
  styleUrls: ['./show-all-tricks-dialog.component.scss']
})
export class ShowAllTricksDialogComponent implements OnInit, AfterViewInit {
  title = 'AllTricks';
  field1Width: number;
  nicknames: string[];
  rowData: any;
  columnDefs: any;

  constructor(
    public dialogRef: MatDialogRef<ShowAllTricksDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: DialogData) {
    this.init();
  }

  ngOnInit(): void {
  }

  init() {
    this.rowData = this.data.allTrickData;
    this.nicknames = this.data.nicknames;

    this.field1Width = FIELD1_WIDTH_SMALL;

    this.columnDefs = [
      {headerName: this.nicknames[0], field: 'player1', width: this.field1Width,
        cellStyle(params){
          if (params.value.includes('♥️') || params.value.includes('♦️')) {
            return {color:'red', 'margin-right': '0'}
          } else {
            return {color: 'black', 'margin-right': '0'}
          }
        }},
      {headerName: this.nicknames[1], field: 'player2', width: this.field1Width,
        cellStyle(params){
          if (params.value.includes('♥️') || params.value.includes('♦️')) {
            return {color:'red', 'margin-right': '0'}
          } else {
            return {color: 'black', 'margin-right': '0'}
          }
        }},
      {headerName: this.nicknames[2], field: 'player3', width: this.field1Width,
        cellStyle(params){
          if (params.value.includes('♥️') || params.value.includes('♦️')) {
            return {color:'red'}
          } else {
            return {color: 'black'}
          }
        }},
      {headerName: this.nicknames[3], field: 'player4', width: this.field1Width,
        cellStyle(params){
          if (params.value.includes('♥️') || params.value.includes('♦️')) {
            return {color:'red'}
          } else {
            return {color: 'black'}
          }
        }},
      {headerName: 'Camp', field: 'camp', width: this.field1Width}
    ];
  }

  ngAfterViewInit(): void {
  }


}
