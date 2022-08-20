import {TestBed} from '@angular/core/testing';

import {NameGraphqlService} from './name-graphql.service';

describe('NameGraphqlService', () => {
  let service: NameGraphqlService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(NameGraphqlService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
