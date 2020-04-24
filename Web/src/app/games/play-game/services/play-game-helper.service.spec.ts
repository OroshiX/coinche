import { TestBed } from '@angular/core/testing';

import { PlayGameHelperService } from './play-game-helper.service';

describe('PlayGameHelperService', () => {
  let service: PlayGameHelperService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PlayGameHelperService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
