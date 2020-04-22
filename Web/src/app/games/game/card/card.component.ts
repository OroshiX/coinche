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

  constructor() {
  }

  ngOnInit(): void {
  }

  onClickCard($event: any) {
  }

  ngOnChanges(changes: SimpleChanges): void {
  }

}
