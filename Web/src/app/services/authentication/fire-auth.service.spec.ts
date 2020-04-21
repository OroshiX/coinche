import { TestBed } from '@angular/core/testing';

import { FireAuthGoogleService } from './fire-auth-google.service';

describe('LoginGoogleService', () => {
  let service: FireAuthGoogleService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(FireAuthGoogleService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
