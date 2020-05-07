import 'package:FlutterCoinche/domain/dto/bid.dart';
import 'package:FlutterCoinche/domain/dto/card.dart';
import 'package:FlutterCoinche/domain/dto/nicknames.dart';
import 'package:FlutterCoinche/domain/dto/player_position.dart';
import 'package:FlutterCoinche/domain/dto/score.dart';
import 'package:FlutterCoinche/domain/dto/table_state.dart';
import 'package:equatable/equatable.dart';
import 'package:json_annotation/json_annotation.dart';

part 'game.g.dart';

@JsonSerializable(explicitToJson: true)
class Game extends Equatable {
  final String id;
  final Nicknames nicknames;
  final List<CardModel> cards;
  final List<CardPlayed> onTable;
  final TableState state;
  final PlayerPosition nextPlayer;
  final PlayerPosition myPosition;
  final List<Bid> bids;
  final Bid currentBid;
  final Score score;
  @JsonKey(nullable: true)
  final PlayerPosition winnerLastTrick;
  final List<CardPlayed> lastTrick;

  Game(
      {this.id,
      this.nicknames,
      this.cards,
      this.onTable,
      this.state,
      this.nextPlayer,
      this.myPosition,
      this.bids = const [],
      this.currentBid,
      this.score,
      this.winnerLastTrick,
      this.lastTrick});

  factory Game.fromJson(Map<String, dynamic> json) => _$GameFromJson(json);

  Map<String, dynamic> toJson() => _$GameToJson(this);

  @override
  String toString() {
    return "game: $id\nmyCards: $cards\nonTable: $onTable\nstate: $state\n"
        "me: $myPosition\nscore: $score\nnicknames: $nicknames\nbids: $bids";
  }

  @override
  List<Object> get props => [
        id,
        nicknames,
        cards,
        onTable,
        state,
        nextPlayer,
        myPosition,
        bids,
        currentBid,
        score,
        winnerLastTrick,
        lastTrick
      ];

  sortCards() {
    cards.sort((CardModel c1, CardModel c2) {
      if (c1.color.index < c2.color.index) return -1;
      if (c1.color.index > c2.color.index) return 1;
      final trump =
          state == TableState.PLAYING && currentBid.cardColor() == c1.color;
      return compareValue(c2.value, c1.value, trump);
    });
  }
}
