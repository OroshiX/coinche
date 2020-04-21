import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { switchMap } from 'rxjs/operators';
import { CardImageService } from '../../games/game/services/card-image.service';
import { ApiFirestoreService } from '../../services/apis-firestore/api-firestore.service';
import { BreakpointService } from '../../services/breakpoint/breakpoint.service';
import { TableGame } from '../../shared/models/collection-game';
import { Card, CardView } from '../../shared/models/play';

@Component({
  selector: 'app-card',
  templateUrl: './card.component.html',
  styleUrls: ['./card.component.scss']
})
export class CardComponent implements OnInit {
  myCardMap: Map<string, CardView> = new Map<string, CardView>();
  map = new Map<number, string[]>();
  backCard: any;

  constructor(private service: CardImageService,
              private breakpointService: BreakpointService,
              private firestoreService: ApiFirestoreService,
              private cd: ChangeDetectorRef) {
    this.cd.detach();
  }

  ngOnInit(): void {
    const gameId = 'DE6nV9kAg5YB8mMaJlEh';
    this.breakpointService.layoutChanges$()
      .pipe(switchMap(() => this.firestoreService.getTableGame(gameId)))
      .subscribe((data: TableGame) => {
        this.updateCardLayoutForScreenChange(data.cards);
      });
  }

  onClickCard(events: any, i: number, card: any) {
    console.log(events);
    console.log(i);
    console.log(card);
    this.cd.detectChanges();
  }

  private updateCardLayoutForScreenChange(cards: Card[]) {
    if (this.breakpointService.isSmallScreen()) {
      this.map = this.service.getMapSmall();
      this.backCard = this.service.getBackCardSmall();
      this.myCardMap = this.service.buildMyDeckSmall(cards);
    } else {
      this.map = this.service.getMap();
      this.backCard = this.service.getBackCard();
      this.myCardMap = this.service.buildMyDeck(cards);
    }
    this.cd.detectChanges();
  }

}
