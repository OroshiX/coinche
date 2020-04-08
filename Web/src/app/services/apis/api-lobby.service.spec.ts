import { TestBed } from '@angular/core/testing';

import { ApiLobbyService } from './api-lobby.service';

describe('ApiInitGameService', () => {
  let service: ApiLobbyService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ApiLobbyService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
