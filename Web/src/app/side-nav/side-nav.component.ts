import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { Observable, Subscription } from 'rxjs';
import { map, shareReplay } from 'rxjs/operators';
import { ApiLobbyService } from '../services/apis/api-lobby.service';
import { ApiLogInOutService } from '../services/apis/api-log-in-out.service';
import { FireAuthService } from '../services/authentication/fire-auth.service';
import { SessionStorageService } from '../services/session-storage/session-storage.service';
import { CurrentUser } from '../shared/models/user';
import { isNotNullAndNotUndefined } from '../shared/utils/helper';
import { CreateNicknameDialogComponent } from './create-nickname-dialog/create-nickname-dialog.component';

export const DIALOG_WIDTH = '300px';

@Component({
  selector: 'app-side-nav',
  templateUrl: './side-nav.component.html',
  styleUrls: ['./side-nav.component.scss']
})
export class SideNavComponent implements OnInit, OnDestroy {
  sub: Subscription;
  currentUserName = '';
  nickname = '';
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
    private apiLobbyService: ApiLobbyService,
    private authService: FireAuthService,
    private sessionService: SessionStorageService,
    private dialog: MatDialog) {
  }

  // ----- dialog begin ------
  openDialog(): void {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.disableClose = false;
    dialogConfig.autoFocus = true;
    dialogConfig.data = this;
    dialogConfig.width = DIALOG_WIDTH;

    const dialogRef = this.dialog.open(CreateNicknameDialogComponent, dialogConfig);

    dialogRef.afterClosed().subscribe((data: string) => {
      console.log('The dialog was closed');
      console.log(data);
      this.apiLobbyService.setNickname(data)
        .subscribe(res => console.log('nickname succeed'));
    });
  }

  ngOnInit(): void {
    this.sessionService.getCurrentUserObs()
      .subscribe((userToken: CurrentUser | null) => {
        this.currentUserName = isNotNullAndNotUndefined(userToken) ? userToken.displayName : '';
        this.isDisabled = !isNotNullAndNotUndefined(userToken);
        console.log(userToken);
      });
  }

  setNickname() {
    if (this.currentUserName !== '') {
      this.openDialog();
      /*this.apiLobbyService.setNickname('Istiti').subscribe(res => console.log(res));*/
    }
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
