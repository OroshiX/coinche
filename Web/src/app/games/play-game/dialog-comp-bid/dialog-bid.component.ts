import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { of } from 'rxjs';
import { bidPointListWithCapotNGeneral, TYPE_BID } from '../../../shared/models/collection-game';
import { CARD_COLOR } from '../../../shared/models/play';
import { DialogData } from '../play-game/play-game.component';

const MIN_POINTS = 80;
const IDX_MIN = 0;
const IDX_MAX_SIMPLE_BID = 10;
const IDX_CAPOT = 11;
const IDX_GENERAL = 12;
const IDX_POINTS_ALL_MAX = IDX_GENERAL;

@Component({
  selector: 'app-dialog-bid',
  templateUrl: './dialog-bid.component.html',
  styleUrls: ['./dialog-bid.component.scss']
})
export class DialogBidComponent {
  bidPoints = bidPointListWithCapotNGeneral;

  points: number;
  pointsLabel: string;
  color: string;
  typeBid: string;
  idxPoints: number;

  colorClub = false;
  colorDiam = false;
  colorSpade = false;
  colorHeart = false;

  constructor(public dialogRef: MatDialogRef<DialogBidComponent>,
              @Inject(MAT_DIALOG_DATA) public data: DialogData) {
    this.initializeData(data);
  }

  onNoClick(): void {
    this.dialogRef.close(of(null));
  }

  clickMinus() {
    this.idxPoints = this.idxPoints <= IDX_MIN ? IDX_MIN : (this.idxPoints - 1);
    this.setData();
  }

  clickPlus() {
    this.idxPoints = this.idxPoints >= IDX_POINTS_ALL_MAX ? this.idxPoints : (this.idxPoints + 1);
    this.setData();
  }

  clickColor(color: string) {
    this.setColor(color);
    this.setDataBasic();
  }

  clickPass() {
    this.typeBid = TYPE_BID.PASS;
    this.setDataBasic();
    this.dialogRef.close(this.data);
  }

  clickCoinche() {
    this.typeBid = TYPE_BID.COINCHE;
    this.setDataBasic();
    this.dialogRef.close(this.data);
  }

  private initializeData(data: DialogData) {
    this.points = data?.points;
    this.idxPoints = !!this.points && this.points >= MIN_POINTS ?
      this.bidPoints.map(el => el.value).indexOf(this.points) + 1 : IDX_MIN;
    this.setType();
    this.setPoints();
    this.setColor('');
  }

  private setData() {
    this.setType();
    this.setPoints();
    this.setDataBasic();
  }

  private setPoints() {
    this.idxPoints = this.idxPoints > IDX_POINTS_ALL_MAX ? this.idxPoints - 1 : this.idxPoints;
    this.points = this.bidPoints.map(el => el.value)[this.idxPoints];
    this.pointsLabel = this.bidPoints.map(el => el.label)[this.idxPoints];
  }

  private setColor(color) {
    this.color = color;
    switch (color) {
      case CARD_COLOR.CLUB :
        this.colorClub = true;
        this.colorDiam = false;
        this.colorSpade = false;
        this.colorHeart = false;
        break;
      case CARD_COLOR.DIAMOND :
        this.colorClub = false;
        this.colorDiam = true;
        this.colorSpade = false;
        this.colorHeart = false;
        break;
      case CARD_COLOR.SPADE :
        this.colorClub = false;
        this.colorDiam = false;
        this.colorSpade = true;
        this.colorHeart = false;
        break;
      case CARD_COLOR.HEART :
        this.colorClub = false;
        this.colorDiam = false;
        this.colorSpade = false;
        this.colorHeart = true;
        break;
      default:
        this.colorClub = false;
        this.colorDiam = false;
        this.colorSpade = false;
        this.colorHeart = false;
    }
  }

  private setType() {
    this.typeBid = this.idxPoints <= IDX_MAX_SIMPLE_BID ? TYPE_BID.SIMPLE_BID :
      this.idxPoints === IDX_CAPOT ? TYPE_BID.CAPOT : this.idxPoints === IDX_GENERAL ? TYPE_BID.GENERAL : '';
  }

  private setDataBasic() {
    this.data = {
      points: this.points,
      color: this.color,
      type: this.typeBid
    };
  }
}
