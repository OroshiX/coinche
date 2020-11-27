import 'package:coinche/domain/extensions/card_won_or_center.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/rendering.dart';

class CardsOnTableModel extends ChangeNotifier {
  late CardWonOrCenter _left, _right, _up, _down;
  CardWonOrCenter get left => _left;
  CardWonOrCenter get right => _right;
  CardWonOrCenter get up => _up;
  CardWonOrCenter get down => _down;
  set left(CardWonOrCenter v) {
    _left = v;
    notifyListeners();
  }

  set right(CardWonOrCenter v) {
    _right = v;
    notifyListeners();
  }

  set up(CardWonOrCenter v) {
    _up = v;
    notifyListeners();
  }

  set down(CardWonOrCenter v) {
    _down = v;
    notifyListeners();
  }

  CardWonOrCenter fromAxisDirection(AxisDirection axisDirection) {
    switch (axisDirection) {
      case AxisDirection.up:
        return up;
      case AxisDirection.right:
        return right;
      case AxisDirection.down:
        return down;
      case AxisDirection.left:
        return left;
    }
  }

  CardsOnTableModel() {
    _left = CardWonOrCenter(cardModel: null, position: null);
    _right = CardWonOrCenter(cardModel: null, position: null);
    _up = CardWonOrCenter(cardModel: null, position: null);
    _down = CardWonOrCenter(cardModel: null, position: null);
  }
}
