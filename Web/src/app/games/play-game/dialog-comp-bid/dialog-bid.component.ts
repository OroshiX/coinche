import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { bidPointListWithCapotNGeneral, TYPE_BID } from '../../../shared/models/collection-game';
import { CARD_COLOR } from '../../../shared/models/play';
import { DialogData } from '../play-game/play-game.component';
import { iconClub, iconDiamond, iconHeart, iconSpade } from '../services/card-image.service';

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
  clubImg = iconClub;
  spadeImg = iconSpade;
  heartImg = iconHeart;
  diamondImg = iconDiamond;
  points: number;
  pointsLabel: string;
  color: string;
  typeBid: string;
  idxPoints: number;
  idxPointsMin = 0;

  constructor(public dialogRef: MatDialogRef<DialogBidComponent>,
              @Inject(MAT_DIALOG_DATA) public data: DialogData) {
    this.initializeData(data);
    console.log(this.bidPoints);
  }

  onNoClick(): void {
    this.dialogRef.close();
  }

  clickMinus() {
    this.idxPoints = this.idxPoints <= 0 ? 0 : (this.idxPoints - 1);
    this.setData();
  }

  clickPlus() {
    this.idxPoints = this.idxPoints >= IDX_POINTS_ALL_MAX ? this.idxPoints : (this.idxPoints + 1);
    console.log(this.idxPoints);
    this.setData();
  }

  clickClub() {
    this.setColor(CARD_COLOR.CLUB);
    this.setDataBasic();
  }

  clickDiamond() {
    this.setColor(CARD_COLOR.DIAMOND);
    this.setDataBasic();
  }

  clickSpade() {
    this.setColor(CARD_COLOR.SPADE);
    this.setDataBasic();
  }

  clickHeart() {
    this.setColor(CARD_COLOR.HEART);
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
    /*this.idxPointsMin = (this.bidPoints.indexOf(data.points) >= 0) &&
    (this.bidPoints.indexOf(data.points) <= IDX_MAX_SIMPLE_BID) ?
      this.bidPoints.indexOf(data.points) + 1 : 0;*/
    /*if () {

    }*/
    this.points = data?.points;
    this.idxPoints = !!this.points && this.points >= 80 ?
      this.bidPoints.map(el => el.value).indexOf(this.points) : this.idxPointsMin;
    this.pointsLabel = this.bidPoints[this.idxPoints].label;
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
    this.points = this.bidPoints[this.idxPoints].value;
    this.pointsLabel = this.bidPoints[this.idxPoints].label;
  }

  private setColor(color) {
    this.color = color;
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
