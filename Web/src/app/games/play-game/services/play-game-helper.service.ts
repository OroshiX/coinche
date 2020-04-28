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

  getPlayersOrderByMyPos(myPos: PLAYER_POSITION, nicknames: PlayerPosition): string[] {
    const playersPosition = this.getNicknameListBase(nicknames);   // Player NORTH is at 0°
    const idx180 = this.getIdxPlayer(myPos);

    if (idx180 === ZERO) {        // Player NORTH is at 180° => idx === ZERO
      return playersPosition;
    } else if (idx180 === ONE) {  // Player NORTH is at 270° => idx === ONE
      const firstEl = playersPosition.shift();
      return [...playersPosition, firstEl];
    } else {                      // Player NORTH is at 0° or 90° => idx === TWO or THREE
      const firstArr = playersPosition.slice(idx180);
      const restArr = playersPosition.slice(0, idx180 - 1);
      return [...firstArr, ...restArr];
    }
  }

  getNicknameListBase(nicknames: PlayerPosition): string [] {
    return [nicknames.NORTH, nicknames.EAST, nicknames.SOUTH, nicknames.WEST];
  }

  getIdxPlayer(playerPos: PLAYER_POSITION): number {
    return playersPositionRef.indexOf(playerPos);
  }

  getBidsOrderByMyPos(myPos: PLAYER_POSITION, bids: Bid[]): Bid [] {
    const bidListOrdered = [...bids];
    const bidIdxMyPos = bids.findIndex(bid => bid.position === myPos);
    if (bidIdxMyPos === ZERO) {        // Player NORTH is at 180° => idx === ZERO
      return bidListOrdered;
    } else if (bidIdxMyPos === ONE) {  // Player NORTH is at 270° => idx === ONE
      const firstEl = bidListOrdered.shift();
      return [...bidListOrdered, firstEl];
    } else {                      // Player NORTH is at 0° or 90° => idx === TWO or THREE
      const firstArr = bidListOrdered.slice(bidIdxMyPos);
      const restArr = bidListOrdered.slice(0, bidIdxMyPos - 1);
      return [...firstArr, ...restArr];
    }
  }
}
