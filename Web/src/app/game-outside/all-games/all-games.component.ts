import { HttpClient } from '@angular/common/http';
import { Component, OnInit, ViewChild } from '@angular/core';
import { AgGridAngular } from 'ag-grid-angular';
import { ApiLogInOutService } from '../../services/apis/api-log-in-out.service';

@Component({
  selector: 'app-all-games',
  templateUrl: './all-games.component.html',
  styleUrls: ['./all-games.component.scss']
})
export class AllGamesComponent implements OnInit {
  @ViewChild('agGrid') agGrid: AgGridAngular;

  title = 'All Games';
  rowData$: any;

  columnDefs = [
    {headerName: 'Make', field: 'make', width: 150, sortable: true, filter: true, checkboxSelection: true},
    {headerName: 'Model', field: 'model', width: 125, sortable: true, filter: true},
    {headerName: 'Price', field: 'price', width: 125, sortable: true, filter: true}
  ];

  rowData = [
    {make: 'Toyota', model: 'Celica', price: 35000},
    {make: 'Ford', model: 'Mondeo', price: 32000},
    {make: 'Porsche', model: 'Boxter', price: 72000}
  ];

  constructor(private http: HttpClient) {
  }

  ngOnInit(): void {
    // this.rowData$ = this.http.get<any>('https://api.myjson.com/bins/15psn9');
    // this.rowData$ = this.apiService.
  }

  getSelectedRows() {
    const selectedNodes = this.agGrid.api.getSelectedNodes();
    const selectedData = selectedNodes.map(node => node.data);
    const selectedDataStringPresentation = selectedData.map(node => node.make + ' ' + node.model).join(', ');
    alert(`Selected nodes: ${selectedDataStringPresentation}`);

  }
}
