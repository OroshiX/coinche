import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { PlayInitComponent } from './play-init/play-init.component';
import { PlayStartComponent } from './play-start/play-start.component';

const routes: Routes = [
  {
    path: 'init',
    component: PlayInitComponent
  },
  {
    path: 'start',
    component: PlayStartComponent
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class GameRoutingModule {
}
