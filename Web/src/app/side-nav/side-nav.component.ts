import { Component, OnDestroy, OnInit } from '@angular/core';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { Observable, Subscription } from 'rxjs';
import { map, shareReplay } from 'rxjs/operators';
import { ApiLogInOutService } from '../services/apis/api-log-in-out.service';
import { FireAuthService } from '../services/authentication/fire-auth.service';
import { SessionStorageService } from '../services/session-storage/session-storage.service';
import { CurrentUser } from '../shared/models/user';
import { isNotNullAndNotUndefined } from '../shared/utils/helper';

@Component({
  selector: 'app-side-nav',
  templateUrl: './side-nav.component.html',
  styleUrls: ['./side-nav.component.scss']
})
export class SideNavComponent implements OnInit, OnDestroy {
  sub: Subscription;
  currentUserName = '';
  isDisabled = false;
  isHandset$: Observable<boolean> = this.breakpointObserver.observe(
    [Breakpoints.Handset, Breakpoints.Small, Breakpoints.Medium, Breakpoints.Large])
    .pipe(
      map(result => result.matches),
      shareReplay()
    );

  constructor(
    private breakpointObserver: BreakpointObserver,
    private apiService: ApiLogInOutService,
    private authService: FireAuthService,
    private sessionService: SessionStorageService) {
  }

  ngOnInit(): void {
    this.sessionService.getCurrentUserObs()
      .subscribe((userToken: CurrentUser | null) => {
        this.currentUserName = isNotNullAndNotUndefined(userToken) ? userToken.displayName : '';
        this.isDisabled = !isNotNullAndNotUndefined(userToken);
      });
  }

  logout() {
    console.log('call logout');
    this.authService.signOut().then(() => alert('You are disconnected from Firebase !'));
    /*this.apiService.logoutToServer()
      .subscribe(ret => {
        console.log('logout', ret);
        // this.authDervice.signOut().then(() => console.log('signout firebase'));
      });*/
  }

  ngOnDestroy(): void {
    this.sub.unsubscribe();
  }

}
