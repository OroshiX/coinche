import { HttpClientModule } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { AgGridModule } from 'ag-grid-angular';
import { AllGamesRoutingModule } from './all-games-routing.module';
import { AllGamesComponent } from './all-games.component';



@NgModule({
  declarations: [AllGamesComponent],
  imports: [
    CommonModule,
    HttpClientModule,
    AllGamesRoutingModule,
    AgGridModule.withComponents([]),
    MatButtonModule,
    MatCardModule
  ]
})
export class AllGamesModule { }
