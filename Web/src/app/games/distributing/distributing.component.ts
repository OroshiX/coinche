import { Component, OnInit } from '@angular/core';
import { ApiFirestoreService } from '../../services/apis-firestore/api-firestore.service';

@Component({
  selector: 'app-distributing',
  templateUrl: './distributing.component.html',
  styleUrls: ['./distributing.component.scss']
})
export class DistributingComponent implements OnInit {
  data: any;
  constructor(private firestoreService: ApiFirestoreService) { }

  ngOnInit(): void {
    const gameId = 'DE6nV9kAg5YB8mMaJlEh';
    this.firestoreService.getTableGame(gameId).subscribe(data => {
      this.data = data;
      console.log(JSON.stringify(data.currentBid));
      console.log(JSON.stringify(data.myPosition));
      console.log(JSON.stringify(data.nextPlayer));
      console.log(JSON.stringify(data.nicknames));
      console.log(JSON.stringify(data.state));
      console.log(JSON.stringify(data.score));
      console.log(JSON.stringify(data.cards));
    })
  }

}
