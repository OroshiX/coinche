import 'package:FlutterCoinche/dto/bid.dart';
import 'package:FlutterCoinche/dto/card.dart';
import 'package:FlutterCoinche/dto/nicknames.dart';
import 'package:FlutterCoinche/dto/player_position.dart';
import 'package:FlutterCoinche/dto/score.dart';
import 'package:FlutterCoinche/dto/table_state.dart';
import 'package:json_annotation/json_annotation.dart';

part 'game.g.dart';

@JsonSerializable(explicitToJson: true)
class Game {
  String id;
  Nicknames nicknames;
  List<CardModel> cards;
  List<CardPlayed> onTable;
  TableState state;
  PlayerPosition nextPlayer;
  PlayerPosition myPosition;
  List<Bid> bids;
  Bid currentBid;
  Score score;
  @JsonKey(nullable: true)
  PlayerPosition winnerLastTrick;
  List<CardPlayed> lastTrick;

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
}
