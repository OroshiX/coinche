import { AfterViewInit, ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import { switchMap } from 'rxjs/operators';
import { ApiFirestoreService } from '../../../services/apis-firestore/api-firestore.service';
import { BreakpointService } from '../../../services/breakpoint/breakpoint.service';
import { Bid, BID_POINTS, PLAYER_POSITION, STATE, TableGame, TYPE_BID } from '../../../shared/models/collection-game';
import { Card, CARD_COLOR, CardView } from '../../../shared/models/play';
import { CardImageService } from '../services/card-image.service';
import { PlayGameHelperService } from '../services/play-game-helper.service';

interface Tile {
  text: string;
  cols: number;
  rows: number;
  color: string;
}

export interface DialogData {
  points: BID_POINTS;
  color: CARD_COLOR;
  type: TYPE_BID
}

export interface BidData {
  eastWest: number;
  northSouth: number;
  currentBidNickname: string;
  currentBidPoints: number;
  currentBidColor: CARD_COLOR;
  currentBidType: TYPE_BID;
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
  map = new Map<number, string[]>();
  myCardMap: Map<string, CardView> = new Map<string, CardView>();
  cardNorth: CardView;
  backCardImg: string;
  backCardImgSmall: string;
  isSmallScreen: boolean;
  rowHeight: number;

  gameState: STATE;

  bidData: BidData;
  currentBidColor: string;
  currentBidPoints: number;
  currentBidType: any;

  isDisableBid: boolean;
  isMyTurn: boolean;
  isMyCardsDisable: boolean;
  nextPlayerIdx: number;
  myPosition: string;

  playersPosOnTable: string[] = [];
  bidListOrdered: Bid[] = [];

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
  ) {
    this.cd.detach();
  }

  ngOnInit(): void {
    this.gameId = this.route.snapshot.params.id;
    this.cd.detectChanges();
    this.breakpointService.layoutChanges$()
      .pipe(switchMap(() => this.firestoreService.getTableGame(this.gameId)))
      .subscribe((data: TableGame) => {
        console.log(JSON.stringify(data));
        this.setTableGame(data);
        // if (this.gameState !== STATE.JOINING && data.cards !== []) {
        this.updateLayoutForScreenChange(data.cards);
        // }
      });
  }

  ngAfterViewInit(): void {
    this.cd.detectChanges();
  }

  onCardChosen(event$: any) {
    this.cardNorth = event$;
    this.cardsPlayed[0] = this.cardNorth;
    console.log('card played', this.cardNorth.value, this.cardNorth.color, this.myPosition);
    const key = `${this.cardNorth.color}${this.cardNorth.value}`;
    this.myCardMap.delete(key);
    this.cd.detectChanges();
  }

  private updateLayoutForScreenChange(cards: Card[]) {
    if (this.breakpointService.isSmallScreen()) {
      this.isSmallScreen = true;
      this.rowHeight = 60;
    } else {
      this.isSmallScreen = false;
      this.rowHeight = 95;
    }
    this.backCardImg = this.service.getBackCardImgSmall();
    this.backCardImgSmall = this.service.getBackCardImg();
    this.myCardMap = this.service.buildMyDeck(cards);
    this.cd.detectChanges();
  }

  private setTableGame(data: TableGame) {
    console.log(JSON.stringify(data));
    this.gameState = data.state;
    this.isDisableBid = this.gameState !== STATE.BIDDING;
    this.myPosition = data.myPosition;
    this.isMyTurn = data.myPosition === data.nextPlayer;
    this.isMyCardsDisable = this.isMyCardsOnTableDisable(data.state, data.nextPlayer, data.myPosition);
    this.nextPlayerIdx = this.helper.getIdxPlayer(data.nextPlayer);
    this.playersPosOnTable = this.helper.getPlayersOrderByMyPos(data.myPosition, data.nicknames);
    this.bidListOrdered = data.bids !== [] ? this.helper.getBidsOrderByMyPos(data.myPosition, data.bids) : data.bids;
    this.bidData = this.buildBidData(data);
    console.log(JSON.stringify(this.bidData));
    this.cd.detectChanges();
  }

  private seCurrentBidding(currentBid: Bid) {
    this.currentBidPoints = currentBid.points;
    this.currentBidColor = currentBid.color;
    this.currentBidType = currentBid.type;
    this.cd.detectChanges();
  }

  private isMyCardsOnTableDisable(state: STATE, nextPlayer: PLAYER_POSITION, myPos: PLAYER_POSITION): boolean {
    return !(state === STATE.PLAYING && nextPlayer === myPos);
  }

  buildBidData(data: TableGame): BidData {
    return {
      eastWest: data.score.eastWest,
      northSouth: data.score.northSouth,
      currentBidColor: data?.currentBid?.color,
      currentBidNickname: this.helper.getNicknameByPos(data.currentBid.position, data.nicknames),
      currentBidPoints: data?.currentBid?.points,
      currentBidType: data?.currentBid?.type,
      nextPlayer: this.helper.getNicknameByPos(data.nextPlayer, data.nicknames)
    };
  }

  onAnnounceBid(bid: Bid) {
    this.seCurrentBidding(bid);
    this.isMyTurn = false;
  }
}
