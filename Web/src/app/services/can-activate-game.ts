import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, RouterStateSnapshot, UrlTree } from '@angular/router';
import { Observable } from 'rxjs';
import { LoginPageService } from './login-page.service';

@Injectable()
export class CanActivateGame implements CanActivate {

  // constructor(private permissions: Permissions, private currentUser: UserToken) { }
  constructor(private loginService: LoginPageService) {
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot):
    Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
    const bool = this.loginService.getUserToken() !== undefined && this.loginService.getUserToken() !== null;
    if (bool === false) {
      alert('Sorry, You are not authorized to play');
    }
    return bool;
  }
}
