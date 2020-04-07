export class Game {
  id: string;
  nbJoined: number;
  nicknameCreator: string;

  constructor(obj: Partial<Game>) {
    Object.assign(this, obj);
  }
}
