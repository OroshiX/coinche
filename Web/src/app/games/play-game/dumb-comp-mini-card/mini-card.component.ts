import { Component, Input, OnInit } from '@angular/core';
import { CardView } from '../../../shared/models/play';

@Component({
  selector: 'app-mini-card',
  templateUrl: './mini-card.component.html',
  styleUrls: ['./mini-card.component.scss']
})
export class MiniCardComponent implements OnInit {
  @Input() card: CardView;
  @Input() isSmallScreen: boolean;
  @Input() isDisable: boolean;

  constructor() { }

  ngOnInit(): void {
  }

}
