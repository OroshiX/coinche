import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { GameRoutingModule } from './game-routing.module';
import { PlayInitComponent } from './play-init/play-init.component';



@NgModule({
  declarations: [PlayInitComponent],
  imports: [
    CommonModule,
    GameRoutingModule
  ]
})
export class GameModule { }
