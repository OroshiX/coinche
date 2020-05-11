import { Component, Input, OnChanges, OnInit, SimpleChanges } from '@angular/core';
import { Belote, CardView } from '../../../shared/models/play';

@Component({
  selector: 'app-last-trick',
  templateUrl: './last-trick.component.html',
  styleUrls: ['./last-trick.component.scss']
})
export class LastTrickComponent implements OnInit, OnChanges {
  @Input() lastTricks: CardView[];
  @Input() winnerLastTrick;
  @Input() belote: Belote;
  constructor() { }

  ngOnInit(): void {
  }

  ngOnChanges(changes: SimpleChanges): void {
    console.log(JSON.stringify(this.belote));
  }

}
