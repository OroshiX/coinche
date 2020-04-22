import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatGridListModule } from '@angular/material/grid-list';

import { GridRoutingModule } from './grid-routing.module';
import { GridComponent } from './grid.component';


@NgModule({
  declarations: [GridComponent],
  imports: [
    CommonModule,
    GridRoutingModule,
    MatGridListModule,
    MatCardModule,
    MatButtonModule
  ]
})
export class GridModule { }
