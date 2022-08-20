import {TestBed} from '@angular/core/testing';

import {NameGraphqlGuard} from './name-graphql.guard';

describe('NameGraphqlGuardGuard', () => {
  let guard: NameGraphqlGuard;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    guard = TestBed.inject(NameGraphqlGuard);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });
});
