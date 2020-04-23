import { Component, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { FireAuthGoogleService } from '../services/authentication/fire-auth-google.service';
import { UserLocalStorageService } from '../services/session-storage/user-local-storage.service';

@Component({
  selector: 'app-login-google',
  templateUrl: './login-google.component.html',
  styleUrls: ['./login-google.component.scss']
})
export class LoginGoogleComponent implements OnInit, OnDestroy {
  sub: Subscription;
  isConnected: boolean;

  constructor(private router: Router,
              private userService: UserLocalStorageService,
              private fireAuthService: FireAuthGoogleService) {
  }

  ngOnInit(): void {
    this.sub = this.userService.isConnected$()
      .subscribe(isConnected => {
        this.isConnected = isConnected;
      });
  }

  signIn() {
    this.fireAuthService.signinWithGoogle()
      .then((res) => {
          console.log('Sign in with google - user: ', res?.user?.displayName);
          this.router.navigate(['all-games']);
        },
        (error) => console.log('error ', error)
      );
  }

  ngOnDestroy(): void {
    this.sub.unsubscribe();
  }

}
