import { HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { throwError } from 'rxjs';
import { FireAuthService } from '../authentication/fire-auth.service';
import { SessionStorageService } from '../session-storage/session-storage.service';

@Injectable({
  providedIn: 'root'
})
export class ApiErrorHandlerService {

  constructor(private authService: FireAuthService, private router: Router, private sessionService: SessionStorageService) {
  }

  handleError(error: HttpErrorResponse) {
    if (error.error instanceof ErrorEvent) {
      // A client-side or network error occurred. Handle it accordingly.
      console.error('An error occurred:', error.error.message);
    } else {
      // The backend returned an unsuccessful response code.
      // The response body may contain clues as to what went wrong,
      console.error(
        `Backend returned code ${error.status}, ` +
        `body was: ${error.error.error}`);
      if (error.status === 401) {
        this.authService.signOut();
        this.sessionService.updateUser();
      }
      if (error.status === 412) {
        alert('You have already joined the selected game! Please choose another one');
      }
    }
    // return an observable with a user-facing error message
    return throwError(
      'Something bad happened; please try again later.');
  };


}
