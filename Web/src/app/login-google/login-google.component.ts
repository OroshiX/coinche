import { Component, OnDestroy, OnInit } from '@angular/core';
import { Subscription } from 'rxjs';
import { FireAuthService } from '../services/authentication/fire-auth.service';
import { SessionStorageService } from '../services/session-storage/session-storage.service';

@Component({
  selector: 'app-login-google',
  templateUrl: './login-google.component.html',
  styleUrls: ['./login-google.component.scss']
})
export class LoginGoogleComponent implements OnInit, OnDestroy {
  sub: Subscription;
  isConnected: boolean;

  constructor(private sessionService: SessionStorageService,
              private fireAuthService: FireAuthService) {
  }

  ngOnInit(): void {
    this.sub = this.sessionService.isConnected()
      .subscribe(isConnected => this.isConnected = isConnected);
  }

  signIn() {
    this.fireAuthService.signinWithGoogle()
      .then((res) => {
          console.log('Sign in with google - user: ', res?.user?.displayName);
          this.sessionService.saveCurrentUser(res.user);
        },
        (error) => console.log('error ', error)
      );
  }

  ngOnDestroy(): void {
    this.sub.unsubscribe();
  }

}
