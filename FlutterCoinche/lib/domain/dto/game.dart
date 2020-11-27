//@dart=2.9
import 'package:coinche/domain/dto/bid.dart';
import 'package:coinche/domain/dto/card.dart';
import 'package:coinche/domain/dto/nicknames.dart';
import 'package:coinche/domain/dto/player_position.dart';
import 'package:coinche/domain/dto/score.dart';
import 'package:coinche/domain/dto/table_state.dart';
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
      this.state = TableState.joining,
      this.nextPlayer = PlayerPosition.north,
      this.myPosition = PlayerPosition.north,
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
      id: withId ?? id,
      nicknames: withNicknames ?? nicknames,
      cards: withCards ?? cards,
      onTable: withOnTable ?? onTable,
      state: withState ?? state,
      nextPlayer: withNextPlayer ?? nextPlayer,
      myPosition: withMyPosition ?? myPosition,
      bids: withBids ?? bids,
      currentBid: withCurrentBid ?? currentBid,
      score: withScore ?? score,
      winnerLastTrick: withWinnerLastTrick ?? winnerLastTrick,
      lastTrick: withLastTrick ?? lastTrick,
    );
  }
}
