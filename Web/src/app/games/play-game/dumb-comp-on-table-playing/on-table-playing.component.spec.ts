import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { OnTablePlayingComponent } from './on-table-playing.component';

describe('OnTableComponent', () => {
  let component: OnTablePlayingComponent;
  let fixture: ComponentFixture<OnTablePlayingComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ OnTablePlayingComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(OnTablePlayingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
