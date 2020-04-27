import { Component, Input, OnInit } from '@angular/core';
import { CardView } from '../../../shared/models/play';

@Component({
  selector: 'app-on-table-playing',
  templateUrl: './on-table-playing.component.html',
  styleUrls: ['./on-table-playing.component.scss']
})
export class OnTablePlayingComponent implements OnInit {
  @Input() cardsPlayed: CardView[];
  @Input() isSmallScreen: boolean;

  constructor() {
  }

  ngOnInit(): void {
  }

}
