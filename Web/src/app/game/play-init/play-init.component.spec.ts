import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PlayInitComponent } from './play-init.component';

describe('PlayInitComponent', () => {
  let component: PlayInitComponent;
  let fixture: ComponentFixture<PlayInitComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PlayInitComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PlayInitComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
