import 'package:FlutterCoinche/domain/dto/card.dart';
import 'package:flutter/cupertino.dart';
import 'package:tuple/tuple.dart';

class CardWonOrCenter extends Tuple2<CardModel, AxisDirection> {
  CardWonOrCenter({@required CardModel cardModel, @required AxisDirection position})
      : super(cardModel, position);
}
