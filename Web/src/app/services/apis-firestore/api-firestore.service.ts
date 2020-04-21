import { Injectable } from '@angular/core';
import { AngularFirestore, AngularFirestoreCollection } from '@angular/fire/firestore';
import { Observable } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { Game } from '../../shared/models/game';
import { UserLocalStorageService } from '../session-storage/user-local-storage.service';

@Injectable({
  providedIn: 'root'
})
export class ApiFirestoreService {
  private gameCollection: AngularFirestoreCollection<any>;

  constructor(private afs: AngularFirestore, private userService: UserLocalStorageService) {
    this.gameCollection = afs.collection<any>('playersSets');
  }

  getTableGame(gameId: string): Observable<any> {
    return this.userService.getCurrentUser$()
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
