import { Injectable } from '@angular/core';
import { AngularFireAuth } from '@angular/fire/auth';
import { BehaviorSubject, Observable } from 'rxjs';
import { switchMap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class SessionStorageService {

  private user: BehaviorSubject<Observable<firebase.User>> =
    new BehaviorSubject<Observable<firebase.User>>(null);
  user$ = this.user.asObservable()
    .pipe(switchMap((user: Observable<firebase.User>) => user));

  private userUid: BehaviorSubject<string> = new BehaviorSubject<string>('');
  userUid$ = this.userUid.asObservable();

  constructor(private afAuth: AngularFireAuth) {
    this.updateUser();
  }

  getUser$(): Observable<firebase.User> {
    return this.user$;
  }

  updateUser() {
    this.user.next(this.afAuth.authState);
  }

  updateUserUid(userUid: string) {
    this.userUid.next(userUid);
  }

  getUserUid() {
    return this.userUid.value;
  }
}
