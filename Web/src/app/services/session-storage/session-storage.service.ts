import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { CurrentUser } from '../../shared/models/user';
import { isNotNullAndNotUndefined } from '../../shared/utils/helper';

const CURRENT_USER = 'currentUser';

@Injectable({
  providedIn: 'root'
})
export class SessionStorageService {

  constructor() {
  }
  private userToken: BehaviorSubject<CurrentUser> = new BehaviorSubject<CurrentUser>(null);

  userToken$ = this.userToken.asObservable();

  static statusCurrentUser(usr: CurrentUser): boolean {
    return isNotNullAndNotUndefined(usr) && isNotNullAndNotUndefined(usr.idToken) && usr.idToken !== '';
  }

  static saveCurrentUserSession(currentUser: CurrentUser): void {
    sessionStorage.setItem(CURRENT_USER, JSON.stringify(currentUser));
  }

  static getCurrentUserSession() {
    return JSON.parse(sessionStorage.getItem(CURRENT_USER));
  }

  static clearCurrentUserSession():void  {
    sessionStorage.clear();
  }

  saveCurrentUser(currentUser: CurrentUser) {
    SessionStorageService.saveCurrentUserSession(currentUser);
    this.userToken.next(SessionStorageService.getCurrentUserSession());
  }

  getCurrentUserObs(): Observable<CurrentUser> {
    return this.userToken$;
  }

  getCurrentUser(): CurrentUser {
    return this.userToken.value;
  }

  isConnected(): boolean {
    return SessionStorageService.statusCurrentUser(this.getCurrentUser());
  }

  isConnectedObs(): Observable<boolean> {
    return this.getCurrentUserObs()
      .pipe(map(usr => SessionStorageService.statusCurrentUser(usr)));
  }

  resetCurrentUser() {
    SessionStorageService.clearCurrentUserSession();
    this.userToken.next(null);
  }
}
