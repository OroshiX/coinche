import { Component, Input, OnInit } from '@angular/core';
import { CardView } from '../../../shared/models/play';

@Component({
  selector: 'app-my-cards',
  templateUrl: './my-cards.component.html',
  styleUrls: ['./my-cards.component.scss']
})
export class MyCardsComponent implements OnInit {
  @Input() myCardMap: Map<string, CardView>;
  @Input() isSmallScreen: boolean;
  @Input() cardPlayed: boolean;

  cardNorth: CardView;
  cardSouth: CardView;

  constructor() {
  }

  ngOnInit(): void {
  }

  onDblClickCard(events: any, i: number, card: any) {
    console.log(events);
    console.log(i);
    console.log(card);
    i % 2 === 0 ? this.cardNorth = card : this.cardSouth = card;
    /*this.cardsPlayed[0] = card;
    this.cardsPlayed[1] = card;
    this.cardsPlayed[2] = card;
    this.cardsPlayed[3] = card;*/
  }

}
