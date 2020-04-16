import { CdkStepper } from '@angular/cdk/stepper';
import { Component, OnInit, ViewChild } from '@angular/core';
import { BreakpointService } from '../../../services/breakpoint/breakpoint.service';
import { CARD_COLOR, CardView } from '../../../shared/models/play';
import { CardImageService } from '../services/card-image.service';

@Component({
  selector: 'app-play-start',
  templateUrl: './play-start.component.html',
  styleUrls: ['./play-start.component.scss']
})
export class PlayStartComponent implements OnInit {
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

  constructor(private service: CardImageService,
              private breakpointService: BreakpointService) {
    this.breakpointService.layoutChanges$()
      .subscribe(_ => {
        this.updateLayoutForScreenChange();
      });
  }

  private updateLayoutForScreenChange() {
    if (this.breakpointService.isSmallScreen()) {
      this.map = this.service.getMapSmall();
      console.log(this.map);
      // this.cardMap = this.service.getCardMapSmall();
      // this.backCard = this.service.getBackCardSmall();
    } else {
      this.map = this.service.getMap();
      console.log(this.map);
      //  this.cardMap = this.service.getCardMap();
      // this.backCard = this.service.getBackCard();
    }
    // console.log(this.cardMap.values());
    // console.log(this.cardMap);
    this.c2 = this.map.get(0)[0];
    // this.cA = this.cardMap.get('CLUB8');
    console.log(this.backCard);
  }

  ngOnInit(): void {
  }

  onClickCard(events: any, i: number, card: any) {
    console.log(events);
    console.log(i);
    console.log(card);
  }

  onNext() {
    this.stepper.next();
    console.log(this.stepper);
    console.log(this.stepper.selectedIndex);
  }

  onReset() {
    this.stepper.reset();
  }

}
