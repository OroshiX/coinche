import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { PlayGameComponent } from './play-game/play-game.component';
import { TestImgComponent } from './test-img/test-img.component';

const routes: Routes = [
  {
    path: '',
    component: PlayGameComponent
  },
  {
    path: 'test',
    component: TestImgComponent
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class PlayGameRoutingModule {
}
