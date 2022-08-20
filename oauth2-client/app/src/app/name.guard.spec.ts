import {TestBed} from '@angular/core/testing';

import {NameGuard} from './name.guard';

describe('NameGuard', () => {
  let guard: NameGuard;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    guard = TestBed.inject(NameGuard);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });
});
