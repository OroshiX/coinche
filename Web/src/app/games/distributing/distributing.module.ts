import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { DistributingRoutingModule } from './distributing-routing.module';
import { DistributingComponent } from './distributing.component';


@NgModule({
  declarations: [DistributingComponent],
  imports: [
    CommonModule,
    DistributingRoutingModule
  ]
})
export class DistributingModule { }
