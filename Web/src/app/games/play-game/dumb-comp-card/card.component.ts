import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges } from '@angular/core';
import { CardView } from '../../../shared/models/play';

@Component({
  selector: 'app-card',
  templateUrl: './card.component.html',
  styleUrls: ['./card.component.scss']
})
export class CardComponent implements OnInit, OnChanges {
  @Input() card: CardView;
  @Input() isSmallScreen: boolean;
  @Input() isDisable: boolean;
  @Input() isOnTable: boolean;
  @Output() cardChosen = new EventEmitter<CardView>();

  constructor() {
  }

  ngOnInit() {
  }

  onClickCard(card: CardView) {
    console.log(card);
    console.log(JSON.stringify(this.card));
    this.cardChosen.emit(card);
  }

  ngOnChanges(changes: SimpleChanges): void {
  }

}
