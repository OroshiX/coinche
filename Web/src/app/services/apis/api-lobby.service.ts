import { HttpClient, HttpParams, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { catchError, map, shareReplay } from 'rxjs/operators';
import { Game } from '../../shared/models/game';
import { GameI } from '../../shared/models/game-interface';
import { ALL_GAMES, API_BACKEND_LOBBY, CREATE_GAME, JOIN_GAME, SET_NICKNAME } from './api-constant';
import { ApiErrorHandlerService } from './api-error-handler.service';


@Injectable({
  providedIn: 'root'
})
export class ApiLobbyService {

  constructor(private httpClient: HttpClient, private errorHandler: ApiErrorHandlerService) {
  }

  allGames(): Observable<GameI[]> {
    return this.httpClient.get<GameI[]>(API_BACKEND_LOBBY + ALL_GAMES)
      .pipe(
        map(games => games
          .map(g => {
              const r = g.name.includes('AUTOMATED') ?  '🤖 '.concat(g.name.replace('AUTOMATED', '')) :
                '👨‍👩‍👧‍👦 '.concat(g.name);
              g.name = r;
              return g;
            }
          )),
        shareReplay<GameI[]>(1),
        catchError(this.errorHandler.handleError));
  }

  createGame(game: Game): Observable<any> {
    return this.httpClient.post<string>(API_BACKEND_LOBBY + CREATE_GAME, game.name,
      {observe: 'response'})
      .pipe(
        catchError(this.errorHandler.handleError)
      );
  }

  joinGame(gameId: string, nickname: string | ''): Observable<any> {
    const parameters = new HttpParams().set('gameId', gameId).set('nickname', nickname);
    return this.httpClient.post<any>(API_BACKEND_LOBBY + JOIN_GAME, '',
      {params: parameters})
      .pipe(
        catchError(this.errorHandler.handleError)
      );
  }

  setNickname(nickname: string): Observable<HttpResponse<any>> {
    return this.httpClient.post<string>(API_BACKEND_LOBBY + SET_NICKNAME, nickname,
      {observe: 'response'})
      .pipe(
        catchError(this.errorHandler.handleError)
      );
  }
}
