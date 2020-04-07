import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, RouterStateSnapshot, UrlTree } from '@angular/router';
import { Observable } from 'rxjs';
import { FireAuthService } from '../authentication/fire-auth.service';

@Injectable()
export class CanActivateGame implements CanActivate {

  constructor(private loginService: FireAuthService) {
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot):
    Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
    const bool = this.loginService.getUserTokenStatus();
    if (this.loginService.getUserTokenStatus() === false) {
      alert('Sorry, You are not authorized to play. Please sign in !');
    }
    return bool;
  }
}
