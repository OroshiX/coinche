import { Component, OnInit } from '@angular/core';
import { FireAuthService } from '../services/fire-auth.service';

@Component({
  selector: 'app-login-google',
  templateUrl: './login-google.component.html',
  styleUrls: ['./login-google.component.scss']
})
export class LoginGoogleComponent implements OnInit {

  constructor(private fireAuthService: FireAuthService) {
  }

  ngOnInit(): void {
  }

  signIn() {
    this.fireAuthService.signinWithGoogle().then(res => console.log('Sign in with google'));
  }

}
