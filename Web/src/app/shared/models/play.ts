import { enumToEntries, enumToValues } from '../utils/helper';
import { PLAYER_POSITION } from './collection-game';

export interface KeyValue {
  id: string;
  value: any;
}

export interface Card {
  color: CARD_COLOR;
  value: CARD_VALUE;
  playable?: boolean;
}

export enum SCREEN {
  SMALL = 'small',
  LARGE = 'large '
}

export class Play {
  card: Card;
  belote: string;
  position: PLAYER_POSITION;

  constructor(obj: Partial<Play>) {
    Object.assign(this, obj);
  }
}

export class AnnounceBid {
  points: number;
  color: string;
  position: string;
  type: string;

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
  id: number;     // 0  from [0, 1, 2, 3, 4, 5, 6, 7]
  value: number;  // 7  from [7, 8, 9, 10, 11, 12, 13, 1]
  color: string;  // HEART  FROM [CLUB, DIAMOND, HEARD, SPADE]
  key: string;  // ex. HEART0 from `${color}${id}`
  valuesMap: MultiValuesCard;
  backgroundImg: string;
  backgroundImgSmall: string;
  playable: boolean;

  constructor(obj: Partial<CardView>) {
    if (obj === undefined || obj === null) {
      Object.assign(this, null);
    } else {
      obj.id = cardValueMapToCardId(obj.value);
      obj.key = `${obj.color.toUpperCase()}${obj.id}`;  // ex. HEART0 - HEART7
      obj.valuesMap = createValuesMap(obj.id);
      Object.assign(this, obj);
    }
  }
}

export enum CARD_COLOR {
  CLUB = 'CLUB', DIAMOND = 'DIAMOND', HEART = 'HEART', SPADE = 'SPADE'
}

export class MultiValuesCard {
  value: number;
  atoutPoints: number;
  colorPoints: number;
  dominanceAtout: number;
  dominanceColor: number;
  key: number;

  constructor(obj: Partial<MultiValuesCard>) {
    Object.assign(this, obj);
  }
}

export class Belote {
  nickname: string;
  position: string;
  belote: string;

  constructor(obj: Partial<Belote>) {
    Object.assign(this, obj);
  }
}

export class AllTrickFlat {
  camp: string;
  color: string;
  value: number;
  belote: string;
  position: string;
  winnerTrick: string;

  constructor(obj: Partial<AllTrickFlat>) {
    Object.assign(this, obj);
  }
}

export class EmojiCardLabel {
  cardLabel: string;
  cardColor: string;
  cardLabelEmoji: any;
  cardEmoji: string;
  isBlack: boolean;
  camp: string;

  constructor(obj: Partial<EmojiCardLabel>) {
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

export enum CARD_VALUE {
  SEVEN = 7,
  EIGHT,
  NINE,
  TEN,
  JACK,
  QUEEN,
  KING,
  ACE = 1
}

export const cardIdList = enumToValues(CARD_ID);
export const cardValueEntriesList = enumToEntries(CARD_VALUE);

export const cardValues = enumToValues(CARD_VALUE);

export const CARD_VALUES_LIST: number[][] = [
  [CARD_VALUE.SEVEN, 0, 0, 0, 0, CARD_ID.SEVEN],
  [CARD_VALUE.EIGHT, 0, 0, 1, 1, CARD_ID.EIGHT],
  [CARD_VALUE.NINE, 14, 0, 6, 2, CARD_ID.NINE],
  [CARD_VALUE.TEN, 10, 10, 4, 6, CARD_ID.TEN],
  [CARD_VALUE.JACK, 20, 2, 7, 3, CARD_ID.JACK],
  [CARD_VALUE.QUEEN, 3, 3, 2, 4, CARD_ID.QUEEN],
  [CARD_VALUE.KING, 4, 4, 3, 5, CARD_ID.KING],
  [CARD_VALUE.ACE, 11, 11, 5, 7, CARD_ID.ACE]
];

export function cardValueMapToCardId(value: CARD_VALUE): number {
  return +cardIdList[cardValues.indexOf(value)];
}

function createValuesMap(cardId: number): MultiValuesCard {
  return new MultiValuesCard({
    value: CARD_VALUES_LIST[cardId][0],
    atoutPoints: CARD_VALUES_LIST[cardId][1],
    colorPoints: CARD_VALUES_LIST[cardId][2],
    dominanceAtout: CARD_VALUES_LIST[cardId][3],
    dominanceColor: CARD_VALUES_LIST[cardId][4],
    key: CARD_VALUES_LIST[cardId][5]
  });
}
