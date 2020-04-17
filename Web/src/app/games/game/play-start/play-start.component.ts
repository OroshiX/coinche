import { CdkStepper } from '@angular/cdk/stepper';
import { AfterViewInit, ChangeDetectorRef, Component, OnInit, ViewChild } from '@angular/core';
import { BreakpointService } from '../../../services/breakpoint/breakpoint.service';
import { CARD_COLOR, CardView } from '../../../shared/models/play';
import { CardImageService } from '../services/card-image.service';

@Component({
  selector: 'app-play-start',
  templateUrl: './play-start.component.html',
  styleUrls: ['./play-start.component.scss'],
  // changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PlayStartComponent implements OnInit, AfterViewInit {
  @ViewChild('stepper') stepper: CdkStepper;

  map = new Map<number, string[]>();
  cardMap = new Map<string, CardView>();
  c2: any;
  cA: CardView;
  backCard: any;
  isActive = true;

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

  constructor(private service: CardImageService,
              private breakpointService: BreakpointService,
              private cd: ChangeDetectorRef) {
    this.cd.detach();
  }

  ngOnInit(): void {
    this.breakpointService.layoutChanges$()
      .subscribe(() => {
        this.updateLayoutForScreenChange();
      });
  }

  ngAfterViewInit(): void {
    /*this.cd.detectChanges();
    this.breakpointService.layoutChanges$()
      .subscribe(() => {
        this.updateLayoutForScreenChange();
      });*/
    // this.cd.detectChanges();
  }

  private updateLayoutForScreenChange() {
    if (this.breakpointService.isSmallScreen()) {
      this.map = this.service.getMapSmall();
      // this.cardMap = this.service.getCardMapSmall();
      this.backCard = this.service.getBackCardSmall();
      this.myCardMap = this.service.buildMyDeckSmall(this.listOfMyCard);
    } else {
      this.map = this.service.getMap();
      // this.cardMap = this.service.getCardMap();
      this.backCard = this.service.getBackCard();
      this.myCardMap = this.service.buildMyDeck(this.listOfMyCard);
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
