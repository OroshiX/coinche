import 'dart:math';

import 'package:FlutterCoinche/domain/dto/bid.dart';
import 'package:FlutterCoinche/domain/dto/game.dart';
import 'package:FlutterCoinche/domain/logic/calculus.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/foundation.dart';

extension GameExtensions on Game {
  List<Aspects> different(Game old) {
    if (old == null) return Aspects.values;
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

  Change getChangeBid(List<Bid> oldBids, AxisDirection posTable) {
    if (oldBids == null || this.bids == null) return null;
    final cardinal = getPosTableToCardinal(myPosition)[posTable];
    final myBids =
        (bids?.where((element) => element.position == cardinal) ?? []).toList();
    final oldLength = oldBids.length;
    final newLength = myBids.length;
    if (oldLength == newLength) return null;
    TypeChange typeChange =
        oldLength > newLength ? TypeChange.DELETE : TypeChange.INSERT;
    final m = min(oldLength, newLength);
    for (var i = 0; i < m; i++) {
      if (myBids[i] == oldBids[i]) continue;
      return Change(typeChange, i);
    }
    return Change(typeChange, m);
  }
}

class Change {
  final TypeChange typeChange;
  final int position;

  const Change(this.typeChange, this.position);
}

enum TypeChange { INSERT, DELETE }

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
