export class Game {
  id: string;
  nbJoined: number;
  nicknameCreator: string;
  name: string;
  gameType: string;

  constructor(obj: Partial<Game>) {
    Object.assign(this, obj);
  }
}

export class Trick {
  player1: string;
  player2: string;
  player3: string;
  player4: string;
  camp: string;

  constructor(obj: Partial<Trick>) {
    Object.assign(this, obj);
  }
}

export const GAME_TYPE_AUTO ='AUTOMATED';
