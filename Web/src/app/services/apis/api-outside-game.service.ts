import { HttpClient, HttpParams, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { GameI } from '../../shared/models/game-interface';
import { ALL_GAMES, API_BACKEND, CREATE_GAME, httpOptions, JOIN_GAME } from './api-constant';


@Injectable({
  providedIn: 'root'
})
export class ApiOutsideGameService {

  constructor(private httpClient: HttpClient) {
  }

  allGames(): Observable<GameI[]> {
    console.log(API_BACKEND + ALL_GAMES);
    return this.httpClient.get<GameI[]>(API_BACKEND + ALL_GAMES);
  }

  createGame(): Observable<HttpResponse<any>> {
    return this.httpClient.post<string>(API_BACKEND + CREATE_GAME, '',
      {observe: 'response'});
  }

  joinGame(gameId: string, nickname: string): Observable<HttpResponse<any>> {
    const parameters = new HttpParams();
    parameters.append('gameId', gameId);
    parameters.append('nickname', nickname);
    return this.httpClient.post<string>(API_BACKEND + JOIN_GAME, '',
      {params: parameters, observe: 'response'});
  }
}
