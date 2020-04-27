import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'app-on-table-bidding',
  templateUrl: './on-table-bidding.component.html',
  styleUrls: ['./on-table-bidding.component.scss']
})
export class OnTableBiddingComponent implements OnInit {
  @Input() isSmallScreen: boolean;
  constructor() { }

  ngOnInit(): void {
  }

}
