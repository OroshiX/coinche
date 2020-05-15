import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-test-img',
  templateUrl: './test-img.component.html',
  styleUrls: ['./test-img.component.scss']
})
export class TestImgComponent implements OnInit {
  img = '../../../assets/images/cards-individuals/7C.png';
  isCardDisabled = false;
  secondBtn = false;

  ngOnInit(): void {
  }

  onClick() {
    this.isCardDisabled = !this.isCardDisabled;
    console.log('Toggle Btn clicked');
  }
  OnDblClick(inp : string) {
    this.secondBtn = true;
    console.log('Btn double clicked', inp);
  }
}
