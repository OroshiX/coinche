import { AfterViewInit, ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { switchMap } from 'rxjs/operators';
import { ApiFirestoreService } from '../../../services/apis-firestore/api-firestore.service';
import { BreakpointService } from '../../../services/breakpoint/breakpoint.service';
import { Bid, PLAYER_POSITION, PlayerPosition, STATE, TableGame } from '../../../shared/models/collection-game';
import { Card, CardView } from '../../../shared/models/play';
import { CardImageService } from '../services/card-image.service';
import { PlayGameHelperService } from '../services/play-game-helper.service';

interface Tile {
  text: string;
  cols: number;
  rows: number;
  color: string;
}

@Component({
  selector: 'app-play-start',
  templateUrl: './play-game.component.html',
  styleUrls: ['./play-game.component.scss'],
})
export class PlayGameComponent implements OnInit, AfterViewInit {

  cardsPlayed: CardView[] = new Array<CardView>();
  map = new Map<number, string[]>();
  myCardMap: Map<string, CardView> = new Map<string, CardView>();
  cardNorth: CardView;
  backCardImg: string;
  backCardImgSmall: string;
  isSmallScreen: boolean;
  rowHeight: number;

  gameState: STATE;
  isMyCardsDisable: boolean;
  nicknameNorth: string;
  nicknameEast: string;
  nicknameSouth: string;
  nicknameWest: string;
  currentPlayer: string;
  currentBidType: any;
  currentBidNickname: string;

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
    private route: ActivatedRoute,
    private router: Router,
    private service: CardImageService,
    private breakpointService: BreakpointService,
    private firestoreService: ApiFirestoreService,
    private cd: ChangeDetectorRef,
    private helper: PlayGameHelperService
  ) {
    this.cd.detach();
  }

  ngOnInit(): void {
    const gameId: string = this.route.snapshot.params.id;
    this.cd.detectChanges();
    this.breakpointService.layoutChanges$()
      .pipe(switchMap(() => this.firestoreService.getTableGame(gameId)))
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
    console.log(event$);
    this.cardNorth = event$;
    this.cardsPlayed[0] = this.cardNorth;
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
      this.rowHeight = 100;
    }
    this.backCardImg = this.service.getBackCardImgSmall();
    this.backCardImgSmall = this.service.getBackCardImg();
    this.myCardMap = this.service.buildMyDeck(cards);
    this.cd.detectChanges();
  }

  private setTableGame(data: TableGame) {
    this.gameState = data.state;
    this.isMyCardsDisable = this.myCardsDisable(data.state, data.nextPlayer, data.myPosition);
    this.setNicknames(data.nicknames);
    this.currentPlayer = this.helper.getNicknameByPos(data.nextPlayer, data.nicknames);
    this.setBidding(data.currentBid, data.nicknames);
    this.cd.detectChanges();
  }

  private setNicknames(nicknames: PlayerPosition) {
    this.nicknameNorth = nicknames.NORTH;
    this.nicknameEast = nicknames.EAST;
    this.nicknameSouth = nicknames.SOUTH;
    this.nicknameWest = nicknames.WEST;
    console.log(nicknames.NORTH);
    this.cd.detectChanges();
  }

  private setBidding(currentBid: Bid, nicknames) {
    this.currentBidType = currentBid.type;
    this.currentBidNickname = this.helper.getNicknameByPos(currentBid.position, nicknames);
    this.cd.detectChanges();
  }

  private myCardsDisable(state: STATE, nextPlayer: PLAYER_POSITION, myPos: PLAYER_POSITION): boolean {
    return !(state === STATE.PLAYING && nextPlayer === myPos);
  }

}
