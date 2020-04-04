import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, RouterStateSnapshot, UrlTree } from '@angular/router';
import { Observable } from 'rxjs';
import { FireAuthService } from '../fire-auth.service';

@Injectable()
export class CanActivateGame implements CanActivate {

  // constructor(private permissions: Permissions, private currentUser: UserToken) { }
  constructor(private loginService: FireAuthService) {
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot):
    Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
    const bool = this.loginService.getUserTokenStatus();
    if (bool === false) {
      alert('Sorry, You are not authorized to play');
    }
    return bool;
  }
}
