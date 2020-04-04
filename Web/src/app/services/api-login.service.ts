import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';

const API_BACKEND = 'http://www.hornik.fr:8080';
const LOGIN = 'loginToken';

@Injectable({
  providedIn: 'root'
})
export class ApiLoginService {

  constructor(private httpClient: HttpClient) { }

  loginToServer(idToken: any) {
    const httpOptions = new HttpHeaders({
      'Content-Type': 'application/json', Authorization: 'auth-token'
    });
    return this.httpClient.post<any>(API_BACKEND + '/' + LOGIN, idToken, {headers: httpOptions});
  }
}
