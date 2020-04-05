import {HttpClient, HttpHeaders, HttpResponse} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';

const API_BACKEND = 'http://www.hornik.fr:8080';
// const API_BACKEND = 'http://192.168.1.11:8080';
const LOGIN = 'loginToken';

@Injectable({
  providedIn: 'root'
})
export class ApiLoginService {

  constructor(private httpClient: HttpClient) {
  }

  loginToServer(idToken: any): Observable<HttpResponse<any>> {
    const httpOptions = new HttpHeaders({
      'Content-Type': 'application/json', Authorization: 'auth-token'
    });
    return this.httpClient.post<HttpResponse<any>>(API_BACKEND + '/' + LOGIN, idToken,
      {headers: httpOptions, observe: 'response'});
  }
}
