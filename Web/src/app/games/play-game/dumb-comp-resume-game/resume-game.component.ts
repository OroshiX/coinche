import { Component, Input, OnChanges, OnInit, SimpleChanges } from '@angular/core';
import { STATE } from '../../../shared/models/collection-game';
import { shortenStr } from '../../../shared/utils/shorter-name';
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
  @Input() isSmallScreen: boolean;
  @Input() isBidsEmpty: boolean;

  playOrBid: string;
  currentOrPreviousBid: string;
  winner1: string;
  winner2: string;
  limit: 8;
  pairEastWestNamePadding: string;
  pairNorthSouthNamePadding: string;

  constructor() {
  }

  ngOnInit(): void {
  }

  ngOnChanges(changes: SimpleChanges): void {
    this.playOrBid = this.gameState === STATE.BIDDING ?
      'bid' : this.gameState === STATE.PLAYING ? 'play' : '';

    if (!!this.bidData) {
      this.winner1 = (this.bidData.eastWest) - (this.bidData.northSouth) > 0 ?
        this.bidData.eastNicknames : this.bidData.northNicknames;
      this.winner2 = (this.bidData.eastWest) - (this.bidData?.northSouth) > 0 ?
        this.bidData.westNicknames : this.bidData.southNicknames;

      this.currentOrPreviousBid = this.isBidsEmpty ? 'Previous bid' : 'Current bid';

      this.pairEastWestNamePadding =
        `${shortenStr(this.bidData.eastNicknames, 9)}/${shortenStr(this.bidData.westNicknames, 9)}`;

      this.pairNorthSouthNamePadding =
        `${shortenStr(this.bidData.northNicknames, 9)}/${shortenStr(this.bidData.southNicknames, 9)}`;

    }
  }

}
