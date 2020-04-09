import { HttpResponse } from '@angular/common/http';
import { Injectable, NgZone } from '@angular/core';
import { AngularFireAuth } from '@angular/fire/auth';
import { Router } from '@angular/router';
import * as firebase from 'firebase';
import { CurrentUser } from '../../shared/models/user';
import { isNotNullAndNotUndefined } from '../../shared/utils/helper';
import { ApiLogInOutService } from '../apis/api-log-in-out.service';
import { SessionStorageService } from '../session-storage/session-storage.service';

@Injectable({
  providedIn: 'root'
})
export class FireAuthService {
  constructor(
    public router: Router,
    public ngZone: NgZone,
    public afAuth: AngularFireAuth,
    private apiService: ApiLogInOutService,
    private sessionService: SessionStorageService
  ) {
  }

  // Firebase SignInWithPopup
  async oAuthProvider(provider) {
    try {
      const response = await this.afAuth.signInWithPopup(provider);
      const currentUser = await this.afAuth.currentUser;
      const idToken = await currentUser.getIdToken();

      this.apiService.loginToServer(idToken)
        .subscribe((ret: HttpResponse<any>) => {
          console.log(ret.status);
          if (ret.status === 204) {
            this.updateUserToken(response.user, idToken);
            this.ngZone.run(() => {
              this.router.navigate(['all-games']);
            });
          }
        });
      return response;
    } catch (error) {
      alert(error);
    }
  }

  // Firebase Google Sign-in
  signinWithGoogle() {
    const googleAuthProvider = new firebase.auth.GoogleAuthProvider();
    return this.oAuthProvider(googleAuthProvider);
  }

  // Firebase Logout
  signOut() {
    return this.afAuth.signOut().then(() => {
      this.sessionService.resetCurrentUser();
      this.router.navigate(['login']).then(() => console.log('redirectTo Login page'));
    });
  }

  updateUserToken(user: any, idToken: any) {
    let usr: CurrentUser;
    if (isNotNullAndNotUndefined(user)) {
      usr = new CurrentUser({uid: user.uid, email: user.email, displayName: user.displayName});
      usr.idToken = idToken;
    }
    this.sessionService.saveCurrentUser(usr);
  }

}
