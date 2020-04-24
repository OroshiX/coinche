import { Injectable } from '@angular/core';
import { Card, CARD_COLOR, CARD_VALUE, cardValueMapToCardId, CardView } from '../../../shared/models/play';

const bckgrndUrlImg = ` url("../../assets/images/1CPtk.png") no-repeat`;
const bckgrndUrlImgSmall = ` url("../../assets/images/1CPtkSmall.png") no-repeat`;
const backCardImg = `url("../../assets/images/back-card-120X174.png") no-repeat`;
const backCardImgSmall = `url("../../assets/images/back-card-60X87.png") no-repeat`;


@Injectable({
  providedIn: 'root'
})
export class CardImageService {

  constructor() {
  }


  listPositionC = [
    /*`${bckgrndUrlImg} -480px 0px`,
    `${bckgrndUrlImg} -960px 0px`,
    `${bckgrndUrlImg} -1440px 0px`,
    `${bckgrndUrlImg} -1920px 0px`,
    `${bckgrndUrlImg} -2400px 0px`,*/
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
    /*`${bckgrndUrlImgSmall} -240px 0px`,
    `${bckgrndUrlImgSmall} -480px 0px`,
    `${bckgrndUrlImgSmall} -720px 0px`,
    `${bckgrndUrlImgSmall} -960px 0px`,
    `${bckgrndUrlImgSmall} -1200px 0px`,*/
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
    /*`${bckgrndUrlImg} -600px 0px`,
    `${bckgrndUrlImg} -1080px 0px`,
    `${bckgrndUrlImg} -1560px 0px`,
    `${bckgrndUrlImg} -2040px 0px`,
    `${bckgrndUrlImg} -2520px 0px`,*/
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
    /*`${bckgrndUrlImgSmall} -300px 0px`,
    `${bckgrndUrlImgSmall} -540px 0px`,
    `${bckgrndUrlImgSmall} -780px 0px`,
    `${bckgrndUrlImgSmall} -1020px 0px`,
    `${bckgrndUrlImgSmall} -1260px 0px`,*/
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
    /*`${bckgrndUrlImg} -720px 0px`,
    `${bckgrndUrlImg} -1200px 0px`,
    `${bckgrndUrlImg} -1680px 0px`,
    `${bckgrndUrlImg} -2160px 0px`,
    `${bckgrndUrlImg} -2640px 0px`,*/
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
    /*`${bckgrndUrlImgSmall} -360px 0px`,
    `${bckgrndUrlImgSmall} -600px 0px`,
    `${bckgrndUrlImgSmall} -840px 0px`,
    `${bckgrndUrlImgSmall} -1080px 0px`,
    `${bckgrndUrlImgSmall} -1320px 0px`,*/
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
    /*`${bckgrndUrlImg} -840px 0px`,
    `${bckgrndUrlImg} -1320px 0px`,
    `${bckgrndUrlImg} -1800px 0px`,
    `${bckgrndUrlImg} -2280px 0px`,
    `${bckgrndUrlImg} -2760px 0px`,*/
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
    /*`${bckgrndUrlImgSmall} -420px 0px`,
    `${bckgrndUrlImgSmall} -660px 0px`,
    `${bckgrndUrlImgSmall} -900px 0px`,
    `${bckgrndUrlImgSmall} -1140px 0px`,
    `${bckgrndUrlImgSmall} -1380px 0px`,*/
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
  myCardMap = new Map<string, CardView>();

  buildCardMap(color: CARD_COLOR,
               value: CARD_VALUE) {
    const key = color + value;
    const cardId = cardValueMapToCardId(value);
    switch (color) {
      case CARD_COLOR.CLUB:
        this.myCardMap.set(key,
          this.createCardView(CARD_COLOR.CLUB, value, this.listPositionC[cardId], this.listPositionCSmall[cardId]));
        break;
      case CARD_COLOR.DIAMOND:
        this.myCardMap.set(key,
          this.createCardView(CARD_COLOR.DIAMOND, value, this.listPositionD[cardId], this.listPositionDSmall[cardId]));
        break;
      case CARD_COLOR.HEART:
        this.myCardMap.set(key,
          this.createCardView(CARD_COLOR.HEART, value, this.listPositionH[cardId], this.listPositionHSmall[cardId]));
        break;
      case CARD_COLOR.SPADE:
        this.myCardMap.set(key,
          this.createCardView(CARD_COLOR.SPADE, value, this.listPositionS[cardId], this.listPositionSSmall[cardId]));
        break;
    }
  }

  buildMyDeck(list: Card[]): Map<string, CardView> {
    const sortedCardList = this.sortList(list);
    sortedCardList.forEach(card => this.buildCardMap(card.color, card.value));
    return this.myCardMap;
  }

  private sortList(list: Card[]): Card[] {
    const clubList = this.sortPerColor(list, CARD_COLOR.CLUB);
    const diamondList = this.sortPerColor(list, CARD_COLOR.DIAMOND);
    const heartList = this.sortPerColor(list, CARD_COLOR.HEART);
    const spadeList = this.sortPerColor(list, CARD_COLOR.SPADE);
    return [...clubList, ...diamondList, ...heartList, ...spadeList];
  }

  private sortPerColor(list: Card[], color: CARD_COLOR): Card[] {
    const clubList = list.filter(c => c.color === color)
      .sort((a, b) => (a.value > b.value) ? 1 : (a.value < b.value) ? -1 : 0);
    const idx = clubList.findIndex(card => card.value === CARD_VALUE.ACE);
    if (idx >= 0 ) {
      const ace = clubList.shift();
      return [...clubList, ace];
    }
    return [...clubList];
  }

  private createCardView(col: string, val: number, imageUrl: string, imageUrlSmall: string): CardView {
    return new CardView({
      color: col,
      value: val,
      backgroundImg: imageUrl,
      backgroundImgSmall: imageUrlSmall
    });
  }

  getBackCardImg() {
    return backCardImg;
  }

  getBackCardImgSmall() {
    return backCardImgSmall;
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
