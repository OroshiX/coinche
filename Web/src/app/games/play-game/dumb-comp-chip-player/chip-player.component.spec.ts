import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ChipPlayerComponent } from './chip-player.component';

describe('ChipPlayerComponent', () => {
  let component: ChipPlayerComponent;
  let fixture: ComponentFixture<ChipPlayerComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ChipPlayerComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ChipPlayerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
