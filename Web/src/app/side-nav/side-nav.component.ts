import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { Observable, Subject } from 'rxjs';
import { map, shareReplay, takeUntil } from 'rxjs/operators';
import { ApiLobbyService } from '../services/apis/api-lobby.service';
import { ApiLogInOutService } from '../services/apis/api-log-in-out.service';
import { FireAuthService } from '../services/authentication/fire-auth.service';
import { SessionStorageService } from '../services/session-storage/session-storage.service';
import { isNotNullAndNotUndefined } from '../shared/utils/helper';
import { CreateNicknameDialogComponent } from './create-nickname-dialog/create-nickname-dialog.component';

export const DIALOG_WIDTH = '300px';

@Component({
  selector: 'app-side-nav',
  templateUrl: './side-nav.component.html',
  styleUrls: ['./side-nav.component.scss']
})
export class SideNavComponent implements OnInit, OnDestroy {
  private unsubscribe$ = new Subject<void>();
  currentUserName = '';
  nickname = '';
  isDisabled = false;
  isHandset$: Observable<boolean>;

  constructor(
    private breakpointObserver: BreakpointObserver,
    private apiService: ApiLogInOutService,
    private apiLobbyService: ApiLobbyService,
    private authService: FireAuthService,
    private sessionService: SessionStorageService,
    private dialog: MatDialog) {
    this.isHandset$ = this.breakpointObserver.observe(
      [Breakpoints.Handset, Breakpoints.Small, Breakpoints.Medium, Breakpoints.Large])
      .pipe(
        map(result => result.matches),
        shareReplay()
      );
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
      this.apiLobbyService.setNickname(data)
        .pipe(takeUntil(this.unsubscribe$))
        .subscribe(() => console.log('nickname succeed'));
    });
  }

  ngOnInit(): void {
    this.sessionService.getCurrentUser$()
      .pipe(takeUntil(this.unsubscribe$))
      .subscribe((user: any | null) => {
        this.currentUserName = isNotNullAndNotUndefined(user) ? user.displayName : '';
        this.isDisabled = !isNotNullAndNotUndefined(user);
      });
  }

  setNickname() {
    if (this.currentUserName !== '') {
      this.openDialog();
    }
  }

  logout() {
    console.log('call logout');
    this.authService.signOut().then(() => alert('You are disconnected from Firebase !'));
    this.apiService.logoutToServer()
      .pipe(takeUntil(this.unsubscribe$))
      .subscribe(ret => {
        console.log('logout', ret);
      });
  }

  ngOnDestroy(): void {
    this.unsubscribe$.next();
    this.unsubscribe$.complete();
  }

}
