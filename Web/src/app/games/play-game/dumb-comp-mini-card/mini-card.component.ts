import { Component, Input, OnChanges, OnInit, SimpleChanges } from '@angular/core';
import { CARD_VALUE, CardView } from '../../../shared/models/play';
import { enumToEntries } from '../../../shared/utils/helper';

@Component({
  selector: 'app-mini-card',
  templateUrl: './mini-card.component.html',
  styleUrls: ['./mini-card.component.scss']
})
export class MiniCardComponent implements OnInit, OnChanges {
  @Input() card: CardView;
  cardLabel: string;

  constructor() {
    this.cardList = enumToEntries(CARD_VALUE);
  }

  cardList: any[];

  ngOnInit(): void {
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (!!this.card && this.card.value) {
      const tmp = this.cardList
        .filter(el => !!el)
        .find(k => +this.card.value === +k[1]);
      const tmp1 = tmp[0];
      this.cardLabel = ['ACE', 'KING', 'QUEEN', 'JACK'].includes(tmp1) ? tmp1.charAt(0) : this.card?.value;
    }
  }

}
