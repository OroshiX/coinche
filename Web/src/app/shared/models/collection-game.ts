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
