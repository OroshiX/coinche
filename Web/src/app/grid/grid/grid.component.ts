import { Component, OnInit } from '@angular/core';
import { CardImageService } from '../../games/game/services/card-image.service';
import { CARD_COLOR, CardView } from '../../shared/models/play';

export interface Tile {
  color: string;
  cols: number;
  rows: number;
  text: string;
}

@Component({
  selector: 'app-grid',
  templateUrl: './grid.component.html',
  styleUrls: ['./grid.component.scss']
})
export class GridComponent implements OnInit {
  myCardMap: Map<string, CardView> = new Map<string, CardView>();

  listOfMyCard = [
    {color: CARD_COLOR.HEART, value: 7},
    {color: CARD_COLOR.CLUB, value: 1},
    {color: CARD_COLOR.SPADE, value: 10},
    {color: CARD_COLOR.DIAMOND, value: 8},
  ];
  tiles: Tile[] = [
    {text: 'SOUTH', cols: 5, rows: 1, color: 'darkgreen'},
    {text: 'EAST', cols: 1, rows: 4, color: 'darkgreen'},
    {text: '', cols: 3, rows: 4, color: 'darkgreen'},
    {text: 'WEST', cols: 1, rows: 4, color: 'darkgreen'},
    {text: 'NORTH', cols: 5, rows: 2, color: 'grey'}
  ];

  constructor(private service: CardImageService) {
  }

  ngOnInit(): void {
    this.myCardMap = this.service.buildMyDeck(this.listOfMyCard);
    console.log(this.myCardMap);
  }

  onClickCard(events: any, i: number, card: any) {
    console.log(events);
    console.log(i);
    console.log(card);
  }
  onClickButton(event: any) {
    console.log(event);
  }

}
