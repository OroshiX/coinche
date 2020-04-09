import { Component, OnInit, ViewChild } from '@angular/core';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { AgGridAngular } from 'ag-grid-angular';
import { tap } from 'rxjs/operators';
import { ApiLobbyService } from '../../services/apis/api-lobby.service';
import { Game } from '../../shared/models/game';
import { GameI } from '../../shared/models/game-interface';
import { CreateGameDialogComponent } from './create-game-dialog/create-game-dialog.component';

export interface DialogData {
  gameId: '';
  nicknameCreator: ''
}

@Component({
  selector: 'app-all-games',
  templateUrl: './all-games.component.html',
  styleUrls: ['./all-games.component.scss']
})
export class AllGamesComponent implements OnInit {
  @ViewChild('agGrid') agGrid: AgGridAngular;

  title = 'All Games';
  rowData: GameI[];
  selectedDataStringPresentation: string;
  isRowSelected: boolean;

  // dialog data
  newGame: Game = new Game({});

  columnDefs = [
    {headerName: 'GameId', field: 'id', width: 150, sort: 'asc', filter: true, checkboxSelection: true},
    {headerName: '#Players', field: 'nbJoined', width: 110, sortable: true, filter: true},
    {headerName: 'Creator', field: 'nicknameCreator', width: 140, sortable: true, filter: true}
  ];

  constructor(
    private apiService: ApiLobbyService,
    private dialog: MatDialog
  ) {
  }

  // ----- dialog begin ------
  openDialog(): void {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.disableClose = false;
    dialogConfig.autoFocus = true;
    dialogConfig.data = this.newGame;
    dialogConfig.width = '300px';

    const dialogRef = this.dialog.open(CreateGameDialogComponent, dialogConfig);

    dialogRef.afterClosed().subscribe(data => {
      console.log('The dialog was closed');
      this.newGame = data;
      this.apiService.createGame(this.newGame).subscribe(res => console.log('new gameId', res))
    });
  }

  // ----- dialog end ------

  ngOnInit(): void {
    this.apiService.allGames()
      .pipe(tap(res => console.log(res)))
      .subscribe((res: GameI[]) => {
        this.rowData = res;
        console.log(this.isRowSelected);
      });
  }

  getSelectedRows() {
    const selectedNodes = this.agGrid.api.getSelectedNodes();
    const selectedData = selectedNodes.map(node => node.data);
    this.selectedDataStringPresentation = selectedData
      .map((node: GameI) => node.id).join(', ');
    this.isRowSelected = selectedData.some((node: GameI) => (node !== null && node?.id !== null));
    console.log(this.isRowSelected);
  }

  isRowSelectable(rowNode: any): boolean {
    return rowNode.data ? rowNode.data.nbJoined < 4 : false;
  };

  joinGame() {
    return true;
  }

  createNewGame() {
    this.openDialog();
  }
}
