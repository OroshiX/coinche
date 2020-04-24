import { STEPPER_GLOBAL_OPTIONS } from '@angular/cdk/stepper';
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatChipsModule } from '@angular/material/chips';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatGridListModule } from '@angular/material/grid-list';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatStepperModule } from '@angular/material/stepper';
import { PlayGameRoutingModule } from './play-game-routing.module';
import { PlayGameComponent } from './play-game/play-game.component';
import { CardComponent } from './dumb-comp-card/card.component';
import { StateComponent } from './dumb-comp-state/state.component';
import { OnTableComponent } from './dumb-comp-on-table/on-table.component';
import { MyCardsComponent } from './dumb-comp-my-cards/my-cards.component';


@NgModule({
  declarations: [PlayGameComponent, CardComponent, StateComponent, OnTableComponent, MyCardsComponent],
  imports: [
    CommonModule,
    PlayGameRoutingModule,
    MatCardModule,
    MatStepperModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatIconModule,
    MatGridListModule,
    MatChipsModule,
    ReactiveFormsModule
  ],
  providers: [
    {
      provide: STEPPER_GLOBAL_OPTIONS,
      useValue: { displayDefaultIndicatorType: false }
    }
  ]})
export class PlayGameModule {
}
