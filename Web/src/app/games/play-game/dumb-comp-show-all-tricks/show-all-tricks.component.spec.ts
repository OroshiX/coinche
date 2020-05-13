import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ShowAllTricksComponent } from './show-all-tricks.component';

describe('ShowAllTricksComponent', () => {
  let component: ShowAllTricksComponent;
  let fixture: ComponentFixture<ShowAllTricksComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ShowAllTricksComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ShowAllTricksComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
