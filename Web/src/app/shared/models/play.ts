export enum SCREEN {
  SMALL= 'small',
  LARGE = 'large '
}
export class Play {
  value: number;
  color: string;
  belote: string;

  constructor(obj: Partial<Play>) {
    Object.assign(this, obj);
  }
}

export class AnnounceBid {
  points: number;
  color: string;
  coinche: string;

  constructor(obj: Partial<AnnounceBid>) {
    Object.assign(this, obj);
  }
}

export class PlayCard {
  value: number;
  color: string;
  belote: string;

  constructor(obj: Partial<PlayCard>) {
    Object.assign(this, obj);
  }
}

export class CardView {
  value: number;
  color: string;
  key: string;
  valuesMap: Map<number, MultiCardValues>;
  backgroundImg: string;
  playable: boolean;

  constructor(obj: Partial<CardView>) {
    obj.key = obj?.color?.toString().toUpperCase().charAt(0) + obj?.value.toString();
    Object.assign(this, obj);
  }
}

export enum CARD_COLOR {
  CLUB = 'CLUB', DIAMOND = 'DIAMOND', HEART = 'HEART', SPADE = 'SPADE'
}

export const ColorList = [CARD_COLOR.CLUB, CARD_COLOR.DIAMOND, CARD_COLOR.HEART, CARD_COLOR.SPADE];

export class MultiCardValues {
  value: number;
  atoutPoints: number;
  colorPoints: number;
  dominanceAtout: number;
  dominanceColor: number;

  constructor(obj: Partial<MultiCardValues>) {
    Object.assign(this, obj);
  }
}

export enum CARD_ID {
  SEVEN,
  EIGHT,
  NINE,
  TEN,
  JACK,
  QUEEN,
  KING,
  ACE
}

export const CARD_VALUES_LIST: number[][] = [
  [7, 0, 0, 0, 0],
  [8, 0, 0, 1, 1],
  [9, 14, 0, 6, 2],
  [10, 10, 10, 4, 6],
  [11, 20, 2, 7, 3],
  [12, 3, 3, 2, 4],
  [13, 4, 4, 3, 5],
  [1, 11, 11, 5, 7]
];


