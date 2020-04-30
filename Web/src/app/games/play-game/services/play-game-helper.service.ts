import { Injectable } from '@angular/core';
import { Bid, PLAYER_POSITION, PlayerPosition, playersPositionRef, TYPE_BID } from '../../../shared/models/collection-game';

const ZERO = 0;
const ONE = 1;
const TWO = 2;
const THREE = 3;

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
    const bidTemp = [...bids];
    const bidIdxMyPos = bids.findIndex(bid => bid.position === myPos);
    if (bidIdxMyPos === ZERO) {        // Me is a bidder at idx ZERO at pos 180°
      console.log('zero', JSON.stringify([...bids]));
      return [...bids];
    } else if (bidIdxMyPos === ONE) {  // Me is a bidder at idx ONE at 270°
      console.log('ONE');
      const firstEl = bidTemp.shift();
      console.log('zero', JSON.stringify([...bidTemp, firstEl]));
      return [...bidTemp, firstEl];
    } else if (bidIdxMyPos === TWO || bidIdxMyPos === THREE) { // Me is a bidder at idx TWO or THREE at pos 0° or 90°
      console.log('ONE');
      const firstArr = bidTemp.slice(bidIdxMyPos);
      const restArr = bidTemp.slice(0, bidIdxMyPos - 1);
      console.log(JSON.stringify([...firstArr, ...restArr]));
      return [...firstArr, ...restArr];
    } else {  // undefined ?
      console.log('undefined', JSON.stringify([{position: myPos, type: TYPE_BID.PASS}, ...bids]));
      return [{position: myPos, type: TYPE_BID.PASS}, ...bids];
    }
  }

  /*getBidsOrderByNorthFirst(bids: any): Bid[] {
    console.log(bids);
    const bidListOrdered = [...bids];
    console.log(JSON.stringify(bidListOrdered));
    if (bids[0].position === PLAYER_POSITION.NORTH) {
      console.log('NORTH');
      console.log([...bids]);
      return [...bids];
    } else if (bids[0].position === PLAYER_POSITION.EAST) {
      console.log('EAST');
      const firstEl = bidListOrdered.shift();
      console.log(firstEl);
      console.log([...bidListOrdered, firstEl]);
      return [...bidListOrdered, firstEl];
    } else {
      console.log('SOUTH or WEST');
      const idx = playersPositionRef.indexOf(bids[0].position);
      const firstArr = bidListOrdered.slice(idx);
      const restArr = bidListOrdered.slice(0, idx - 1);
      console.log(firstArr);
      console.log(restArr);
      console.log([...firstArr, ...restArr]);
      return [...firstArr, ...restArr];
    }
  }*/

  /*getBidsOrderByMyPos(myPos: PLAYER_POSITION, bids: Bid[]): Bid [] {
    const bidListOrdered = [...bids];
    const bidIdxMyPos = bids.findIndex(bid => bid.position === myPos);
    if (bidIdxMyPos === ZERO) {        // Player NORTH is at 180° => idx === ZERO
      console.log('zero', JSON.stringify(bidListOrdered));
      return bidListOrdered;
    } else if (bidIdxMyPos === ONE) {  // Player NORTH is at 270° => idx === ONE
      console.log('ONE');
      const firstEl = bidListOrdered.shift();
      console.log('zero', JSON.stringify([...bidListOrdered, firstEl]));
      return [...bidListOrdered, firstEl];
    } else {                      // Player NORTH is at 0° or 90° => idx === TWO or THREE
      console.log('ONE');
      const firstArr = bidListOrdered.slice(bidIdxMyPos);
      const restArr = bidListOrdered.slice(0, bidIdxMyPos - 1);
      console.log(JSON.stringify([...firstArr, ...restArr]))
      return [...firstArr, ...restArr];
    }
  }*/
}
