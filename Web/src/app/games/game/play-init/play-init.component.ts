import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { Component, OnInit } from '@angular/core';
import { CardImageService } from '../services/card-image.service';

@Component({
  selector: 'app-play-init',
  templateUrl: './play-init.component.html',
  styleUrls: ['./play-init.component.scss']
})
export class PlayInitComponent implements OnInit {

  map = new Map<number, string[]>();
  cardMap = new Map<string, string>();
  c2: any;
  cA: any;
  backCard: any;
  isActive = true;

  constructor(private service: CardImageService, private breakpointObserver: BreakpointObserver) {
    const layoutChanges = this.breakpointObserver.observe([
      '(orientation: portrait)',
      '(orientation: landscape)',
      Breakpoints.Medium,
      Breakpoints.Small
    ]);

    layoutChanges.subscribe(_ => {
      this.updateMyLayoutForOrientationChange();
    });


  }

  private updateMyLayoutForOrientationChange() {
    if (this.breakpointObserver.isMatched('(max-width: 959px)')) {
      this.map = this.service.getMapSmall();
      this.cardMap = this.service.getCardMapSmall();
      this.backCard = this.service.getBackCardSmall();
    } else {
      this.map = this.service.getMap();
      this.cardMap = this.service.getCardMap();
      this.backCard = this.service.getBackCard();
    }
    this.c2 = this.map.get(0)[0];
    this.cA = this.cardMap.get('c11');
    console.log(this.backCard);
  }

  ngOnInit(): void {
  }

  onClick(events: any) {
    console.log(events);
  }

}
