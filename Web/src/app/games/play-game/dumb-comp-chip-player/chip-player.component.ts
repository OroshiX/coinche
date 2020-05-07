import { AfterViewInit, Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'app-chip-player',
  templateUrl: './chip-player.component.html',
  styleUrls: ['./chip-player.component.scss']
})
export class ChipPlayerComponent implements OnInit, AfterViewInit {
  @Input() nextPlayerIdx: number;
  @Input() idx: number;
  @Input() nickname: string;
  @Input() isSmallScreen: boolean;

  constructor() {
  }

  ngOnInit(): void {
  }

  ngAfterViewInit(): void {
    console.log(this.isSmallScreen);
  }

}
