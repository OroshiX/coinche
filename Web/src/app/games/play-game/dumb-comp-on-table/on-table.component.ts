import { Component, Input, OnInit } from '@angular/core';
import { CardView } from '../../../shared/models/play';

@Component({
  selector: 'app-on-table',
  templateUrl: './on-table.component.html',
  styleUrls: ['./on-table.component.scss']
})
export class OnTableComponent implements OnInit {
  @Input() cardsPlayed: CardView[];
  @Input() isSmallScreen: boolean;
  @Input() isOnTable: boolean;

  constructor() {
  }

  ngOnInit(): void {
  }

}
