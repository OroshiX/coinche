import { STATE } from './collection-game';
import { Play } from './play';

export interface GameI {
  id: string,
  nbJoined: number,
  nicknameCreator: string,
  name: string;
  state: STATE;
  inRoom: boolean;
}

export interface AllTrick {
  camp: string,
  trick: Play[]
}

