import { TestBed } from '@angular/core/testing';

import { ApiInGameService } from './api-in-game.service';

describe('ApiInGameService', () => {
  let service: ApiInGameService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ApiInGameService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
