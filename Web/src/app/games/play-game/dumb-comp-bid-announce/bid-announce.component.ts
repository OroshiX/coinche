import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { switchMap } from 'rxjs/operators';
import { ApiGamesService } from '../../../services/apis/api-games.service';
import { TYPE_BID } from '../../../shared/models/collection-game';
import { AnnounceBid } from '../../../shared/models/play';
import { DialogBidComponent } from '../dialog-comp-bid/dialog-bid.component';

@Component({
  selector: 'app-bid-announce',
  templateUrl: './bid-announce.component.html',
  styleUrls: ['./bid-announce.component.scss']
})
export class BidAnnounceComponent implements OnInit {
  @Input() isDisableBid: boolean;
  @Input() myPosition: string;
  @Input() gameId: string;
  @Output() announceBidData = new EventEmitter<any>();


  constructor(public dialog: MatDialog, private apiService: ApiGamesService) {
  }

  ngOnInit(): void {
  }

  bidPass(): void {
    const bid = new AnnounceBid({type: TYPE_BID.PASS});
    this.announceBidApi(bid)
      .subscribe(res => this.announceBidData.emit(res));
  }

  openDialog(): void {
    const dialogRef = this.dialog.open(DialogBidComponent, {
      width: '250px',
      data: {}
    });

    dialogRef.afterClosed()
      .pipe(
        switchMap(result => {
          return this.announceBidApi(result);
        }))
      .subscribe(res => {
        console.log('The dialog was closed');
        this.announceBidData.emit(res);
      });
  }

  private announceBidApi(result: AnnounceBid) {
    result.position = this.myPosition;
    return this.apiService.announceBid(this.gameId, result);
  }
}
