import { Component, Input, OnChanges, OnInit, SimpleChanges } from '@angular/core';
import { CardView } from '../../../shared/models/play';
import { PlayGameHelperService } from '../services/play-game-helper.service';

@Component({
  selector: 'app-mini-card',
  templateUrl: './mini-card.component.html',
  styleUrls: ['./mini-card.component.scss']
})
export class MiniCardComponent implements OnInit, OnChanges {
  @Input() card: CardView;
  cardLabel: string;
  emoji: string;
  cardLabelEmoji: string;
  isBlack: boolean;

  constructor(private helper: PlayGameHelperService) {
  }

  ngOnInit(): void {
  }

  ngOnChanges(changes: SimpleChanges): void {
    const cardEmoji = this.helper.buildEmojiCard(this.card);
    this.cardLabel = cardEmoji.cardLabel;
    this.emoji = cardEmoji.cardEmoji;
    this.cardLabelEmoji = cardEmoji.cardLabelEmoji;
    this.isBlack= cardEmoji.isBlack;
  }

}
