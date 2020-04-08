import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { API_BACKEND_GAMES, GET_TABLE } from './api-constant';

@Injectable({
  providedIn: 'root'
})
export class ApiGamesService {

  constructor(private httpClient: HttpClient) { }

  getTable(gameId: string): Observable<any> {
    const url = API_BACKEND_GAMES + gameId + '/' + GET_TABLE;
    console.log(url);
    return this.httpClient.get(url);
  }
}
