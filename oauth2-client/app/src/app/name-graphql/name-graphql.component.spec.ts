import {ComponentFixture, TestBed} from '@angular/core/testing';

import {NameGraphqlComponent} from './name-graphql.component';

describe('NameGraphqlComponent', () => {
  let component: NameGraphqlComponent;
  let fixture: ComponentFixture<NameGraphqlComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [NameGraphqlComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(NameGraphqlComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
