import 'package:FlutterCoinche/domain/dto/game.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:tuple/tuple.dart';

class GameInherited extends InheritedModel<Aspects> {
  final Game game;
  final Map<AxisDirection, Tuple2<Color, String>> map;

  const GameInherited(
      {@required this.game, @required this.map, @required Widget child})
      : super(child: child);

  @override
  bool updateShouldNotify(GameInherited old) {
    print("changed: ${old.game} -> $game NO ASPECTS");
    return game != old.game || !mapEquals(map, old.map);
  }

  @override
  bool updateShouldNotifyDependent(GameInherited old, Set<Aspects> aspects) {
    final res = (aspects.contains(Aspects.ID) && old.game.id != game.id) ||
        (aspects.contains(Aspects.CARDS) && old.game.cards != game.cards) ||
        (aspects.contains(Aspects.SCORE) && old.game.score != game.score) ||
        (aspects.contains(Aspects.STATE) && old.game.state != game.state) ||
        (aspects.contains(Aspects.BIDS) && old.game.bids != game.bids) ||
        (aspects.contains(Aspects.NICKNAMES) &&
            old.game.nicknames != game.nicknames) ||
        (aspects.contains(Aspects.ON_TABLE) &&
            !listEquals(old.game.onTable, game.onTable)) ||
        (aspects.contains(Aspects.NEXT_PLAYER) &&
            old.game.nextPlayer != game.nextPlayer) ||
        (aspects.contains(Aspects.MY_POSITION) &&
            old.game.myPosition != game.myPosition) ||
        (aspects.contains(Aspects.LAST_TRICK) &&
            (old.game.lastTrick != game.lastTrick ||
                old.game.winnerLastTrick != game.winnerLastTrick)) ||
        (aspects.contains(Aspects.CURRENT_BID) &&
            old.game.currentBid != game.currentBid) ||
        (aspects.contains(Aspects.COLORS) && !mapEquals(old.map, map));
    print("-> ${res ? "================\n\nchanged: ${old.game} -> $game [aspects: $aspects]" : "same"}");
    return res;
  }

  static GameInherited of(BuildContext context, {Aspects aspectType}) {
    return InheritedModel.inheritFrom<GameInherited>(context,
        aspect: aspectType);
  }
}

enum Aspects {
  ID,
  CARDS,
  STATE,
  BIDS,
  SCORE,
  NICKNAMES,
  ON_TABLE,
  NEXT_PLAYER,
  MY_POSITION,
  CURRENT_BID,
  LAST_TRICK,
  COLORS,
}
