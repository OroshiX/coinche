import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateGameDialogComponent } from './create-game-dialog.component';

describe('CreateDialogComponent', () => {
  let component: CreateGameDialogComponent;
  let fixture: ComponentFixture<CreateGameDialogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CreateGameDialogComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CreateGameDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
