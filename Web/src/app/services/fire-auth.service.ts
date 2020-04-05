import { Injectable, NgZone } from '@angular/core';
import { AngularFireAuth } from '@angular/fire/auth';
import * as firebase from 'firebase';
import { Router } from '@angular/router';
import { BehaviorSubject } from 'rxjs';
import { CurrentUser } from '../models/user';
import { ApiLoginService } from './api-login.service';

@Injectable({
  providedIn: 'root'
})
export class FireAuthService {
  private userToken: BehaviorSubject<CurrentUser> = new BehaviorSubject<CurrentUser>(null);

  // userToken$ = this.userToken.asObservable();

  constructor(
    public router: Router,
    public ngZone: NgZone,
    public afAuth: AngularFireAuth,
    private apiService: ApiLoginService
  ) {
    this.afAuth.authState.subscribe((user: firebase.User | null) => {
      if (user !== null) {
        this.updateUser(user);
        console.log(this.getUserToken());
      }
    });
  }

  // Firebase SignInWithPopup
  oAuthProvider(provider) {
    return this.afAuth.signInWithPopup(provider)
      .then((res) => {
        this.afAuth.currentUser.then((e) => {
          console.log(e);
          e.getIdToken().then(
            idToken => {
              console.log(idToken);
              this.updateUserToken(res.user, idToken);
              this.apiService.loginToServer(idToken).subscribe(ret => console.log(ret));
              this.ngZone.run(() => {
                this.router.navigate(['play']);
              });
            }
          );
        });
      }).catch((error) => {
        window.alert(error);
      });
  }

  // Firebase Google Sign-in
  signinWithGoogle() {
    const googleAuthProvider = new firebase.auth.GoogleAuthProvider();
    return this.oAuthProvider(googleAuthProvider)
      .then(_ => {
        console.log('firebase google signin');
      }).catch(error => {
        console.log(error);
      });
  }

  // Firebase Logout
  signOut() {
    return this.afAuth.signOut().then((_) => {
      this.router.navigate(['login']).then(__ => console.log('redirectTo Login page'));
    });
  }

  getUserTokenStatus(): boolean {
    return this.userToken !== undefined;
  }

  getUserToken(): CurrentUser {
    return this.userToken.value;
  }

  getUserTokenId(): any {
    return this.userToken.value.idToken;
  }

  updateUserToken(user: any, idToken: any) {
    console.log(firebase.auth.AuthCredential.toString());
    const usr: CurrentUser = new CurrentUser({uid: user.uid, email: user.email, displayName: user.displayName});
    usr.idToken = idToken;
    this.userToken.next(usr);
  }

  updateUser(user: firebase.User) {
    this.userToken.next(new CurrentUser({uid: user.uid, email: user.email, displayName: user.displayName}));
  }

}
