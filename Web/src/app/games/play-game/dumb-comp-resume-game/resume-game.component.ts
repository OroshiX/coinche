import { Component, Input, OnChanges, OnInit, SimpleChanges } from '@angular/core';
import { STATE } from '../../../shared/models/collection-game';
import { BidData } from '../play-game/play-game.component';

@Component({
  selector: 'app-resume-game',
  templateUrl: './resume-game.component.html',
  styleUrls: ['./resume-game.component.scss']
})
export class ResumeGameComponent implements OnInit, OnChanges {
  @Input() gameState: STATE;
  @Input() isMyTurn: boolean;
  @Input() bidData: BidData;

  playOrBid: string;

  constructor() {
  }

  ngOnInit(): void {
  }

  ngOnChanges(changes: SimpleChanges): void {
    // console.log(JSON.stringify(this.bidData));
    // console.log(this.gameState);
    this.playOrBid = this.gameState === STATE.BIDDING ?
      'bid' : this.gameState === STATE.PLAYING ? 'play' : '';
    // console.log(this.playOrBid);
  }

}
