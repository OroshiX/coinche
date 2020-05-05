import { enumToValues } from '../utils/helper';
import { Card, CARD_COLOR, Play } from './play';

export interface TableGame {
  id: string;
  nicknames: PlayerPosition;
  cards: Card[];
  onTable: Play[];
  state: STATE;
  nextPlayer: PLAYER_POSITION;
  myPosition: PLAYER_POSITION;
  bids: Bid[];
  currentBid: Bid;
  score: Score;
  winnerLastTrick: PLAYER_POSITION;
  lastTrick: Play[];
}

export interface Bid {
  position?: PLAYER_POSITION;
  type?: TYPE_BID;
  points?: BID_POINTS;
  color?: CARD_COLOR;
}

export enum TYPE_BID {
  SIMPLE_BID = 'SimpleBid',
  CAPOT = 'Capot',
  GENERAL = 'General',
  PASS = 'Pass',
  COINCHE = 'Coinche',
}

export class BidIdxValue {
  label: string;
  value: number;
  idx: number;
  constructor(obj: Partial<BidIdxValue>) {
    Object.assign(this, obj);
  }
}

export const bidTypeList = enumToValues(TYPE_BID);

export enum BID_POINTS {
  EIGHTY = 80,
  NINETY = EIGHTY + 10,
  HUNDRED = EIGHTY + 2 * 10,
  HUNDRED_AND_TEN = EIGHTY + 3 * 10,
  HUNDRED_AND_TWENTY = EIGHTY + 4 * 10,
  HUNDRED_AND_THIRTY = EIGHTY + 5 * 10,
  HUNDRED_AND_FORTY = EIGHTY + 6 * 10,
  HUNDRED_AND_FIFTY = EIGHTY + 7 * 10,
  HUNDRED_AND_SIXTY = EIGHTY + 8 * 10,
  HUNDRED_AND_SEVENTY = EIGHTY + 9 * 10,
  HUNDRED_AND_EIGHTY = EIGHTY + 10 * 10,
}

export const bidPointList: number[] = enumToValues(BID_POINTS);

const capot =  new BidIdxValue({label: TYPE_BID.CAPOT, value: 200, idx: 11});
const general =  new BidIdxValue({label: TYPE_BID.GENERAL, value: 220, idx: 12});
export const bidPointListWithCapotNGeneral: BidIdxValue[] = bidPointList.map((v, i) =>
  new BidIdxValue({label: v.toString(), idx: i, value: v}));
bidPointListWithCapotNGeneral.push(capot);
bidPointListWithCapotNGeneral.push(general);


/*export interface CardPlayed {
  color: string;
  value: number;
  etc: string;
}*/


export enum STATE {
  JOINING = 'JOINING',
  DISTRIBUTING = 'DISTRIBUTING',
  BIDDING = 'BIDDING',
  PLAYING = 'PLAYING',
  BETWEEN_GAMES = 'BETWEEN_GAMES',
  ENDED = 'ENDED'
}

export enum PLAYER_POSITION {
  NORTH = 'NORTH',
  EAST = 'EAST',
  SOUTH = 'SOUTH',
  WEST = 'WEST'
}

// export const playersPositionRef = [PLAYER_POSITION.NORTH, PLAYER_POSITION.EAST, PLAYER_POSITION.SOUTH, PLAYER_POSITION.WEST];
export const playersPositionRef = enumToValues(PLAYER_POSITION);


export interface Score {
  northSouth: number;
  eastWest: number;
}

export interface PlayerPosition {
  NORTH: string;
  EAST: string;
  SOUTH: string;
  WEST: string;
}
