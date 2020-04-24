import { Component, Input, OnChanges, OnInit, SimpleChanges } from '@angular/core';
import { CardView } from '../../../shared/models/play';

@Component({
  selector: 'app-card',
  templateUrl: './card.component.html',
  styleUrls: ['./card.component.scss']
})
export class CardComponent implements OnInit, OnChanges {
  @Input() card: CardView;
  @Input() isSmallScreen: boolean;
  @Input() cardPlayed: boolean;

  constructor() {
  }

  ngOnInit() {
  }

  onClickCard($event: any) {
    console.log($event);
    console.log(JSON.stringify(this.card));
  }

  ngOnChanges(changes: SimpleChanges): void {
  }

}
