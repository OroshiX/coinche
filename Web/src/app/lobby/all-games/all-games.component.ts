import {Component, OnInit, ViewChild} from '@angular/core';
import {AgGridAngular} from 'ag-grid-angular';
import {tap} from 'rxjs/operators';
import {ApiLobbyService} from '../../services/apis/api-lobby.service';
import {GameI} from '../../shared/models/game-interface';


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

  columnDefs = [
    {headerName: 'GameId', field: 'id', width: 150, sort: 'asc', filter: true, checkboxSelection: true},
    {headerName: '#Players', field: 'nbJoined', width: 110, sortable: true, filter: true},
    {headerName: 'Creator', field: 'nicknameCreator', width: 140, sortable: true, filter: true}
  ];

  constructor(private apiService: ApiLobbyService) {
  }

  ngOnInit(): void {
    this.apiService.allGames()
      .pipe(tap(res => console.log(res)))
      .subscribe((res: GameI[]) => {
        this.rowData = res;
      });
  }

  getSelectedRows(event: Event) {
    console.log(event);
    const selectedNodes = this.agGrid.api.getSelectedNodes();
    const selectedData = selectedNodes.map(node => node.data);
    this.selectedDataStringPresentation = selectedData
      .map((node: GameI) => node.id + ' ' + node.nbJoined + ' ' + node.nicknameCreator).join(', ');
    // alert(`Selected row: ${selectedDataStringPresentation}`);

  }

  isRowSelectable(rowNode: any): boolean {
    return rowNode.data ? rowNode.data.nbJoined < 4 : false;
  };
}
