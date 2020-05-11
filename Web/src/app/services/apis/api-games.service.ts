import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { AnnounceBid, PlayCard } from '../../shared/models/play';
import { ANNOUNCE_BID, API_BACKEND_GAMES, PLAY_CARD, SHOW_ALL_TRICKS } from './api-constant';
import { ApiErrorHandlerService } from './api-error-handler.service';

@Injectable({
  providedIn: 'root'
})
export class ApiGamesService {

  constructor(private httpClient: HttpClient, private errorHandler: ApiErrorHandlerService) {
  }

  playCard(gameId: string, playCard: PlayCard): Observable<any> {
    const url = API_BACKEND_GAMES + '/' + gameId + '/' + PLAY_CARD;
    console.log(url);
    return this.httpClient.post(url, playCard, {observe: 'response'})
      .pipe(
        catchError(this.errorHandler.handleError)
      );
  }

  announceBid(gameId: string, announceBid: AnnounceBid) {
    const url = API_BACKEND_GAMES + '/' + gameId + '/' + ANNOUNCE_BID;
    return this.httpClient.post(url, announceBid, {observe: 'response'})
      .pipe(
        catchError(this.errorHandler.handleError)
      );
  }

  showAllTricks(gameId: string): Observable<any> {
    const url =  API_BACKEND_GAMES + '/' + gameId + '/' + SHOW_ALL_TRICKS;
    return this.httpClient.get(url)
      .pipe(catchError(this.errorHandler.handleError));
  }
}
