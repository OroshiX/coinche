import { CdkStepper } from '@angular/cdk/stepper';
import { AfterViewInit, ChangeDetectorRef, Component, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { switchMap } from 'rxjs/operators';
import { ApiFirestoreService } from '../../../services/apis-firestore/api-firestore.service';
import { BreakpointService } from '../../../services/breakpoint/breakpoint.service';
import { TableGame } from '../../../shared/models/collection-game';
import { Card, CARD_COLOR, CardView } from '../../../shared/models/play';
import { CardImageService } from '../services/card-image.service';

@Component({
  selector: 'app-play-start',
  templateUrl: './play-start.component.html',
  styleUrls: ['./play-start.component.scss'],
})
export class PlayStartComponent implements OnInit, AfterViewInit {
  @ViewChild('stepper') stepper: CdkStepper;

  map = new Map<number, string[]>();
  cardMap = new Map<string, CardView>();
  c2: any;
  cA: CardView;
  backCard: any;
  isActive = true;

  data: any;

  listOfMyCard = [
    {color: CARD_COLOR.HEART, value: 7},
    {color: CARD_COLOR.SPADE, value: 8},
    {color: CARD_COLOR.CLUB, value: 7},
    {color: CARD_COLOR.SPADE, value: 10},
    {color: CARD_COLOR.DIAMOND, value: 8},
    {color: CARD_COLOR.HEART, value: 12},
    {color: CARD_COLOR.DIAMOND, value: 13},
    {color: CARD_COLOR.HEART, value: 1},
  ];

  myCardMap: Map<string, CardView> = new Map<string, CardView>();

  constructor(
    private route: ActivatedRoute,
    private service: CardImageService,
    private breakpointService: BreakpointService,
    private firestoreService: ApiFirestoreService,
    private cd: ChangeDetectorRef
  ) {
    this.cd.detach();
  }

  ngOnInit(): void {
    // const gameId = 'DE6nV9kAg5YB8mMaJlEh';
    // route.data includes both `data` and `resolve`
    // const user = this.route.data.pipe(map(d => d.user));
    const gameId: string = this.route.snapshot.params.id;
    console.log(gameId);
    this.breakpointService.layoutChanges$()
      .pipe(switchMap(() => this.firestoreService.getTableGame(gameId)))
      .subscribe((data: TableGame) => {
        this.updateLayoutForScreenChange(data.cards);
      });
  }

  ngAfterViewInit(): void {
  }

  private updateLayoutForScreenChange(cards: Card[]) {
    if (this.breakpointService.isSmallScreen()) {
      this.map = this.service.getMapSmall();
      this.backCard = this.service.getBackCardSmall();
      this.myCardMap = this.service.buildMyDeckSmall(cards);
    } else {
      this.map = this.service.getMap();
      this.backCard = this.service.getBackCard();
      this.myCardMap = this.service.buildMyDeck(cards);
    }
    this.cd.detectChanges();
  }

  onClickCard(events: any, i: number, card: any) {
    console.log(events);
    console.log(i);
    console.log(card);
    this.cd.detectChanges();
  }

  onNext() {
    this.stepper.next();
    this.cd.detectChanges();
  }

  onReset() {
    this.stepper.reset();
    this.cd.detectChanges();
  }

}
