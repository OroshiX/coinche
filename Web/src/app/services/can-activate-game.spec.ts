import { TestBed } from '@angular/core/testing';

import { CanActivateGame } from './can-activate-game';

describe('CanActivateGameService', () => {
  let service: CanActivateGame;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CanActivateGame);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
