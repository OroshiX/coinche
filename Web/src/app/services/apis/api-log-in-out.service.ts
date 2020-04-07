import { HttpClient, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { API_BACKEND, LOGIN, LOGOUT } from './api-constant';

@Injectable({
  providedIn: 'root'
})
export class ApiLogInOutService {

  constructor(private httpClient: HttpClient) {
  }

  loginToServer(idToken: any): Observable<HttpResponse<any>> {
    return this.httpClient.post<string>(API_BACKEND + LOGIN, idToken, {observe: 'response'});
  }

  logoutToServer(): Observable<HttpResponse<any>> {
    return this.httpClient.post<HttpResponse<any>>(API_BACKEND + LOGOUT, '', {observe: 'response'});
  }
}
