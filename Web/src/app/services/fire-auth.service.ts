import { Injectable, NgZone } from '@angular/core';
import { AngularFireAuth } from '@angular/fire/auth';
import { Router } from '@angular/router';
import { auth } from 'firebase';
import { BehaviorSubject } from 'rxjs';
import { User } from '../models/user';
import { ApiLoginService } from './api-login.service';

@Injectable({
  providedIn: 'root'
})
export class FireAuthService {
  private userToken: BehaviorSubject<User> = new BehaviorSubject<User>(null);
  userToken$ = this.userToken.asObservable();

  constructor(
    public router: Router,
    public ngZone: NgZone,
    public afAuth: AngularFireAuth,
    private apiService: ApiLoginService
  ) {
    this.afAuth.authState.subscribe(user => {
      console.log(user);
      if (user !== undefined) {
        this.updateUser(user);
        console.log(this.getUserToken());
      }
    });
  }

  // Firebase SignInWithPopup
  oAuthProvider(provider) {
    return this.afAuth.signInWithPopup(provider)
      .then((res) => {
        console.log(res);
        console.log(res.user);
        console.log(res.credential.providerId);
        // @ts-ignore
        const idToken = res.credential.idToken;
        this.updateUserToken(res.user, idToken);
        console.log(this.userToken.value);
        // post to server
        this.apiService.loginToServer(idToken).subscribe(ret => console.log(ret));
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
        console.log('firebase google signin');
      }).catch(error => {
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
    return this.userToken !== undefined;
  }

  getUserToken(): User {
    return this.userToken.value;
  }

  getUserTokenId(): any {
    return this.userToken.value.idToken;
  }

  updateUserToken(user: any, idToken: any) {
    const usr: User = new User({uid: user.uid, email: user.email, displayName: user.displayName});
    usr.idToken = idToken;
    this.userToken.next(usr);
  }

  updateUser(user: any) {
     this.userToken.next(new User({uid: user.uid, email: user.email, displayName: user.displayName}));
  }

}
