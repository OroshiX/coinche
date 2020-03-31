import { Injectable } from '@angular/core';

const bckgrndUrlImg = ` url("../../assets/images/1CPtk.png") no-repeat `;
const bckgrndUrlImgSmall = ` url("../../assets/images/1CPtkSmall.png") no-repeat `;

@Injectable({
  providedIn: 'root'
})
export class CardImageService {


  listPositionC = [
    `${bckgrndUrlImg} -480px 0px`,
    `${bckgrndUrlImg} -960px 0px`,
    `${bckgrndUrlImg} -1440px 0px`,
    `${bckgrndUrlImg} -1920px 0px`,
    `${bckgrndUrlImg} -2400px 0px`,
    `${bckgrndUrlImg} -2880px 0px`,
    `${bckgrndUrlImg} -3360px 0px`,
    `${bckgrndUrlImg} -3840px 0px`,
    `${bckgrndUrlImg}  0px 0px`,
    `${bckgrndUrlImg} -4800px 0px`,
    `${bckgrndUrlImg} -6000px 0px`,
    `${bckgrndUrlImg} -5520px 0px`,
    `${bckgrndUrlImg} -4320px 0px`
  ];

  listPositionCSmall = [
    `${bckgrndUrlImgSmall} -240px 0px`,
    `${bckgrndUrlImgSmall} -480px 0px`,
    `${bckgrndUrlImgSmall} -720px 0px`,
    `${bckgrndUrlImgSmall} -960px 0px`,
    `${bckgrndUrlImgSmall} -1200px 0px`,
    `${bckgrndUrlImgSmall} -1440px 0px`,
    `${bckgrndUrlImgSmall} -1680px 0px`,
    `${bckgrndUrlImgSmall} -1920px 0px`,
    `${bckgrndUrlImgSmall}  0px 0px`,
    `${bckgrndUrlImgSmall} -2400px 0px`,
    `${bckgrndUrlImgSmall} -3000px 0px`,
    `${bckgrndUrlImgSmall} -2760px 0px`,
    `${bckgrndUrlImgSmall} -2160px 0px`
  ];

  listPositionD = [
    `${bckgrndUrlImg} -600px 0px`,
    `${bckgrndUrlImg} -1080px 0px`,
    `${bckgrndUrlImg} -1560px 0px`,
    `${bckgrndUrlImg} -2040px 0px`,
    `${bckgrndUrlImg} -2520px 0px`,
    `${bckgrndUrlImg} -3000px 0px`,
    `${bckgrndUrlImg} -3480px 0px`,
    `${bckgrndUrlImg} -3960px 0px`,
    `${bckgrndUrlImg} -120px 0px`,
    `${bckgrndUrlImg} -4920px 0px`,
    `${bckgrndUrlImg} -6120px 0px`,
    `${bckgrndUrlImg} -5640px 0px`,
    `${bckgrndUrlImg} -4440px 0px`
  ];

  listPositionDSmall = [
    `${bckgrndUrlImgSmall} -300px 0px`,
    `${bckgrndUrlImgSmall} -540px 0px`,
    `${bckgrndUrlImgSmall} -780px 0px`,
    `${bckgrndUrlImgSmall} -1020px 0px`,
    `${bckgrndUrlImgSmall} -1260px 0px`,
    `${bckgrndUrlImgSmall} -1500px 0px`,
    `${bckgrndUrlImgSmall} -1740px 0px`,
    `${bckgrndUrlImgSmall} -1980px 0px`,
    `${bckgrndUrlImgSmall} -60px 0px`,
    `${bckgrndUrlImgSmall} -2460px 0px`,
    `${bckgrndUrlImgSmall} -3060px 0px`,
    `${bckgrndUrlImgSmall} -2820px 0px`,
    `${bckgrndUrlImgSmall} -2220px 0px`
  ];

  listPositionH = [
    `${bckgrndUrlImg} -720px 0px`,
    `${bckgrndUrlImg} -1200px 0px`,
    `${bckgrndUrlImg} -1680px 0px`,
    `${bckgrndUrlImg} -2160px 0px`,
    `${bckgrndUrlImg} -2640px 0px`,
    `${bckgrndUrlImg} -3120px 0px`,
    `${bckgrndUrlImg} -3600px 0px`,
    `${bckgrndUrlImg} -4080px 0px`,
    `${bckgrndUrlImg} -240px 0px `,
    `${bckgrndUrlImg} -5040px 0px`,
    `${bckgrndUrlImg} -6240px 0px`,
    `${bckgrndUrlImg} -5760px 0px`,
    `${bckgrndUrlImg} -4560px 0px`
  ];

