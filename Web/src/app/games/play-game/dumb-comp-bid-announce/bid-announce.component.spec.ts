import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { BidAnnounceComponent } from './bid-announce.component';

describe('BidAnnounceComponent', () => {
  let component: BidAnnounceComponent;
  let fixture: ComponentFixture<BidAnnounceComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ BidAnnounceComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(BidAnnounceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
