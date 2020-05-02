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

  inactiveCard: boolean;

  constructor() {
  }

  ngOnInit() {
  }

  onClickCard(card: CardView) {
    this.cardChosen.emit(card);
  }

  ngOnChanges(changes: SimpleChanges): void {
    this.inactiveCard = !this.isOnTable && this.card?.playable === false;
    // console.log(this.card?.color, this.card?.value, this.inactiveCard);
  }

}
