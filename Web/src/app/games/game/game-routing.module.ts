import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { PlayStartComponent } from './play-start/play-start.component';

const routes: Routes = [
  {
    path: '',
    component: PlayStartComponent
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class GameRoutingModule {
}
