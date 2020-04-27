import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { Bid } from '../../../shared/models/collection-game';
import { colorList } from '../../../shared/models/play';
import { backCardImg, iconClub, } from '../services/card-image.service';

@Component({
  selector: 'app-bid',
  templateUrl: './bid.component.html',
  styleUrls: ['./bid.component.scss']
})
export class BidComponent implements OnInit {
  @Input() isSmallScreen: boolean;
  @Input() isMyBid: boolean;
  @Output() bidChosen = new EventEmitter<Bid>();

  formBid: FormGroup;

  backCardImg = `${backCardImg} 0px 0px`;
  iconClub = `${iconClub} 0px 0px`;

  bidPoints: string[];
  cardColors: string[];
  bidTypes: string[];

  constructor(private fb: FormBuilder) {
    this.cardColors = [...colorList];
  }

  get bidPointControl(): FormControl {
    return this.formBid.get('bidPointControl') as FormControl;
  }

  get bidColorControl(): FormControl {
    return this.formBid.get('bidColorControl') as FormControl;
  }

  get bidTypeControl(): FormControl {
    return this.formBid.get('bidTypeControl') as FormControl;
  }

  ngOnInit(): void {
    this.cardColors = [...colorList];
    this.formBid = this.buildFormGroup();
    this.bidTypeControl.valueChanges.subscribe(val => {
      console.log(val);
    });
  }

  submit() {
    console.log('submit');
  }

  private buildFormGroup(): FormGroup {
    return this.fb.group({
      bidPointControl: this.fb.control(''),
      bidColorControl: this.fb.control(''),
      bidTypeControl: this.fb.control(''),
    });
  }

}
