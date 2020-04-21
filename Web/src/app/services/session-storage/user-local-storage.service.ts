import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { CurrentUser } from '../../shared/models/current-user';

const CURRENT_USER = 'currentUser';

@Injectable({
  providedIn: 'root'
})
export class UserLocalStorageService {
  private currentUserSubject: BehaviorSubject<CurrentUser>;
  public currentUser$: Observable<CurrentUser>;

  constructor() {
    this.currentUserSubject = new BehaviorSubject<CurrentUser>(JSON.parse(localStorage.getItem('currentUser')));
    this.currentUser$ = this.currentUserSubject.asObservable();
  }

  updateCurrentUser(user: CurrentUser) {
    localStorage.setItem(CURRENT_USER, JSON.stringify(user));
    this.currentUserSubject.next(user);
  }

  resetUser() {
    localStorage.removeItem(CURRENT_USER);
    this.currentUserSubject.next(null);
  }

  getCurrentUser(): CurrentUser {
    return this.currentUserSubject.value;
  }

  getCurrentUser$(): Observable<CurrentUser> {
    return this.currentUser$;
  }

  isConnected$(): Observable<boolean> {
    return of(!!this.getCurrentUser() && !!this.getCurrentUser().idToken);
  }

  isConnected(): boolean {
    return !!this.getCurrentUser() && !!this.getCurrentUser().idToken;
  }
}
