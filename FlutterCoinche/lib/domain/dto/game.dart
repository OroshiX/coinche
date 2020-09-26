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

  const Game(
      {this.id,
      this.nicknames = const Nicknames(),
      this.cards = const [],
      this.onTable = const [],
      this.state = TableState.JOINING,
      this.nextPlayer = PlayerPosition.NORTH,
      this.myPosition = PlayerPosition.NORTH,
      this.bids = const [],
      this.currentBid,
      this.score = const Score(),
      this.winnerLastTrick,
      this.lastTrick = const []});

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

  Game copy({
    String withId,
    Nicknames withNicknames,
    List<CardModel> withCards,
    List<CardPlayed> withOnTable,
    TableState withState,
    PlayerPosition withNextPlayer,
    PlayerPosition withMyPosition,
    List<Bid> withBids,
    Bid withCurrentBid,
    Score withScore,
    PlayerPosition withWinnerLastTrick,
    List<CardPlayed> withLastTrick,
  }) {
    return Game(
      id: withId != null ? withId : id,
      nicknames: withNicknames != null ? withNicknames : nicknames,
      cards: withCards != null ? withCards : cards,
      onTable: withOnTable != null ? withOnTable : onTable,
      state: withState != null ? withState : state,
      nextPlayer: withNextPlayer != null ? withNextPlayer : nextPlayer,
      myPosition: withMyPosition != null ? withMyPosition : myPosition,
      bids: withBids != null ? withBids : bids,
      currentBid: withCurrentBid != null ? withCurrentBid : currentBid,
      score: withScore != null ? withScore : score,
      winnerLastTrick:
          withWinnerLastTrick != null ? withWinnerLastTrick : winnerLastTrick,
      lastTrick: withLastTrick != null ? withLastTrick : lastTrick,
    );
  }
}
