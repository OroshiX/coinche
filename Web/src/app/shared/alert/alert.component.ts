import { Component, OnDestroy, OnInit } from '@angular/core';
import { Subscription } from 'rxjs';
import { AlertService } from './alert.service';

interface AlertMessage {
  text: string,
  type: string
}

@Component({
  selector: 'app-alert',
  templateUrl: './alert.component.html',
  styleUrls: ['./alert.component.scss']
})
export class AlertComponent implements OnInit, OnDestroy {

  private subscription: Subscription;
  message: AlertMessage = {
    text: 'Sorry, you are not authorized to play !',
    type: 'success'
  };

  constructor(private alertService: AlertService) {
  }

  ngOnInit() {
    this.subscription = this.alertService.getMessage().subscribe(message => {
      this.message.type = 'success';
    });
  }

  ngOnDestroy() {
    this.subscription.unsubscribe();
  }
}
