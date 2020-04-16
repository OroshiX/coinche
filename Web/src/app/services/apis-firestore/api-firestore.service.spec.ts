import { TestBed } from '@angular/core/testing';

import { ApiFirestoreService } from './api-firestore.service';

describe('ApiFirestoreService', () => {
  let service: ApiFirestoreService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ApiFirestoreService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
