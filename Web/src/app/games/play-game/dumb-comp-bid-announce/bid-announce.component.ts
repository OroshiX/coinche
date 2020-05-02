import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { switchMap } from 'rxjs/operators';
import { ApiGamesService } from '../../../services/apis/api-games.service';
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

  openDialog(): void {
    const dialogRef = this.dialog.open(DialogBidComponent, {
      width: '250px',
      data: {}
    });

    dialogRef.afterClosed()
      .pipe(
        switchMap(result => {
          const bid = new AnnounceBid(result);
          bid.position = this.myPosition;
          return this.apiService.announceBid(this.gameId, bid);
        }))
      .subscribe(res => {
        console.log('The dialog was closed');
        this.announceBidData.emit(res);
      });
  }
}
