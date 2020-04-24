import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { OnTableComponent } from './on-table.component';

describe('OnTableComponent', () => {
  let component: OnTableComponent;
  let fixture: ComponentFixture<OnTableComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ OnTableComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(OnTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
