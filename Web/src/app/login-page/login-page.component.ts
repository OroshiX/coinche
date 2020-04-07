import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { LoginPageService } from '../services/login-page.service';
import { LoginErrorStateMatcher, PWD_PATTERN } from '../shared/utils/login-error-state-matcher';

@Component({
  selector: 'app-login-page',
  templateUrl: './login-page.component.html',
  styleUrls: ['./login-page.component.scss']
})
export class LoginPageComponent implements OnInit, OnDestroy {
  loginForm: FormGroup;
  matcher = new LoginErrorStateMatcher();
  hide = true;
  sub: Subscription;

  get emailAddress(): FormControl {
    return this.loginForm.get('emailAddress') as FormControl;
  }

  get currentPassword(): FormControl {
    return this.loginForm.get('currentPassword') as FormControl;
  }

  constructor(private formBuilder: FormBuilder, private router: Router, private loginService: LoginPageService) {
  }

  ngOnInit(): void {
    this.loginForm = this.formBuilder.group({
        emailAddress: this.formBuilder.control('',
          [Validators.required, Validators.email]),
        currentPassword: this.formBuilder.control('',
          [Validators.required, Validators.pattern(PWD_PATTERN)])
      }
    );

    this.sub = this.loginForm.valueChanges.subscribe(value => console.log(value));
  }

  ngOnDestroy(): void {
    this.sub.unsubscribe();
  }

  submit() {
    if (this.loginForm.valid) {
      this.loginService.updateUserToken(this.loginForm.value);
      this.router.navigateByUrl('play')
        .then(res => console.log(res));
      console.log('navigate');
    }
  }

}
