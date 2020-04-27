import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { OnTableBiddingComponent } from './on-table-bidding.component';

describe('BiddingComponent', () => {
  let component: OnTableBiddingComponent;
  let fixture: ComponentFixture<OnTableBiddingComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ OnTableBiddingComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(OnTableBiddingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
