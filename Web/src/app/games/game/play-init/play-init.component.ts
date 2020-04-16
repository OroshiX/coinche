import { Component, OnInit } from '@angular/core';
import { BreakpointService } from '../../../services/breakpoint/breakpoint.service';
import { CardView } from '../../../shared/models/play';
import { CardImageService } from '../services/card-image.service';

@Component({
  selector: 'app-play-init',
  templateUrl: './play-init.component.html',
  styleUrls: ['./play-init.component.scss']
})
export class PlayInitComponent implements OnInit {

  map = new Map<number, string[]>();
  cardMap = new Map<string, CardView>();
  C2: any;
  CA: CardView;
  backCard: any;
  isActive = true;

  constructor(private service: CardImageService, private breakpointService: BreakpointService) {
    this.breakpointService.layoutChanges$()
      .subscribe(_ => {
        this.updateMyLayoutForScreenChange();
      });
  }

  private updateMyLayoutForScreenChange() {
    if (this.breakpointService.isSmallScreen()) {
      this.map = this.service.getMapSmall();
      this.cardMap = this.service.getCardMapSmall();
      this.backCard = this.service.getBackCardSmall();
    } else {
      this.map = this.service.getMap();
      this.cardMap = this.service.getCardMap();
      this.backCard = this.service.getBackCard();
    }
    this.C2 = this.map.get(0)[0];
    this.CA = this.cardMap.get('CLUB6');
    console.log(this.CA);
    console.log(this.cardMap.get('CLUB6'));
    console.log(this.backCard);
  }

  ngOnInit(): void {
  }

  onClick(events: any) {
    console.log(events);
  }

}
