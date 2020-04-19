import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DistributingComponent } from './distributing.component';

describe('DistributingComponent', () => {
  let component: DistributingComponent;
  let fixture: ComponentFixture<DistributingComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DistributingComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DistributingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
