import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree } from '@angular/router';
import { Observable } from 'rxjs';
import { UserLocalStorageService } from '../session-storage/user-local-storage.service';

@Injectable({providedIn: 'root'})
export class GameGuard implements CanActivate {
  constructor(
    private router: Router,
    private userService: UserLocalStorageService
  ) {
  }

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {

    const isConnected = this.userService.isConnected();
    /* if (route.params.id === undefined || route.params.id === '') {
       alert('No gameId found !');
       console.log(route.params.id);
       this.router.navigate(['/login']);
       return false;
     }*/

    if (isConnected) {
      // logged in so return true
      if (route.params.id === undefined || route.params.id === '') {
        alert('No gameId found !');
        console.log(route.params.id);
        this.router.navigate(['/login']);
        return false;
      }
      console.log('gameId:', route.params.id);
      return true;
    } else {
      this.userService.resetUser();
      this.router.navigate(['/login']);
      console.log('state url:', state.url);
      return false;
    }
  }
}
