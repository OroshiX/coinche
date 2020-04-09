import { HttpResponse } from '@angular/common/http';
import { Injectable, NgZone } from '@angular/core';
import { AngularFireAuth } from '@angular/fire/auth';
import { Router } from '@angular/router';
import * as firebase from 'firebase';
import { tap } from 'rxjs/operators';
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
    /*this.afAuth.authState.subscribe((user: firebase.User | null) => {
      console.log(user);
      this.updateUser(user);
    });*/
  }

  // Firebase SignInWithPopup
  async oAuthProvider(provider) {
    try {
      const response = await this.afAuth.signInWithPopup(provider);
      const currentUser = await this.afAuth.currentUser;
      const idToken = await currentUser.getIdToken();

      this.apiService.loginToServer(idToken)
        .pipe(tap(res => console.log(res)))
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

  /* getUserTokenStatus(): boolean {
     return (isNotNullAndNotUndefined(this.getUserToken()))
       && this.getUserToken().idToken !== null && this.getUserToken().idToken !== '';
   }*/

  /*isConnectedObs(): Observable<boolean>{
    return this.sessionService.isConnectedObs();
  }

  private getUserToken(): CurrentUser {
    return this.sessionService.getCurrentUser();
  }

  resetCurrentUser(): void {
    this.sessionService.resetCurrentUser();
  }
  }*/

}
