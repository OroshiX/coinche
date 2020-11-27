import 'package:coinche/domain/dto/bid.dart';
import 'package:coinche/domain/dto/game.dart';
import 'package:coinche/domain/dto/player_position.dart';
import 'package:coinche/domain/logic/calculus.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/foundation.dart';

extension GameExtensions on Game {
  List<Aspects> different(Game old) {
    // if (old == null) return Aspects.values;
    var res = <Aspects>[];
    if (old.id != id) res.add(Aspects.id);
    if (!setEquals(old.cards.toSet(), cards.toSet())) {
      res.add(Aspects.cards);
    }
    if (old.score != score) {
      res.add(Aspects.score);
    }
    if (old.state != state) {
      res.add(Aspects.state);
    }
    if (!listEquals(old.bids, bids)) {
      res.add(Aspects.allBids);
    }
    if (old.nicknames != nicknames) {
      res.add(Aspects.nicknames);
    }
    if (!listEquals(old.onTable, onTable)) {
      res.add(Aspects.onTable);
    }
    if (old.nextPlayer != nextPlayer) {
      res.add(Aspects.nextPlayer);
    }
    if (old.myPosition != myPosition) {
      res.add(Aspects.myPosition);
    }
    if (!listEquals(old.lastTrick, lastTrick) ||
        old.winnerLastTrick != winnerLastTrick) {
      res.add(Aspects.lastTrick);
    }
    if (old.currentBid != currentBid) {
      res.add(Aspects.currentBid);
    }
    if (_getChangeBid(old, AxisDirection.up)) {
      res.add(Aspects.bidsTop);
    }
    if (_getChangeBid(old, AxisDirection.right)) {
      res.add(Aspects.bidsRight);
    }
    if (_getChangeBid(old, AxisDirection.left)) res.add(Aspects.bidsLeft);
    if (_getChangeBid(old, AxisDirection.down)) {
      res.add(Aspects.bidsBottom);
    }
    return res;
  }

  bool isWon() {
    final northWon = (score.northSouth - score.eastWest) > 0;
    return northWon &&
            (myPosition == PlayerPosition.north ||
                myPosition == PlayerPosition.south) ||
        !northWon &&
            (myPosition == PlayerPosition.east ||
                myPosition == PlayerPosition.west);
  }

  bool _getChangeBid(Game? old, AxisDirection posTable) {
    if (old == null || bids == null) return false;
    final oldBids = old.bidsOfPosition(posTable);
    final newBids = bidsOfPosition(posTable);
    final change = changeBid(oldBids, newBids, posTable);
    return change != null;
  }

  static Change? changeBid(
      List<Bid> oldBids, List<Bid>? newBids, AxisDirection posTable) {
    if (newBids == null) return null;
    final oldLength = oldBids.length;
    final newLength = newBids.length;
    if (oldLength == newLength) return null;
    var typeChange =
        oldLength > newLength ? TypeChange.delete : TypeChange.insert;
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

enum TypeChange { insert, delete }

enum Aspects {
  id,
  cards,
  state,
  allBids,
  bidsLeft,
  bidsRight,
  bidsTop,
  bidsBottom,
  score,
  nicknames,
  onTable,
  nextPlayer,
  myPosition,
  currentBid,
  lastTrick,
  colors,
}

Aspects fromPositionBid(AxisDirection posTable) {
  switch (posTable) {
    case AxisDirection.up:
      return Aspects.bidsTop;
    case AxisDirection.right:
      return Aspects.bidsRight;
    case AxisDirection.down:
      return Aspects.bidsBottom;
    case AxisDirection.left:
      return Aspects.bidsLeft;
  }
}
