import { HttpHeaders } from '@angular/common/http';

// outside game
export const API_BACKEND = 'http://www.hornik.fr:8080/';
export const LOGIN = 'loginToken';
export const LOGOUT = 'logout';
export const ALL_GAMES = 'allGames';
export const CREATE_GAME = 'createGame';
export const JOIN_GAME = 'joinGame';

// in game
export const API_BACKEND_GAMES = API_BACKEND + 'games';
export const GET_TABLE = 'getTable';
export const PLAY_CARD = 'playCard';
export const ANNOUNCE_BID = 'announceBid';
export const SHOW_LAST_TRICK = 'showLastTrick';
export const SHOW_ALL_TRICKS = 'showAllTricks';
export const GET_SCORE = 'getScore';
export const GET_CARDS ='getCards';

export const httpOptions = new HttpHeaders({
  'Content-Type': 'application/json', Authorization: 'auth-token'
});
