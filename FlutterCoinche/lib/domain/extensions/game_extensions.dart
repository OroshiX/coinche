import 'package:coinche/domain/dto/bid.dart';
import 'package:coinche/domain/dto/game.dart';
import 'package:coinche/domain/dto/player_position.dart';
import 'package:coinche/domain/logic/calculus.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/foundation.dart';

extension GameExtensions on Game {
  List<Aspects> different(Game old) {
    if (old == null) return Aspects.values;
    List<Aspects> res = [];
    if (old.id != id) res.add(Aspects.ID);
    if (!setEquals(old.cards.toSet(), cards.toSet())) res.add(Aspects.CARDS);
    if (old.score != score) res.add(Aspects.SCORE);
    if (old.state != state) res.add(Aspects.STATE);
    if (!listEquals(old.bids, bids)) res.add(Aspects.ALL_BIDS);
    if (old.nicknames != nicknames) res.add(Aspects.NICKNAMES);
    if (!listEquals(old.onTable, onTable)) res.add(Aspects.ON_TABLE);
    if (old.nextPlayer != nextPlayer) res.add(Aspects.NEXT_PLAYER);
    if (old.myPosition != myPosition) res.add(Aspects.MY_POSITION);
    if (!listEquals(old.lastTrick, lastTrick) ||
        old.winnerLastTrick != winnerLastTrick) res.add(Aspects.LAST_TRICK);
    if (old.currentBid != currentBid) res.add(Aspects.CURRENT_BID);
    if (this._getChangeBid(old, AxisDirection.up)) res.add(Aspects.BIDS_TOP);
    if (this._getChangeBid(old, AxisDirection.right))
      res.add(Aspects.BIDS_RIGHT);
    if (this._getChangeBid(old, AxisDirection.left)) res.add(Aspects.BIDS_LEFT);
    if (this._getChangeBid(old, AxisDirection.down))
      res.add(Aspects.BIDS_BOTTOM);
    return res;
  }

  bool isWon() {
    final northWon = (score.northSouth - score.eastWest) > 0;
    return northWon &&
            (myPosition == PlayerPosition.NORTH ||
                myPosition == PlayerPosition.SOUTH) ||
        !northWon &&
            (myPosition == PlayerPosition.EAST ||
                myPosition == PlayerPosition.WEST);
  }

  bool _getChangeBid(Game old, AxisDirection posTable) {
    if (old == null || this.bids == null) return null;
    final oldBids = old.bidsOfPosition(posTable);
    final newBids = bidsOfPosition(posTable);
    final change = changeBid(oldBids, newBids, posTable);
    return change != null;
  }

  static Change changeBid(
      List<Bid> oldBids, List<Bid> newBids, AxisDirection posTable) {
    if (oldBids == null || newBids == null) return null;
    final oldLength = oldBids.length;
    final newLength = newBids.length;
    if (oldLength == newLength) return null;
    TypeChange typeChange =
        oldLength > newLength ? TypeChange.DELETE : TypeChange.INSERT;
    return Change(typeChange, (oldLength - newLength).abs());
  }

  List<Bid> bidsOfPosition(AxisDirection posTable) {
    if (bids == null) return [];
    final cardinal = getPosTableToCardinal(myPosition)[posTable];
    return bids.where((element) => element.position == cardinal).toList();
  }
}

class Change {
  final TypeChange typeChange;
  final int nbChanges;

  const Change(this.typeChange, this.nbChanges);
}

enum TypeChange { INSERT, DELETE }

enum Aspects {
  ID,
  CARDS,
  STATE,
  ALL_BIDS,
  BIDS_LEFT,
  BIDS_RIGHT,
  BIDS_TOP,
  BIDS_BOTTOM,
  SCORE,
  NICKNAMES,
  ON_TABLE,
  NEXT_PLAYER,
  MY_POSITION,
  CURRENT_BID,
  LAST_TRICK,
  COLORS,
}

Aspects fromPositionBid(AxisDirection posTable) {
  switch (posTable) {
    case AxisDirection.up:
      return Aspects.BIDS_TOP;
    case AxisDirection.right:
      return Aspects.BIDS_RIGHT;
    case AxisDirection.down:
      return Aspects.BIDS_BOTTOM;
    case AxisDirection.left:
      return Aspects.BIDS_LEFT;
  }
  return Aspects.ALL_BIDS;
}
