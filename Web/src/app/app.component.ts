import { Component, OnInit } from '@angular/core';
import { CardImageService } from './services/card-image.service';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {
  map = new Map<number, string[]>();
  cardMap = new Map<string, string>();
  c2: any;
  cA: any;

  constructor(private service: CardImageService, private breakpointObserver: BreakpointObserver) {
    const layoutChanges = this.breakpointObserver.observe([
      '(orientation: portrait)',
      '(orientation: landscape)',
      Breakpoints.Medium,
      Breakpoints.Small
    ]);

    layoutChanges.subscribe(result => {
      this.updateMyLayoutForOrientationChange();
    });


  }

  private updateMyLayoutForOrientationChange() {
    if (this.breakpointObserver.isMatched('(max-width: 959px)')) {
      this.map = this.service.getMapSmall();
      this.cardMap = this.service.getCardMapSmall();
    } else {
      this.map = this.service.getMap();
      this.cardMap = this.service.getCardMap();
    }
    this.c2 = this.map.get(0)[0];
    console.log(this.cardMap);
    this.cA = this.cardMap.get('c11');
    console.log(this.cA);
  }

  ngOnInit(): void {
  }

  onClick(events: any) {
    console.log(events);
  }

}
