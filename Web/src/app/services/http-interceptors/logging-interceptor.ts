import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { finalize, tap } from 'rxjs/operators';
import { MessagesService } from './messages.service';

@Injectable()
export class LoggingInterceptor implements HttpInterceptor {

  constructor(private messenger: MessagesService) {
  }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const started = Date.now();
    let ok: string;
    let stat: string;

    // extend server response observable with logging
    return next.handle(req)
      .pipe(
        tap(event => {
            stat = event instanceof HttpResponse ? (event.status).toString() : '';
            console.log(stat);
          }
        ),
        tap(
          // Succeeds when there is a response; ignore other events
          event => ok = event instanceof HttpResponse ? 'succeeded' : '',
          // Operation failed; error is an HttpErrorResponse
          () => ok = 'failed'
        ),
        // Log when response observable either completes or errors
        finalize(() => {
          const elapsed = Date.now() - started;
          const msg = `${req.method} "${req.urlWithParams}" ${stat}
             ${ok} in ${elapsed} ms.`;
          this.messenger.addMsg(msg);
        })
      );
  }
}
