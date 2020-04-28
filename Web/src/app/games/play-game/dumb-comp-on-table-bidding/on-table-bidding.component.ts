import { Component, Input, OnInit } from '@angular/core';
import { Bid } from '../../../shared/models/collection-game';

@Component({
  selector: 'app-on-table-bidding',
  templateUrl: './on-table-bidding.component.html',
  styleUrls: ['./on-table-bidding.component.scss']
})
export class OnTableBiddingComponent implements OnInit {
  @Input() isSmallScreen: boolean;
  @Input() bidListOrdered: Bid[];

  constructor() {
  }

  ngOnInit(): void {
  }

}
