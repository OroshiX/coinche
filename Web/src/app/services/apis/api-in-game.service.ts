import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class ApiInGameService {

  constructor(private httpClient: HttpClient) { }

  getTable(gameId: string) {

  }
  /*
   allGames(): Observable<Array<Game>> {
    return this.httpClient.get<Array<Game>>(API_BACKEND + ALL_GAMES);
  }

  createGame(): Observable<HttpResponse<any>> {
    return this.httpClient.post<HttpResponse<any>>(API_BACKEND + CREATE_GAME, '',
      {headers: httpOptions, observe: 'response'});
  }

  joinGame(gameId: string, nickname: string): Observable<HttpResponse<any>> {
    const parameters = new HttpParams();
    parameters.append('gameId', gameId);
    parameters.append('nickname', nickname);
    return this.httpClient.post<HttpResponse<any>>(API_BACKEND + JOIN_GAME, '',
      {headers: httpOptions, params: parameters, observe: 'response'});
  }
   */
}
