import { Component, Input, OnInit } from '@angular/core';
import { CardView } from '../../../shared/models/play';

@Component({
  selector: 'app-last-trick',
  templateUrl: './last-trick.component.html',
  styleUrls: ['./last-trick.component.scss']
})
export class LastTrickComponent implements OnInit {
  @Input() lastTricks: CardView[];
  @Input() winnerLastTrick;
  constructor() { }

  ngOnInit(): void {
  }

}
