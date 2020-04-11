import { Component, OnInit, ViewChild } from '@angular/core';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { AgGridAngular } from 'ag-grid-angular';
import { Observable } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { ApiLobbyService } from '../../services/apis/api-lobby.service';
import { BreakpointService } from '../../services/breakpoint/breakpoint.service';
import { Game } from '../../shared/models/game';
import { GameI } from '../../shared/models/game-interface';
import { CreateGameDialogComponent } from './create-game-dialog/create-game-dialog.component';

export interface DialogData {
  name: '';
  nicknameCreator: ''
}

export const DIALOG_WIDTH = '300px';
export const TABLE_WIDTH_SMALL = '410px';
export const TABLE_WIDTH_LARGE = '550px';
export const FIELD1_WIDTH_SMALL = 150;
export const FIELD1_WIDTH_LARGE = 220;
export const FIELD2_WIDTH_SMALL = 110;
export const FIELD2_WIDTH_LARGE = 150;
export const FIELD3_WIDTH_SMALL = 150;
export const FIELD3_WIDTH_LARGE = 180;

@Component({
  selector: 'app-all-games',
  templateUrl: './all-games.component.html',
  styleUrls: ['./all-games.component.scss']
})
export class AllGamesComponent implements OnInit {
  @ViewChild('agGrid') agGrid: AgGridAngular;

  title = 'All Games';
  rowData$: Observable<GameI[]>;
  selectedDataStringPresentation: string;
  isRowSelected: boolean;
  isSmallScreen: boolean;
  tableWidthSmall = TABLE_WIDTH_SMALL;
  tableWidthLarge = TABLE_WIDTH_LARGE;
  field1Width: number;
  field2Width: number;
  field3Width: number;

  // dialog data
  newGame: Game = new Game({});

  columnDefs: any;

  constructor(
    private apiService: ApiLobbyService,
    private breakpointService: BreakpointService,
    private dialog: MatDialog
  ) {
    this.breakpointService.layoutChanges$()
      .subscribe(() => {
          this.updateLayoutForScreenChange();
        }
      );
  }

  // ----- dialog begin ------
  openDialog(): void {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.disableClose = false;
    dialogConfig.autoFocus = true;
    dialogConfig.data = this.newGame;
    dialogConfig.width = DIALOG_WIDTH;

    const dialogRef = this.dialog.open(CreateGameDialogComponent, dialogConfig);

    dialogRef.afterClosed()
      .pipe(switchMap(data => this.processCreateGame(data)))
      .subscribe(res => {
        console.log(res);
        this.rowData$ = this.apiService.allGames();
      });
  }

  // ----- dialog end ------

  ngOnInit(): void {
    this.rowData$ = this.apiService.allGames();
  }

  getSelectedRows() {
    const selectedNodes = this.agGrid.api.getSelectedNodes();
    const selectedData = selectedNodes.map(node => node.data);
    this.selectedDataStringPresentation = selectedData.map((node: GameI) => node.id).join(', ');
    this.isRowSelected = selectedData.some((node: GameI) => (node !== null && node?.id !== null));
  }

  isRowSelectable(rowNode: any): boolean {
    return rowNode.data ? rowNode.data.nbJoined < 4 : false;
  }

  joinGame() {
    console.log('join game');
    this.apiService.joinGame(this.selectedDataStringPresentation, '')
      .subscribe(() => this.rowData$ = this.apiService.allGames());
  }

  createNewGame() {
    this.openDialog();
  }

  private updateLayoutForScreenChange() {
    this.isSmallScreen = this.breakpointService.isSmallScreen();
    this.field1Width = this.isSmallScreen ? FIELD1_WIDTH_SMALL : FIELD1_WIDTH_LARGE;
    this.field2Width = this.isSmallScreen ? FIELD2_WIDTH_SMALL : FIELD2_WIDTH_LARGE;
    this.field3Width = this.isSmallScreen ? FIELD3_WIDTH_SMALL : FIELD3_WIDTH_LARGE;
    this.columnDefs = [
      {headerName: 'Game name', field: 'name', width: this.field1Width, sort: 'asc', sortable: true, filter: true, checkboxSelection: true},
      {headerName: '#Players', field: 'nbJoined', width: this.field2Width, sortable: true, filter: true},
      {headerName: 'Creator', field: 'nicknameCreator', width: this.field3Width, sortable: true, filter: true}
    ];
  }

  private processCreateGame(data: Game) {
    console.log('The dialog was closed');
    this.newGame.name = data.name;
    this.newGame.nicknameCreator = data.nicknameCreator;
    return this.apiService.createGame(this.newGame);
  }
}
