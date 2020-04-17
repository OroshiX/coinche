import { Injectable } from '@angular/core';
import { CurrentUser } from '../shared/models/user';
import { SessionStorageService } from './session-storage/session-storage.service';

@Injectable({
  providedIn: 'root'
})
export class LoginPageService {

  constructor(private sessionUser: SessionStorageService) {
  }

  updateUserToken(usr: CurrentUser) {
    this.sessionUser.updateUser();
  }
}
