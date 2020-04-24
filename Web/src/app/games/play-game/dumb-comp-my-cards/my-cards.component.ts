import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { CardView } from '../../../shared/models/play';

@Component({
  selector: 'app-my-cards',
  templateUrl: './my-cards.component.html',
  styleUrls: ['./my-cards.component.scss']
})
export class MyCardsComponent implements OnInit {
  @Input() myCardMap: Map<string, CardView>;
  @Input() isSmallScreen: boolean;
  @Input() isDisable: boolean;
  @Output() cardChosen = new EventEmitter<CardView>();

  constructor() {
  }

  ngOnInit(): void {
  }

  onCardChosen(event: any) {
    console.log(event);
    this.cardChosen.emit(event);
  }

}
