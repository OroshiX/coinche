import { CdkStepper } from '@angular/cdk/stepper';
import { Component, Input, OnInit, ViewChild } from '@angular/core';

@Component({
  selector: 'app-state',
  templateUrl: './state.component.html',
  styleUrls: ['./state.component.scss']
})
export class StateComponent implements OnInit {
  @ViewChild('stepper') stepper: CdkStepper;

  @Input() state: any;

  constructor() {
  }

  ngOnInit(): void {
  }

  onNext() {
    this.stepper.next();
  }

  onReset() {
    this.stepper.reset();
  }
}
