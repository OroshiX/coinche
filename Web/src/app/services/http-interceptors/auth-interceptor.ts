import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { FireAuthGoogleService } from '../authentication/fire-auth-google.service';

@Injectable({
  providedIn: 'root'
})
export class AuthInterceptor implements HttpInterceptor {
  constructor(private authenticationService: FireAuthGoogleService) {
  }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    // Clone the request and replace the original headers with
    // cloned headers, updated with the authorization.
    const authReq = req.clone({
      withCredentials: true,
      headers: req.headers.set('X-Requested-With', 'XMLHttpRequest')
    });

    // send cloned request with header to the next handler.
    return next.handle(authReq).pipe(catchError(err => {
      if (err.status === 401) {
        // auto logout if 401 response returned from api
        this.authenticationService.signOut();
        location.reload();
      }
      return throwError(err);
    }));
  }
}
