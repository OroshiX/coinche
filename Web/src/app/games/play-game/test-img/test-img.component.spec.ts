import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TestImgComponent } from './test-img.component';

describe('TestImgComponent', () => {
  let component: TestImgComponent;
  let fixture: ComponentFixture<TestImgComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TestImgComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TestImgComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
