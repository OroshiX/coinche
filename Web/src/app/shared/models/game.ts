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
