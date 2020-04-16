import { Injectable } from '@angular/core';
import { AngularFirestore, AngularFirestoreCollection } from '@angular/fire/firestore';

@Injectable({
  providedIn: 'root'
})
export class ApiFirestoreService {
  private tableCollection: AngularFirestoreCollection<any>;
  constructor(private afs: AngularFirestore) {
  }

  getTable(gameId: string) {
  }
}
