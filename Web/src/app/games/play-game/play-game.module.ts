import { STEPPER_GLOBAL_OPTIONS } from '@angular/cdk/stepper';
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatChipsModule } from '@angular/material/chips';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatGridListModule } from '@angular/material/grid-list';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatStepperModule } from '@angular/material/stepper';
import { AgGridModule } from 'ag-grid-angular';
import { SharedCommonModule } from '../../shared-common/shared-common.module';
import { ShowAllTricksDialogComponent } from './dialog-comp-show-all-tricks/show-all-tricks-dialog.component';
import { BidAnnounceComponent } from './dumb-comp-bid-announce/bid-announce.component';
import { BidComponent } from './dumb-comp-bid/bid.component';
import { CardComponent } from './dumb-comp-card/card.component';
import { ChipPlayerComponent } from './dumb-comp-chip-player/chip-player.component';
import { LastTrickComponent } from './dumb-comp-last-trick/last-trick.component';
import { MiniCardComponent } from './dumb-comp-mini-card/mini-card.component';
import { MyCardsComponent } from './dumb-comp-my-cards/my-cards.component';
import { OnTableBiddingComponent } from './dumb-comp-on-table-bidding/on-table-bidding.component';
import { OnTablePlayingComponent } from './dumb-comp-on-table-playing/on-table-playing.component';
import { ResumeGameComponent } from './dumb-comp-resume-game/resume-game.component';
import { ShowAllTricksComponent } from './dumb-comp-show-all-tricks/show-all-tricks.component';
import { StateComponent } from './dumb-comp-state/state.component';
import { PlayGameRoutingModule } from './play-game-routing.module';
import { PlayGameComponent } from './play-game/play-game.component';
import { TestImgComponent } from './test-img/test-img.component';


@NgModule({
  declarations: [
    PlayGameComponent,
    CardComponent,
    StateComponent,
    OnTablePlayingComponent,
    MyCardsComponent,
    OnTableBiddingComponent,
    BidComponent,
    ChipPlayerComponent,
    BidAnnounceComponent,
    ResumeGameComponent,
    LastTrickComponent,
    MiniCardComponent,
    ShowAllTricksComponent,
    ShowAllTricksDialogComponent,
    TestImgComponent

  ],
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
    MatSelectModule,
    ReactiveFormsModule,
    SharedCommonModule,
    AgGridModule.withComponents([]),
  ],
  providers: [
    {
      provide: STEPPER_GLOBAL_OPTIONS,
      useValue: { displayDefaultIndicatorType: false }
    }
  ]})
export class PlayGameModule {
}
