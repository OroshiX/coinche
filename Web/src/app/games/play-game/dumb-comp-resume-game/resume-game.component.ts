import { Component, Input, OnChanges, OnInit, SimpleChanges } from '@angular/core';
import { STATE, TYPE_BID } from '../../../shared/models/collection-game';
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
  pairEastWestNamePadding: string;
  pairNorthSouthNamePadding: string;
  showBidAnnounced: boolean;

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
      this.showBidAnnounced = !(this.isBidsEmpty && this.bidData.currentBidType === TYPE_BID.PASS
        && this.gameState === STATE.BIDDING);

      this.pairEastWestNamePadding =
        `${shortenStr(this.bidData.eastNicknames, 14)}/${shortenStr(this.bidData.westNicknames, 14)}`;

      this.pairNorthSouthNamePadding =
        `${shortenStr(this.bidData.northNicknames, 14)}/${shortenStr(this.bidData.southNicknames, 14)}`;
      /*this.pairNorthSouthNamePadding = str.padEnd(24, '\u2764\uFE0F');*/
      /*this.pairNorthSouthNamePadding = str.padEnd(24, '\u2663');
      this.pairNorthSouthNamePadding = str.padEnd(24, '\u2666');

      this.sleep = 'Waiting for '.concat('\u231B');*/
    }
  }

}
