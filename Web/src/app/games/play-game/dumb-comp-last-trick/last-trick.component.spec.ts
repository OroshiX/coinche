import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { LastTrickComponent } from './last-trick.component';

describe('LastTrickComponent', () => {
  let component: LastTrickComponent;
  let fixture: ComponentFixture<LastTrickComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ LastTrickComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(LastTrickComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
