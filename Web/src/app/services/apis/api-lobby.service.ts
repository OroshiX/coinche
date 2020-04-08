import { HttpClient, HttpParams, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { catchError, shareReplay } from 'rxjs/operators';
import { GameI } from '../../shared/models/game-interface';
import { ALL_GAMES, API_BACKEND, API_BACKEND_LOBBY, CREATE_GAME, JOIN_GAME } from './api-constant';
import { ApiErrorHandlerService } from './api-error-handler.service';


@Injectable({
  providedIn: 'root'
})
export class ApiLobbyService {

  constructor(private httpClient: HttpClient, private errorHandler: ApiErrorHandlerService) {
  }

  allGames(): Observable<GameI[]> {
    console.log(API_BACKEND + 'lobby/' + ALL_GAMES);
    return this.httpClient.get<GameI[]>(API_BACKEND_LOBBY + ALL_GAMES)
      .pipe(
        shareReplay<GameI[]>(1),
        catchError(this.errorHandler.handleError));
  }

  createGame(): Observable<HttpResponse<any>> {
    return this.httpClient.post<string>(API_BACKEND_LOBBY + CREATE_GAME, '',
      {observe: 'response'});
  }

  joinGame(gameId: string, nickname: string): Observable<HttpResponse<any>> {
    const parameters = new HttpParams();
    parameters.append('gameId', gameId);
    parameters.append('nickname', nickname);
    return this.httpClient.post<string>(API_BACKEND_LOBBY + JOIN_GAME, '',
      {params: parameters, observe: 'response'});
  }
}
