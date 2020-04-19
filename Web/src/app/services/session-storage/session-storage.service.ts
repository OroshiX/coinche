import { Injectable } from '@angular/core';
import { AngularFireAuth } from '@angular/fire/auth';
import * as firebase from 'firebase';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { isNotNullAndNotUndefined } from '../../shared/utils/helper';

@Injectable({
  providedIn: 'root'
})
export class SessionStorageService {
  // Firebase
  private user: BehaviorSubject<Observable<firebase.User>> =
    new BehaviorSubject<Observable<firebase.User>>(this.afAuth.authState);
  user$ = this.user?.asObservable()
    .pipe(switchMap((user: Observable<firebase.User>) => user));

  // Local
  private currentUser: BehaviorSubject<firebase.User> = new BehaviorSubject<firebase.User>(null);
  currentUser$ = this.currentUser?.asObservable();

  private userUid: BehaviorSubject<string> = new BehaviorSubject<string>('');
  userUid$ = this.userUid.asObservable();

  constructor(private afAuth: AngularFireAuth) {
    this.afAuth.authState.subscribe((usr: firebase.User | null) => {
      this.currentUser.next(usr);
      if (isNotNullAndNotUndefined(usr)) {
        this.userUid.next(usr.uid);
      }
    });
  }

  // Firebase
  getUser$(): Observable<firebase.User> {
    return this.user$;
  }

  updateUser() {
    // this.afAuth.authState.subscribe(res => console.log(res));
    this.user.next(this.afAuth.authState);
  }

  resetUser() {
    this.user.next(of(null));
  }

  isLogin(): Observable<boolean> {
    return this.getUser$()
      .pipe(
        map(user =>
          isNotNullAndNotUndefined(user)
          && (isNotNullAndNotUndefined(user.uid) || user.uid !== '')));
  }

  // Local
  isConnected(): Observable<boolean> {
    return this.getCurrentUser$().pipe(
      map(user =>
        isNotNullAndNotUndefined(user)
        && (isNotNullAndNotUndefined(user.uid) || user.uid !== '')));
  }

  saveCurrentUser(usr: firebase.User) {
    this.currentUser.next(usr);
  }

  resetCurrentUser() {
    this.currentUser.next(null);
  }

  getCurrentUser$(): Observable<firebase.User> {
    return this.currentUser$;
  }

  getCurrentUser(): firebase.User {
    return this.currentUser.value;
  }

  resetCurrentUserUid() {
    this.userUid.next('');
  }

  getUserId$(): Observable<string> {
    return this.userUid$;
  }

  getUserUid(): string {
    return this.userUid.value;
  }
}
