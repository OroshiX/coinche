import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map, tap } from 'rxjs/operators';
import { ApiLobbyService } from '../../../services/apis/api-lobby.service';
import { GameI } from '../../../shared/models/game-interface';

@Injectable({
  providedIn: 'root'
})
export class HelperService {

  constructor(private apiService: ApiLobbyService) {
  }

  getAllGames(): Observable<GameI[]> {
    return this.apiService.allGames()
      .pipe(
        map((games: GameI[]) =>
            games.map(game => {
              console.log(game);
              game.name.replace('AUTOMATED', '');
              console.log(game);
              return game;
            })
        ),
        tap(g => console.log(JSON.stringify(g))));
  }
}
