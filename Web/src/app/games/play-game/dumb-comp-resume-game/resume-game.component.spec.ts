import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ResumeGameComponent } from './resume-game.component';

describe('ResumeGameComponent', () => {
  let component: ResumeGameComponent;
  let fixture: ComponentFixture<ResumeGameComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ResumeGameComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ResumeGameComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
