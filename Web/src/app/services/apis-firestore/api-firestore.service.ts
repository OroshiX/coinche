import { Injectable } from '@angular/core';
import { AngularFirestore, AngularFirestoreCollection } from '@angular/fire/firestore';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { Game } from '../../shared/models/game';
import { SessionStorageService } from '../session-storage/session-storage.service';

@Injectable({
  providedIn: 'root'
})
export class ApiFirestoreService {
  private gameCollection: AngularFirestoreCollection<any>;
  private games$: Observable<any>;

  uid: string;

  constructor(private afs: AngularFirestore, private sessionService: SessionStorageService) {
    this.gameCollection = afs.collection<any>('playersSets');
    // this.uid = this.sessionService.getUserUid();
  }

  getTableGame(gameId: string): Observable<any> {
    this.games$ = this.gameCollection
      .doc(gameId)
      .collection('players')
      .doc(this.sessionService.getUserUid())
      .snapshotChanges().pipe(
        map(a => {
          const data = a.payload.data() as Game;
          const id = a.payload.id;
          return {id, ...data};
        })
      );
    return this.games$;
  }
}
