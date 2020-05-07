import { Injectable } from '@angular/core';
import { Bid, PLAYER_POSITION, PlayerPosition, playersPositionRef, STATE, TableGame } from '../../../shared/models/collection-game';
import { CardView, Play } from '../../../shared/models/play';
import { CardImageService } from './card-image.service';

const ZERO = 0;
const ONE = 1;

@Injectable({
  providedIn: 'root'
})
export class PlayGameHelperService {

  constructor(private imgService: CardImageService) {
  }

  getNicknameByPos(currentPos: PLAYER_POSITION, nicknames: PlayerPosition): string {
    return currentPos === PLAYER_POSITION.NORTH ? nicknames.NORTH :
      currentPos === PLAYER_POSITION.EAST ? nicknames.EAST :
        currentPos === PLAYER_POSITION.SOUTH ? nicknames.SOUTH :
          currentPos === PLAYER_POSITION.WEST ? nicknames.WEST : '';
  }

  getNicknamesPairByPos(playerPosEast: PLAYER_POSITION, playerPosWest: PLAYER_POSITION, nicknames: PlayerPosition): string {
    return `${this.getNicknameByPos(playerPosEast, nicknames)}/${this.getNicknameByPos(playerPosWest, nicknames)}`;
  }

  getPlayersNicknameByMyPos(myPos: PLAYER_POSITION, nicknames: PlayerPosition): string[] {
    const playersPos = this.playersPositionRefOrderedByMyPosZero(myPos);
    return playersPos.map((pos) => this.getNicknameByPos(pos, nicknames));
  }

  getIdxPlayer(myPos: PLAYER_POSITION, playerPos?: PLAYER_POSITION | undefined | null): number {
    return this.playersPositionRefOrderedByMyPosZero(myPos)
      .indexOf(playerPos === undefined || playerPos === null ? myPos : playerPos);
  }

  mustHideCardsOntable(data: TableGame): boolean {
    const ret = (data.state === STATE.PLAYING && data.onTable.length > 0 &&
      data.onTable.map(e => `${e?.card?.color}${e?.card?.value}`)
        .every(c => data.lastTrick.map(e => `${e?.card?.color}${e?.card?.value}`).includes(c))
    );

    return ret;
  }

  onTableCardsOrdered(myPos: PLAYER_POSITION, onTable: Play[]): CardView[] {
    const playersPos = this.playersPositionRefOrderedByMyPosZero(myPos);
    return playersPos.map(pos => onTable.find((p: Play) => p.position === pos))
      .map(pl => !!pl ? this.imgService.buildCardView(pl?.card?.color, pl?.card?.value, pl?.card?.playable) : new CardView(null));
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
    const bidsProceeded = (bids.length > 4) ? [...bids.slice(length - 4)] : [...bids];
    return playerPosOrderedByMyPosZero
      .filter(pos => !!pos)
      .map(pos => bidsProceeded.filter(bid => !!bid).find(bid => bid.position === pos));
  }
}
