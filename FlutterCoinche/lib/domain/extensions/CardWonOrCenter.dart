import 'package:FlutterCoinche/domain/dto/card.dart';
import 'package:flutter/cupertino.dart';
import 'package:tuple/tuple.dart';

class CardWonOrCenter extends Tuple4<CardModel, AxisDirection, int, bool> {
  CardWonOrCenter(
      {@required CardModel cardModel,
      @required AxisDirection position,
      int timestamp, bool shouldAnim = true})
      : super(
            cardModel,
            position,
            timestamp == null
                ? DateTime.now().millisecondsSinceEpoch
                : timestamp, shouldAnim);
}
