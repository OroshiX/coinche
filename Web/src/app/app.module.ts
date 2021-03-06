import { LayoutModule } from '@angular/cdk/layout';
import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { AngularFireModule } from '@angular/fire';
import { AngularFireAuthModule } from '@angular/fire/auth';
import { ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatListModule } from '@angular/material/list';
import { MatSelectModule } from '@angular/material/select';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatTooltipModule } from '@angular/material/tooltip';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterModule } from '@angular/router';
import { environment } from '../environments/environment';
import { AppRoutingModule } from './app-routing.module';

import { AppComponent } from './app.component';
import { DialogBidComponent } from './games/play-game/dialog-comp-bid/dialog-bid.component';
import { LoginGoogleComponent } from './login-google/login-google.component';
import { LoginPageComponent } from './login-page/login-page.component';
import { AuthInterceptor } from './services/http-interceptors/auth-interceptor';
import { LoggingInterceptor } from './services/http-interceptors/logging-interceptor';
import { SharedCommonModule } from './shared-common/shared-common.module';
import { AlertComponent } from './shared/alert/alert.component';
import { CreateNicknameDialogComponent } from './side-nav/create-nickname-dialog/create-nickname-dialog.component';
import { SideNavComponent } from './side-nav/side-nav.component';

export const httpInterceptorProviders = [
  {provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true},
  {provide: HTTP_INTERCEPTORS, useClass: LoggingInterceptor, multi: true},
];

@NgModule({
  declarations: [
    AppComponent,
    SideNavComponent,
    LoginPageComponent,
    AlertComponent,
    LoginGoogleComponent,
    CreateNicknameDialogComponent,
    DialogBidComponent
  ],
  entryComponents: [
    CreateNicknameDialogComponent
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    AppRoutingModule,
    LayoutModule,
    MatToolbarModule,
    MatButtonModule,
    MatSidenavModule,
    MatIconModule,
    MatListModule,
    RouterModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatSelectModule,
    ReactiveFormsModule,
    AngularFireAuthModule,
    AngularFireModule.initializeApp(environment.firebaseConfig),
    HttpClientModule,
    MatDialogModule,
    MatTooltipModule,
    SharedCommonModule,
  ],
  exports: [
    SharedCommonModule,
  ],
  providers: [httpInterceptorProviders],
  bootstrap: [AppComponent]
})
export class AppModule {
}
