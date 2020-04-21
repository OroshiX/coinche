import { HttpResponse } from '@angular/common/http';
import { Injectable, NgZone } from '@angular/core';
import { AngularFireAuth } from '@angular/fire/auth';
import { Router } from '@angular/router';
import * as firebase from 'firebase';
import { CurrentUser } from '../../shared/models/current-user';
import { isNotNullAndNotUndefined } from '../../shared/utils/helper';
import { ApiLogInOutService } from '../apis/api-log-in-out.service';
import { UserLocalStorageService } from '../session-storage/user-local-storage.service';

@Injectable({
  providedIn: 'root'
})
export class FireAuthGoogleService {
  constructor(
    public router: Router,
    public ngZone: NgZone,
    public afAuth: AngularFireAuth,
    private userService: UserLocalStorageService ,
    private apiService: ApiLogInOutService,
  ) {
  }

  // Firebase Google Sign-in
  signinWithGoogle() {
    const googleAuthProvider = new firebase.auth.GoogleAuthProvider();
    return this.oAuthProvider(googleAuthProvider);
  }

  // Firebase Logout
  signOut() {
    return this.afAuth.signOut().then(() => {
      this.userService.resetUser();
      this.router.navigate(['login']).then(() => console.log('redirectTo Login page'));
    });
  }

  // Firebase SignInWithPopup
  private async oAuthProvider(provider) {
    try {
      const response = await this.afAuth.signInWithPopup(provider);
      const currentUser = await this.afAuth.currentUser;
      const idToken = await currentUser.getIdToken(true);

      if (!isNotNullAndNotUndefined(idToken) || idToken === '') {
        return response;
      }
      this.apiService.loginToServer(idToken)
        .subscribe((ret: HttpResponse<any>) => {
          console.log(ret.status);
          if (ret.status === 204) {
            this.updateUserToken(response.user, idToken);
            /*this.ngZone.run(() => {
              this.router.navigate(['all-games']);
            });*/
          }
        });
      console.log(this.afAuth.authState);
      return response;
    } catch (error) {
      console.log(this.afAuth.authState);
      alert(error);
    }
  }

  private updateUserToken(user: any, idToken: any) {
    let usr: CurrentUser;
    if (isNotNullAndNotUndefined(user)) {
      usr = new CurrentUser({uid: user.uid, email: user.email, displayName: user.displayName});
      usr.idToken = idToken;
    }
    this.userService.updateCurrentUser(usr);
  }

}
