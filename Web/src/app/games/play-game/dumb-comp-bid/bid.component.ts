import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { Bid } from '../../../shared/models/collection-game';
import { cardIdList, cardIdListEnum, colorList, colorListObj } from '../../../shared/models/play';
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
    console.log(this.cardColors);
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
    console.log(cardIdListEnum);
    console.log(cardIdList);
    console.log(colorListObj);
    console.log(colorList);
    this.cardColors = [...colorList];
    console.log(this.cardColors);
    this.formBid = this.buildFormGroup();
    this.bidTypeControl.valueChanges.subscribe(val => {
      console.log(val);
    });
  }

  /*ngOnChanges(changes: SimpleChanges): void {
    console.log('');
  }*/

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
