import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ShowAllTricksDialogComponent } from './show-all-tricks-dialog.component';

describe('ShowAllTrickDialogComponent', () => {
  let component: ShowAllTricksDialogComponent;
  let fixture: ComponentFixture<ShowAllTricksDialogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ShowAllTricksDialogComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ShowAllTricksDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
