import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class MessagesService {
  messages: string[] = [];

  constructor() {
  }

  addMsg(msg: string) {
    this.messages.push(msg);
    console.log(this.messages);
  }
}
