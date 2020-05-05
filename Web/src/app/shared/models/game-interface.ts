import { STATE } from './collection-game';

export interface GameI {
  id: string,
  nbJoined: number,
  nicknameCreator: string,
  name: string;
  state: STATE;
  inRoom: boolean;
}
