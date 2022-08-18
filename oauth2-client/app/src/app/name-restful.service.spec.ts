import {TestBed} from '@angular/core/testing';

import {NameRestfulService} from './name-restful.service';

describe('NameRestfulService', () => {
  let service: NameRestfulService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(NameRestfulService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
