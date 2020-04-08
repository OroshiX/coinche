import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { FireAuthService } from '../services/authentication/fire-auth.service';

@Component({
  selector: 'app-login-google',
  templateUrl: './login-google.component.html',
  styleUrls: ['./login-google.component.scss']
})
export class LoginGoogleComponent implements OnInit {
  isConnected$: Observable<boolean>;

  constructor(private fireAuthService: FireAuthService) {
  }

  ngOnInit(): void {
    this.isConnected$ = this.fireAuthService.isConnected();
  }

  signIn() {
    this.fireAuthService.signinWithGoogle()
      .then((res) => {
          console.log(res.credential.toJSON());
          console.log('Sign in with google - user: ', res?.user?.displayName);
        }
      );
  }

}
