import { AfterViewInit, ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import { switchMap } from 'rxjs/operators';
import { ApiFirestoreService } from '../../../services/apis-firestore/api-firestore.service';
import { ApiGamesService } from '../../../services/apis/api-games.service';
import { BreakpointService } from '../../../services/breakpoint/breakpoint.service';
import { Bid, PLAYER_POSITION, STATE, TableGame } from '../../../shared/models/collection-game';
import { Belote, CardView, PlayCard } from '../../../shared/models/play';
import { CardImageService } from '../services/card-image.service';
import { PlayGameHelperService } from '../services/play-game-helper.service';

interface Tile {
  text: string;
  cols: number;
  rows: number;
  color: string;
}

export interface DialogData {
  points: number;
  color: string;
  type: string
}

export interface BidData {
  eastWest: number;
  northSouth: number;
  eastNicknames: string;
  westNicknames: string;
  northNicknames: string;
  southNicknames: string;
  currentBidNickname: string;
  currentBidPoints: number;
  currentBidColor: string;
  currentBidType: string;
  nextPlayer: string;
}

@Component({
  selector: 'app-play-start',
  templateUrl: './play-game.component.html',
  styleUrls: ['./play-game.component.scss'],
})
export class PlayGameComponent implements OnInit, AfterViewInit {
  gameId: string;
  cardsPlayed: CardView[] = new Array<CardView>();
  lastTricks: CardView[] = new Array<CardView>();
  isLastTricksEmpty: boolean;
  map = new Map<number, string[]>();
  myCardMap: Map<string, CardView> = new Map<string, CardView>();
  backCardImgSmall: string;
  isSmallScreen: boolean;
  rowHeight: number;

  gameState: STATE;
  mustHideOnTable: boolean;
  bidData: BidData;
  currentBidColor: string;
  currentBidPoints: number;
  currentBidType: any;

  currentBidPointsAnnounced: number;
  isBidsEmpty: boolean;
  isDisableBid: boolean;
  isMyTurn: boolean;
  isMyCardsDisable: boolean;
  nextPlayerIdx: number;
  myPosition: string;

  playersNicknameByMyPosOnTable: string[] = [];
  bidListOrdered: Bid[] = [];
  winnerLastTrick: string;
  beloteRebelote: Belote;

  tiles: Tile[] = [
    {text: '', cols: 2, rows: 1, color: 'darkgreen'},
    {text: 'SOUTH', cols: 1, rows: 1, color: 'darkgreen'},
    {text: '', cols: 2, rows: 1, color: 'darkgreen'},
    {text: 'EAST', cols: 1, rows: 4, color: 'darkgreen'},
    {text: '', cols: 3, rows: 4, color: 'darkgreen'},
    {text: 'WEST', cols: 1, rows: 4, color: 'darkgreen'},
    {text: '', cols: 2, rows: 1, color: 'darkgreen'},
    {text: 'NORTH', cols: 1, rows: 1, color: 'darkgreen'},
    {text: '', cols: 2, rows: 1, color: 'darkgreen'},
    {text: '', cols: 5, rows: 2, color: 'darkred'}
  ];


  constructor(
    public dialog: MatDialog,
    private route: ActivatedRoute,
    private router: Router,
    private service: CardImageService,
    private breakpointService: BreakpointService,
    private firestoreService: ApiFirestoreService,
    private cd: ChangeDetectorRef,
    private helper: PlayGameHelperService,
    private apiService: ApiGamesService
  ) {
    this.cd.detach();
  }

  ngOnInit(): void {
    this.gameId = this.route.snapshot.params.id;
    this.cd.detectChanges();
    this.breakpointService.layoutChanges$()
      .pipe(switchMap(() => this.firestoreService.getTableGame(this.gameId)),
        // debounceTime(100)
      )
      .subscribe((data: TableGame) => {
        // console.log('table game *********************');
        // console.log(JSON.stringify(data));
        this.setTableGame(data);
        this.updateLayoutForScreenChange();
      });
  }

  ngAfterViewInit(): void {
    this.cd.detectChanges();
  }

  private updateLayoutForScreenChange() {
    if (this.breakpointService.isSmallScreen()) {
      this.isSmallScreen = true;
      this.rowHeight = 75;
      this.cd.detectChanges();
    } else {
      this.isSmallScreen = false;
      this.rowHeight = 95;
      this.cd.detectChanges();
    }
    this.backCardImgSmall = this.service.getBackCardImgSmall();
    this.cd.detectChanges();
  }

  private setTableGame(data: TableGame) {
    this.gameState = data.state;
    this.isDisableBid = this.gameState !== STATE.BIDDING;
    this.myPosition = data.myPosition;
    this.isMyTurn = data.myPosition === data.nextPlayer;
    this.isBidsEmpty = data.bids.length <= 0;
    this.currentBidPointsAnnounced = data.bids.length > 0 ? data.currentBid.points : 0;
    this.isMyCardsDisable = this.isMyCardsOnTableDisable(data.state, data.nextPlayer, data.myPosition);
    this.nextPlayerIdx = this.gameState !== STATE.ENDED && this.gameState !== STATE.JOINING ?
      this.helper.getIdxPlayer(data.myPosition, data.nextPlayer) : 9; // 9 just to set whatever
    this.playersNicknameByMyPosOnTable = this.helper.getPlayersNicknameByMyPos(data.myPosition, data.nicknames);
    // console.log('Players position on Table  ============', this.playersNicknameByMyPosOnTable);
    this.bidListOrdered = data.bids.length > 0 ? this.helper.getBidsOrderByMyPos(data.myPosition, data.bids) : data.bids;
    this.bidData = this.buildBidData(data);
    this.myCardMap = this.service.buildMyDeck(data.cards);
    this.cardsPlayed = this.helper.onTableCardsOrdered(data.myPosition, data.onTable);
    this.isLastTricksEmpty = data.lastTrick.length <= 0;
    this.lastTricks = this.helper.onTableCardsOrdered(data.myPosition, data.lastTrick);
    this.beloteRebelote = this.helper.beloteRebelote(data.myPosition, data.lastTrick, data.nicknames);
    /*if (this.isMyTurn && data.winnerLastTrick === data.myPosition) {
      console.log('must reset cards on table', this.cardsPlayed);
    }*/
    this.winnerLastTrick = this.helper.getNicknameByPos(data.winnerLastTrick, data.nicknames);
    this.mustHideOnTable = this.helper.mustHideCardsOntable(data);
    this.cd.detectChanges();
  }

  private setCurrentBidding(currentBid: Bid) {
    if (!!currentBid) {
      this.currentBidPoints = currentBid.points;
      this.currentBidColor = currentBid.color;
      this.currentBidType = currentBid.type;
      this.bidListOrdered[0] = currentBid;
    }
    this.cd.detectChanges();
  }

  private isMyCardsOnTableDisable(state: STATE, nextPlayer: PLAYER_POSITION, myPos: PLAYER_POSITION): boolean {
    return !(state === STATE.PLAYING && nextPlayer === myPos);
  }

  buildBidData(data: TableGame): BidData {
    return {
      eastWest: data.score.eastWest,
      northSouth: data.score.northSouth,
      eastNicknames: this.helper.getNicknameByPos(PLAYER_POSITION.EAST, data.nicknames),
      westNicknames: this.helper.getNicknameByPos(PLAYER_POSITION.WEST, data.nicknames),
      northNicknames: this.helper.getNicknameByPos(PLAYER_POSITION.NORTH, data.nicknames),
      southNicknames: this.helper.getNicknameByPos(PLAYER_POSITION.SOUTH, data.nicknames),
      currentBidColor: data?.currentBid?.color,
      currentBidNickname: this.helper.getNicknameByPos(data.currentBid.position, data.nicknames),
      currentBidPoints: data?.currentBid?.points,
      currentBidType: data?.currentBid?.type,
      nextPlayer: this.helper.getNicknameByPos(data.nextPlayer, data.nicknames)
    };
  }

  onAnnounceBid(bid: Bid) {
    console.log('in announceBid');
    this.setCurrentBidding(bid);
    this.isMyTurn = false;
  }

  onCardChosen(event$: any) {
    const cardChosen = event$;
    this.cardsPlayed[0] = cardChosen;
    this.apiService.playCard(this.gameId,
      new PlayCard({color: cardChosen.color, value: cardChosen.value, belote: null}))
      .subscribe(() => {
        console.log('Card played');
        this.cd.detectChanges();
      });
    this.cd.detectChanges();
  }
}
