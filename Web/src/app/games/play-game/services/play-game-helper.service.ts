import { Injectable } from '@angular/core';
import { Bid, PLAYER_POSITION, PlayerPosition, playersPositionRef } from '../../../shared/models/collection-game';

const ZERO = 0;
const ONE = 1;

@Injectable({
  providedIn: 'root'
})
export class PlayGameHelperService {

  constructor() {
  }

  getNicknameByPos(currentPos: PLAYER_POSITION, nicknames: PlayerPosition): string {
    return currentPos === PLAYER_POSITION.NORTH ? nicknames.NORTH :
      currentPos === PLAYER_POSITION.EAST ? nicknames.EAST :
        currentPos === PLAYER_POSITION.SOUTH ? nicknames.SOUTH :
          currentPos === PLAYER_POSITION.WEST ? nicknames.WEST : '';
  }

  getPlayersNicknameByMyPos(myPos: PLAYER_POSITION, nicknames: PlayerPosition): string[] {
    const playersPos = this.playersPositionRefOrderedByMyPosZero(myPos);
    return playersPos.map((pos) => this.getNicknameByPos(pos, nicknames));
  }

  getIdxPlayer(myPos: PLAYER_POSITION, playerPos?: PLAYER_POSITION | undefined | null): number {
    return this.playersPositionRefOrderedByMyPosZero(myPos)
      .indexOf(playerPos === undefined || playerPos === null ? myPos : playerPos);
  }

  // my position must always be found at idx zero of this list
  playersPositionRefOrderedByMyPosZero(myPos: PLAYER_POSITION): PLAYER_POSITION[] {
    const listPosOut = [...playersPositionRef];
    const myPosIdxBase = playersPositionRef.indexOf(myPos);
    if (myPosIdxBase === ZERO) {
      return listPosOut;
    } else if (myPosIdxBase === ONE) {
      const firstEl = listPosOut.shift();
      return [...listPosOut, firstEl];
    } else {
      const firstArr = listPosOut.slice(myPosIdxBase);
      const restArr = listPosOut.slice(0, myPosIdxBase - 1);
      return [...firstArr, ...restArr];
    }
  }

  getBidsOrderByMyPos(myPos: PLAYER_POSITION, bids: Bid[]): Bid [] {
    const playerPosOrderedByMyPosZero = this.playersPositionRefOrderedByMyPosZero(myPos);
    return playerPosOrderedByMyPosZero
      .filter(pos => !!pos)
      .map(pos => bids
        .filter(bid => !!bid)
        .find(bid => bid.position === pos));
  }
}
