import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { Injectable } from '@angular/core';

const MAX_SMALL_SCREEN = '(max-width: 959px)';

@Injectable({
  providedIn: 'root'
})
export class BreakpointService {

  constructor(private breakpointObserver: BreakpointObserver) {
  }

  layoutChanges$() {
    return this.breakpointObserver.observe([
      '(orientation: portrait)',
      '(orientation: landscape)',
      Breakpoints.Medium,
      Breakpoints.Small
    ]);
  }

  isSmallScreen(): boolean {
    return this.breakpointObserver.isMatched(MAX_SMALL_SCREEN);
  }

}
