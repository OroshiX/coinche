import { Card } from './play';

export interface TableGame {
  id: string;
  nicknames: PlayerPosition;
  cards: Card[];
  onTable: CardPlayed[];
  state: STATE;
  nextPlayer: PLAYER_POSITION;
  myPosition: PLAYER_POSITION;
  bids: Bid[];
  currentBid: Bid;
  score: Score;
  winnerLastTrick: PlayerPosition;
  lastTrick: CardPlayed[];
}

export interface Bid {
  position: PLAYER_POSITION;
  type: TYPE_BID;
}

export enum TYPE_BID {
  SIMPLE_BID = 'SimpleBid',
  CAPOT = 'Capot',
  GENERAL = 'General',
  PASS = 'Pass',
  COINCHE = 'Coinche',
}

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

export interface CardPlayed {
  color: string;
  value: number;
  etc: string;
}


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

export const playersPositionRef = [PLAYER_POSITION.NORTH, PLAYER_POSITION.EAST, PLAYER_POSITION.SOUTH, PLAYER_POSITION.WEST];
/*
export const playersPositionRef = ['NORTH', 'EAST', 'SOUTH', 'WEST'];
*/


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
