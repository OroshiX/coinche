import { Component, OnInit } from '@angular/core';
import { CardImageService } from './services/card-image.service';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';

/*const bckgrndUrlImg = ` url("../../assets/images/1CPtk.png") no-repeat `;
const bckgrndUrlImgSmall = ` url("../../assets/images/1CPtkSmall.png") no-repeat `;

const WIDTH_XS = 30;
const WIDTH_SMALL = 60;
const WIDTH_LARGE = 120;
const PX = 'px';
const ZERO = 0;*/

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {
  map = new Map<number, string[]>();

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
    this.map = this.breakpointObserver.isMatched('(max-width: 959px)') ?
      this.service.getMapSmall() : this.service.getMap();
  }

  ngOnInit(): void {}
}
