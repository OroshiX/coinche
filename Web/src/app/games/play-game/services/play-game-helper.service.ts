import { Injectable } from '@angular/core';
import { PLAYER_POSITION, PlayerPosition } from '../../../shared/models/collection-game';

@Injectable({
  providedIn: 'root'
})
export class PlayGameHelperService {

  constructor() { }

  getNicknameByPos(currentPos: PLAYER_POSITION, nicknames: PlayerPosition): string {
    const nickname =
      currentPos === PLAYER_POSITION.NORTH ? nicknames.NORTH :
        currentPos === PLAYER_POSITION.EAST ? nicknames.EAST :
          currentPos === PLAYER_POSITION.SOUTH ? nicknames.SOUTH :
            currentPos === PLAYER_POSITION.WEST ? nicknames.WEST : '';
    return nickname;
  }
}
