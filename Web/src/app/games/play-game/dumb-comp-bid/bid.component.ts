import { Component, Input, OnInit } from '@angular/core';
import { Bid } from '../../../shared/models/collection-game';

@Component({
  selector: 'app-bid',
  templateUrl: './bid.component.html',
  styleUrls: ['./bid.component.scss']
})
export class BidComponent implements OnInit {
  @Input() isSmallScreen: boolean;
  @Input() bid: Bid;

  constructor() {
  }

  ngOnInit(): void {
  }

}
