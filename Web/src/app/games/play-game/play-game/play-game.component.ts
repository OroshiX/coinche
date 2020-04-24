import { AfterViewInit, ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { switchMap } from 'rxjs/operators';
import { ApiFirestoreService } from '../../../services/apis-firestore/api-firestore.service';
import { BreakpointService } from '../../../services/breakpoint/breakpoint.service';
import { TableGame } from '../../../shared/models/collection-game';
import { Card, CardView } from '../../../shared/models/play';
import { CardImageService } from '../services/card-image.service';

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
  gameState: any;
  cardsPlayed: CardView[] = new Array<CardView>();
  map = new Map<number, string[]>();
  myCardMap: Map<string, CardView> = new Map<string, CardView>();
  cardNorth: CardView;
  backCardImg: string;
  backCardImgSmall: string;
  isSmallScreen: boolean;
  rowHeight: number;

  data: any;

  tiles: Tile[] = [
    {text: 'SOUTH', cols: 5, rows: 1, color: 'darkgreen'},
    {text: 'EAST', cols: 1, rows: 4, color: 'darkgreen'},
    {text: '', cols: 3, rows: 4, color: 'darkgreen'},
    {text: 'WEST', cols: 1, rows: 4, color: 'darkgreen'},
    {text: 'NORTH', cols: 5, rows: 4, color: 'darkgreen'}
  ];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private service: CardImageService,
    private breakpointService: BreakpointService,
    private firestoreService: ApiFirestoreService,
    private cd: ChangeDetectorRef
  ) {
    this.cd.detach();
  }

  ngOnInit(): void {
    // route.data includes both `data` and `resolve`
    // const user = this.route.data.pipe(map(d => d.user));
    const gameId: string = this.route.snapshot.params.id;
    this.cd.detectChanges();
    this.breakpointService.layoutChanges$()
      .pipe(switchMap(() => this.firestoreService.getTableGame(gameId)))
      .subscribe((data: TableGame) => {
        console.log(JSON.stringify(data));
        this.updateLayoutForScreenChange(data.cards);
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

}
