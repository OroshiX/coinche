import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateNicknameDialogComponent } from './create-nickname-dialog.component';

describe('CreateDialogComponent', () => {
  let component: CreateNicknameDialogComponent;
  let fixture: ComponentFixture<CreateNicknameDialogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CreateNicknameDialogComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CreateNicknameDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
