import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AnnounceBid, Play } from '../../shared/models/play';
import { ANNOUNCE_BID, API_BACKEND_GAMES, PLAY_CARD } from './api-constant';

@Injectable({
  providedIn: 'root'
})
export class ApiGamesService {

  constructor(private httpClient: HttpClient) { }

  playCard(gameId: string, playCard: Play): Observable<any> {
    const url = API_BACKEND_GAMES + '/' + gameId + '/' + PLAY_CARD;
    console.log(url);
    return this.httpClient.post(url, playCard, {observe: 'response'});
  }

  announceBid(gameId: string, announceBid: AnnounceBid) {
    const url = API_BACKEND_GAMES +  '/' + gameId + '/' + ANNOUNCE_BID;
    console.log(url);
    return this.httpClient.post(url, announceBid, {observe: 'response'})
  }
}
