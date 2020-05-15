import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs';
import { map } from 'rxjs/operators';
import { ApiGamesService } from '../../../services/apis/api-games.service';
import { PLAYER_POSITION } from '../../../shared/models/collection-game';
import { Trick } from '../../../shared/models/game';
import { AllTrick } from '../../../shared/models/game-interface';
import { AllTrickFlat, EmojiCardLabel, PlayCard } from '../../../shared/models/play';
import { shortenStr } from '../../../shared/utils/shorter-name';
import { ShowAllTricksDialogComponent } from '../dialog-comp-show-all-tricks/show-all-tricks-dialog.component';
import { PlayGameHelperService } from '../services/play-game-helper.service';

export class ShowAllTrick {
  allTrickData: Trick[];
  nicknames: string[];
}

@Component({
  selector: 'app-show-all-tricks',
  templateUrl: './show-all-tricks.component.html',
  styleUrls: ['./show-all-tricks.component.scss']
})
export class ShowAllTricksComponent implements OnInit, OnDestroy {
  @Input() myPosition: PLAYER_POSITION;
  @Input() nicknames: string[];

  sub: Subscription;
  dataToDialog = new ShowAllTrick();
  campList = [];

  constructor(private route: ActivatedRoute,
              private apiService: ApiGamesService,
              private helper: PlayGameHelperService,
              public dialog: MatDialog) {
  }

  ngOnInit(): void {
    const gameId = this.route.snapshot.params.id;
    this.sub = this.buildDataDialog(gameId)
      .pipe(
        map(data => data.map((el: Map<string, EmojiCardLabel>) => this.convertMapToObj(el))),
        map((data, idx) => data.map(trick => this.addCampToObj(trick, idx++)))
      )
      .subscribe(data => {
        this.dataToDialog.allTrickData = data;
        this.dataToDialog.nicknames = this.nicknames.map(nn => shortenStr(nn, 14));
        // console.log('allTrickData=====', data);
      });
  }

  ngOnDestroy(): void {
    if (this.sub) {
      this.sub.unsubscribe();
    }
  }

  openDialog(): void {
    const dialogRef = this.dialog.open(ShowAllTricksDialogComponent, {
      width: '500px',
      data: this.dataToDialog
    });

    dialogRef.afterClosed().subscribe(() => {
    });
  }

  private convertMapToObj(el: Map<string, EmojiCardLabel>): Trick {
    return new Trick({
      player1: el.get('0').cardLabelEmoji,
      player2: el.get('1').cardLabelEmoji,
      player3: el.get('2').cardLabelEmoji,
      player4: el.get('3').cardLabelEmoji
    });
  }

  private addCampToObj(trick: Trick, idx: number): Trick {
    trick.camp = this.campList[idx];
    return trick;
  }

  private buildDataDialog(gameId: string) {
    return this.apiService.showAllTricks(gameId)
      .pipe(
        map((tricks: AllTrick[]) => this.helper.buildAllTrickFlat(tricks)),
        map(res => res.map((tr: AllTrickFlat[]) => {
          const rowTrickMap = new Map<string, EmojiCardLabel | string>();
          const listPlayerPosOrdered = this.helper.playersPositionRefOrderedByMyPosZero(this.myPosition);
          tr.map(el => {
            const id = listPlayerPosOrdered.findIndex((p, idx) => p === el.position);
            if (id === 0) {
              this.campList.push(el.camp);
            }
            const emojiLabel = this.helper.buildEmojiCard(new PlayCard({
              color: el.color,
              value: el.value
            }), el.belote);
            rowTrickMap.set(id.toString(), emojiLabel);
          });
          return rowTrickMap;
        })),
      );
  }

}
