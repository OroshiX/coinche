import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { UserToken } from '../models/current-user';

@Injectable({
  providedIn: 'root'
})
export class LoginPageService {
  private userToken: BehaviorSubject<UserToken> = new BehaviorSubject<UserToken>(null);
  userToken$ = this.userToken.asObservable();

  constructor() {
  }

  getUserToken() {
    return this.userToken.value;
  }

  updateUserToken(userToken: UserToken) {
    this.userToken.next(userToken);
  }
}
