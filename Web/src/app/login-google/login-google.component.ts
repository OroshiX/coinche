import { Component, OnInit } from '@angular/core';
import { FireAuthService } from '../services/authentication/fire-auth.service';

@Component({
  selector: 'app-login-google',
  templateUrl: './login-google.component.html',
  styleUrls: ['./login-google.component.scss']
})
export class LoginGoogleComponent implements OnInit {
  isConnected: boolean;

  constructor(private fireAuthService: FireAuthService) {
  }

  ngOnInit(): void {
    this.fireAuthService.isConnected().subscribe(isConnected => {
      this.isConnected = isConnected;
    });
  }

  signIn() {
    this.fireAuthService.signinWithGoogle()
      .then((res) => {
          console.log('Sign in with google - user: ', res?.user?.displayName);
        }
      );
  }

}
