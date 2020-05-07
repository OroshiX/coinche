import 'package:FlutterCoinche/dto/game.dart';
import 'package:flutter/foundation.dart';

extension GameExtensions on Game {
  List<Aspects> different(Game old) {
    List<Aspects> res = [];
    if (old.id != id) res.add(Aspects.ID);
    if (old.cards != cards) res.add(Aspects.CARDS);
    if (old.score != score) res.add(Aspects.SCORE);
    if (old.state != state) res.add(Aspects.STATE);
    if (old.bids != bids) res.add(Aspects.BIDS);
    if (old.nicknames != nicknames) res.add(Aspects.NICKNAMES);
    if (!listEquals(old.onTable, onTable)) res.add(Aspects.ON_TABLE);
    if (old.nextPlayer != nextPlayer) res.add(Aspects.NEXT_PLAYER);
    if (old.myPosition != myPosition) res.add(Aspects.MY_POSITION);
    if (old.lastTrick != lastTrick || old.winnerLastTrick != winnerLastTrick)
      res.add(Aspects.LAST_TRICK);
    if (old.currentBid != currentBid) res.add(Aspects.CURRENT_BID);
    return res;
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
