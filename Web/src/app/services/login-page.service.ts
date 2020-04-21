import { Injectable } from '@angular/core';
import { CurrentUser } from '../shared/models/current-user';

@Injectable({
  providedIn: 'root'
})
export class LoginPageService {

  constructor() {
  }

  updateUserToken(usr: CurrentUser) {
    console.log(usr);
    // this.sessionUser.updateUser();
  }
}
