import { TestBed } from '@angular/core/testing';

import { ApiOutsideGameService } from './api-outside-game.service';

describe('ApiInitGameService', () => {
  let service: ApiOutsideGameService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ApiOutsideGameService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
