import { TestBed } from '@angular/core/testing';

import { ApiLogInOutService } from './api-log-in-out.service';

describe('ApiLoginService', () => {
  let service: ApiLogInOutService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ApiLogInOutService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
