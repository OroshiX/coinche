import { Component, OnInit } from '@angular/core';
import { BreakpointService } from '../../../services/breakpoint/breakpoint.service';
import { CardImageService } from '../services/card-image.service';

@Component({
  selector: 'app-play-init',
  templateUrl: './play-init.component.html',
  styleUrls: ['./play-init.component.scss']
})
export class PlayInitComponent implements OnInit {

  map = new Map<number, string[]>();
  C2: any;
  backCard: any;
  isSmallScreen: boolean;

  constructor(private service: CardImageService, private breakpointService: BreakpointService) {
    this.breakpointService.layoutChanges$()
      .subscribe(_ => {
        this.updateMyLayoutForScreenChange();
      });
  }

  private updateMyLayoutForScreenChange() {
    if (this.breakpointService.isSmallScreen()) {
      this.map = this.service.getMapSmall();
      this.backCard = this.service.getBackCardImgSmall();
    } else {
      this.map = this.service.getMap();
      this.backCard = this.service.getBackCardImg();
    }
    this.C2 = this.map.get(0)[0];
  }

  ngOnInit(): void {
  }

  onClick(events: any) {
    console.log(events);
  }

}
