import { Injectable } from '@angular/core';
import { AngularFireAuth } from '@angular/fire/auth';
import { AngularFirestore, AngularFirestoreCollection } from '@angular/fire/firestore';
import { from, Observable } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { Game } from '../../shared/models/game';

@Injectable({
  providedIn: 'root'
})
export class ApiFirestoreService {
  private gameCollection: AngularFirestoreCollection<any>;

  constructor(private afs: AngularFirestore, private afAuth: AngularFireAuth) {
    this.gameCollection = afs.collection<any>('playersSets');
  }

  getTableGame(gameId: string): Observable<any> {
    return from(this.afAuth.currentUser)
      .pipe(
        switchMap(usr => {
          return this.gameCollection
            .doc(gameId)
            .collection('players')
            .doc(usr.uid)
            .snapshotChanges()
            .pipe(
              map(a => {
                const data = a.payload.data() as Game;
                const id = a.payload.id;
                return {id, ...data};
              })
            );
        })
      );
  }

}
