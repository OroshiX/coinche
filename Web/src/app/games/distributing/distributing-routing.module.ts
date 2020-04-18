import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { DistributingComponent } from './distributing.component';


const routes: Routes = [
  {
    path: '',
    component: DistributingComponent
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class DistributingRoutingModule {
}