  listPositionHSmall = [
    `${bckgrndUrlImgSmall} -360px 0px`,
    `${bckgrndUrlImgSmall} -600px 0px`,
    `${bckgrndUrlImgSmall} -840px 0px`,
    `${bckgrndUrlImgSmall} -1080px 0px`,
    `${bckgrndUrlImgSmall} -1320px 0px`,
    `${bckgrndUrlImgSmall} -1560px 0px`,
    `${bckgrndUrlImgSmall} -1800px 0px`,
    `${bckgrndUrlImgSmall} -2040px 0px`,
    `${bckgrndUrlImgSmall} -120px 0px `,
    `${bckgrndUrlImgSmall} -2520px 0px`,
    `${bckgrndUrlImgSmall} -3120px 0px`,
    `${bckgrndUrlImgSmall} -2880px 0px`,
    `${bckgrndUrlImgSmall} -2280px 0px`
  ];

  listPositionS = [
    `${bckgrndUrlImg} -840px 0px`,
    `${bckgrndUrlImg} -1320px 0px`,
    `${bckgrndUrlImg} -1800px 0px`,
    `${bckgrndUrlImg} -2280px 0px`,
    `${bckgrndUrlImg} -2760px 0px`,
    `${bckgrndUrlImg} -3240px 0px`,
    `${bckgrndUrlImg} -3720px 0px`,
    `${bckgrndUrlImg} -4200px 0px`,
    `${bckgrndUrlImg} -360px 0px`,
    `${bckgrndUrlImg} -5160px 0px`,
    `${bckgrndUrlImg} -6360px 0px`,
    `${bckgrndUrlImg} -5880px 0px`,
    `${bckgrndUrlImg} -4680px 0px`
  ];

  listPositionSSmall = [
    `${bckgrndUrlImgSmall} -420px 0px`,
    `${bckgrndUrlImgSmall} -660px 0px`,
    `${bckgrndUrlImgSmall} -900px 0px`,
    `${bckgrndUrlImgSmall} -1140px 0px`,
    `${bckgrndUrlImgSmall} -1380px 0px`,
    `${bckgrndUrlImgSmall} -1620px 0px`,
    `${bckgrndUrlImgSmall} -1860px 0px`,
    `${bckgrndUrlImgSmall} -2100px 0px`,
    `${bckgrndUrlImgSmall} -180px 0px`,
    `${bckgrndUrlImgSmall} -2580px 0px`,
    `${bckgrndUrlImgSmall} -3180px 0px`,
    `${bckgrndUrlImgSmall} -2940px 0px`,
    `${bckgrndUrlImgSmall} -2340px 0px`
  ];

  map = new Map<number, string[]>();
  mapSmall = new Map<number, string[]>();
  cardMapSmall = new Map<string, string>();
  cardMap = new Map<string, string>();

  constructor() {
  }

  processCardMap() {
    this.processMap();

    const listGroup = ['c', 'd', 'h', 's'];
    listGroup.forEach((groupId) => {
      for (let j = 0; j <= 12; j++) {
        switch (groupId) {
          case 'c':
            this.cardMapSmall.set(groupId + j, this.listPositionCSmall[j]);
            this.cardMap.set(groupId + j, this.listPositionC[j]);
            break;
          case 'd':
            this.cardMapSmall.set(groupId + j, this.listPositionDSmall[j]);
            this.cardMap.set(groupId + j, this.listPositionD[j]);
            break;
          case 'h':
            this.cardMapSmall.set(groupId + j, this.listPositionHSmall[j]);
            this.cardMap.set(groupId + j, this.listPositionH[j]);
            break;
          case 's':
            this.cardMapSmall.set(groupId + j, this.listPositionSSmall[j]);
            this.cardMap.set(groupId + j, this.listPositionS[j]);
            break;
        }
      }

    });
  }

  getCardMap() {
    this.processCardMap();
    return this.cardMap;
  }

  getCardMapSmall() {
    this.processCardMap();
    return this.cardMapSmall;
  }

  getMap() {
    this.processMap();
    return this.map;
  }

  getMapSmall() {
    this.processMap();
    return this.mapSmall;
  }

  private processMap() {
    this.mapSmall.set(0, this.listPositionCSmall);
    this.mapSmall.set(1, this.listPositionDSmall);
    this.mapSmall.set(2, this.listPositionHSmall);
    this.mapSmall.set(3, this.listPositionSSmall);
    this.map.set(0, this.listPositionC);
    this.map.set(1, this.listPositionD);
    this.map.set(2, this.listPositionH);
    this.map.set(3, this.listPositionS);
  }
}
