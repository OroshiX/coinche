import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { of } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { ApiGamesService } from '../../../services/apis/api-games.service';
import { BID_POINTS, TYPE_BID } from '../../../shared/models/collection-game';
import { AnnounceBid, CARD_COLOR } from '../../../shared/models/play';
import { DialogBidComponent } from '../dialog-comp-bid/dialog-bid.component';

@Component({
  selector: 'app-bid-announce',
  templateUrl: './bid-announce.component.html',
  styleUrls: ['./bid-announce.component.scss']
})
export class BidAnnounceComponent implements OnInit, OnChanges {
  @Input() isDisableBid: boolean;
  @Input() myPosition: string;
  @Input() gameId: string;
  @Input() currentBidPoints: number;
  @Output() announceBidData = new EventEmitter<any>();


  constructor(public dialog: MatDialog, private apiService: ApiGamesService) {
  }

  ngOnChanges(changes: SimpleChanges): void {
  }

  ngOnInit(): void {
  }


  bidPass(): void {
    const bid = new AnnounceBid({type: TYPE_BID.PASS, color: CARD_COLOR.HEART, points: BID_POINTS.EIGHTY});
    this.announceBidApi(bid)
      .subscribe(ret => this.announceBidData.emit(ret));
  }

  openDialog(): void {
    const dialogRef = this.dialog.open(DialogBidComponent, {
      width: '250px',
      data: {points: this.currentBidPoints}
    });

    dialogRef.afterClosed()
      .pipe(
        switchMap(result => {
          return !!result && !!result.points ? this.announceBidApi(result) : of(null);
        }))
      .subscribe(res => {
        this.announceBidData.emit(res);
      });
  }

  private announceBidApi(result: AnnounceBid) {
    if (!!result) {
      result.position = this.myPosition;
      return this.apiService.announceBid(this.gameId, result);
    }
    return null;
  }
}
