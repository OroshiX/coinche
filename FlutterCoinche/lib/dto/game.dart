import 'package:FlutterCoinche/dto/bid.dart';
import 'package:FlutterCoinche/dto/card.dart';
import 'package:FlutterCoinche/dto/nicknames.dart';
import 'package:FlutterCoinche/dto/player_position.dart';
import 'package:FlutterCoinche/dto/score.dart';
import 'package:FlutterCoinche/dto/table_state.dart';
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
      this.bids,
      this.currentBid,
      this.score,
      this.winnerLastTrick,
      this.lastTrick});

  factory Game.fromJson(Map<String, dynamic> json) => _$GameFromJson(json);

  Map<String, dynamic> toJson() => _$GameToJson(this);

  @override
  String toString() {
    return "game: $id, myCards: $cards";
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
}
