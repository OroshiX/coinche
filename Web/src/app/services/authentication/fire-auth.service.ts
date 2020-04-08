import { HttpResponse } from '@angular/common/http';
import { Injectable, NgZone } from '@angular/core';
import { AngularFireAuth } from '@angular/fire/auth';
import { Router } from '@angular/router';
import * as firebase from 'firebase';
import { BehaviorSubject, Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { CurrentUser } from '../../shared/models/user';
import { isNotNullAndNotUndefined } from '../../shared/utils/helper';
import { ApiLogInOutService } from '../apis/api-log-in-out.service';

@Injectable({
  providedIn: 'root'
})
export class FireAuthService {
  private userToken: BehaviorSubject<CurrentUser> = new BehaviorSubject<CurrentUser>(null);

  userToken$ = this.userToken.asObservable();

  constructor(
    public router: Router,
    public ngZone: NgZone,
    public afAuth: AngularFireAuth,
    private apiService: ApiLogInOutService
  ) {
    this.afAuth.authState.subscribe((user: firebase.User | null) => {
      this.updateUser(user);
    });
  }

  // Firebase SignInWithPopup
  async oAuthProvider(provider) {
    try {
      const response = await this.afAuth.signInWithPopup(provider);
      const currentUser = await this.afAuth.currentUser;
      const idToken = await currentUser.getIdToken();
      this.updateUserToken(response.user, idToken);
      this.apiService.loginToServer(idToken).subscribe((ret: HttpResponse<any>) => {
        console.log(ret.status);
        this.ngZone.run(() => {
          this.router.navigate(['all-games']);
        });
      });
      return response;
    } catch (error) {
      window.alert(error);
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
      this.resetCurrentUser();
      this.router.navigate(['login']).then(() => console.log('redirectTo Login page'));
    });
  }

  getUserTokenStatus(): boolean {
    return (isNotNullAndNotUndefined(this.getUserToken()))
      && this.getUserToken()?.idToken !== '';
  }

  isConnected(): Observable<boolean> {
    return this.userToken$.pipe(
      map((currentUser: CurrentUser | null) =>
        isNotNullAndNotUndefined(currentUser) && currentUser.idToken !== '' ? true : false
      ));
  }

  getUserToken(): CurrentUser {
    return isNotNullAndNotUndefined(this.userToken) ? this.userToken?.value : null;
  }

  getUserTokenId(): any {
    return this.userToken.value.idToken;
  }

  resetCurrentUser(): void {
    this.updateUserToken(null, '');
  }

  updateUserToken(user: any, idToken: any) {
    let usr: CurrentUser;
    if (isNotNullAndNotUndefined(user)) {
      usr = new CurrentUser({uid: user.uid, email: user.email, displayName: user.displayName});
      usr.idToken = idToken;
    }
    this.userToken.next(usr);
  }

  updateUser(user: firebase.User) {
    if (isNotNullAndNotUndefined(user)) {
      this.userToken.next(new CurrentUser({uid: user.uid, email: user.email, displayName: user.displayName}));
    } else {
      this.userToken.next(null);
    }
  }

}
