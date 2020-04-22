import { Component, Input, OnInit } from '@angular/core';
import { CardView } from '../../../shared/models/play';

@Component({
  selector: 'app-card',
  templateUrl: './card.component.html',
  styleUrls: ['./card.component.scss']
})
export class CardComponent implements OnInit {
  // tslint:disable-next-line:no-input-rename
  @Input('card') myCard: CardView;

  constructor() {
  }

  ngOnInit(): void {
    console.log(this.myCard);
  }

  onClickCard($event: any) {
    console.log($event);
  }

}
