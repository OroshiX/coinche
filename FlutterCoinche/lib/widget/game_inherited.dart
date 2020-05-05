import 'package:FlutterCoinche/dto/game.dart';
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
    return game.id != old.game.id ||
        game.cards != old.game.cards ||
        game.score != old.game.score ||
        game.state != old.game.state ||
        game.bids != old.game.bids ||
        game.nicknames != old.game.nicknames ||
        game.onTable != old.game.onTable ||
        game.nextPlayer != old.game.nextPlayer ||
        game.myPosition != old.game.myPosition ||
        game.lastTrick != old.game.lastTrick ||
        game.currentBid != old.game.currentBid ||
        !mapEquals(map, old.map);
  }

  @override
  bool updateShouldNotifyDependent(GameInherited old, Set<Aspects> aspects) {
    return (aspects.contains(Aspects.ID) && old.game.id != game.id) ||
        (aspects.contains(Aspects.CARDS) && old.game.cards != game.cards) ||
        (aspects.contains(Aspects.SCORE) && old.game.score != game.score) ||
        (aspects.contains(Aspects.STATE) && old.game.state != game.state) ||
        (aspects.contains(Aspects.BIDS) && old.game.bids != game.bids) ||
        (aspects.contains(Aspects.NICKNAMES) &&
            old.game.nicknames != game.nicknames) ||
        (aspects.contains(Aspects.ON_TABLE) &&
            old.game.onTable != game.onTable) ||
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
