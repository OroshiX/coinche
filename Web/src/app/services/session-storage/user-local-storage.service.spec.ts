import { TestBed } from '@angular/core/testing';

import { UserLocalStorageService } from './user-local-storage.service';

describe('UserSessionStorageService', () => {
  let service: UserLocalStorageService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(UserLocalStorageService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
