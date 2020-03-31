import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { PlayInitComponent } from './play-init/play-init.component';

const routes: Routes = [
  {
    path: '',
    component: PlayInitComponent
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class GameRoutingModule {
}
