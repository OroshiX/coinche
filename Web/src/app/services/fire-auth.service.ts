import { Injectable, NgZone } from '@angular/core';
import { AngularFireAuth } from '@angular/fire/auth';
import { Router } from '@angular/router';
import { auth } from 'firebase';
import { BehaviorSubject } from 'rxjs';
import { User } from '../models/user';

@Injectable({
  providedIn: 'root'
})
export class FireAuthService {
  user: User;
  private userToken: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
  userToken$ = this.userToken.asObservable();

  constructor(
    public router: Router,
    public ngZone: NgZone,
    public afAuth: AngularFireAuth,
  ) {
    this.afAuth.authState.subscribe(user => {
      this.user = user;
    });
  }

  // Firebase SignInWithPopup
  oAuthProvider(provider) {
    return this.afAuth.signInWithPopup(provider)
      .then((res) => {
        this.ngZone.run(() => {
          this.router.navigate(['play']);
        });
      }).catch((error) => {
        window.alert(error);
      });
  }

  // Firebase Google Sign-in
  signinWithGoogle() {
    return this.oAuthProvider(new auth.GoogleAuthProvider())
      .then(res => {
        this.updateUserTokenStatus(true);
        console.log('Successfully logged in!');
      }).catch(error => {
        this.updateUserTokenStatus(false);
        console.log(error);
      });
  }

  // Firebase Logout
  signOut() {
    return this.afAuth.signOut().then(() => {
      this.router.navigate(['login']);
    });
  }

  getUserTokenStatus(): boolean {
    return this.userToken.value;
  }

  updateUserTokenStatus(status: boolean) {
    this.userToken.next(status);
  }


}
