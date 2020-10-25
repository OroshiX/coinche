import 'dart:math';

import 'package:coinche/domain/dto/bid.dart';
import 'package:coinche/domain/dto/card.dart';
import 'package:coinche/domain/dto/player_position.dart';
import 'package:coinche/domain/dto/table_state.dart';
import 'package:flutter/cupertino.dart';

extension CardsExtension on List<CardModel> {
  sortCards(TableState state, Bid currentBid) {
    this.sort((CardModel c1, CardModel c2) {
      if (c1.color.index < c2.color.index) return -1;
      if (c1.color.index > c2.color.index) return 1;
      final trump =
          state == TableState.PLAYING && currentBid.cardColor() == c1.color;
      return compareValue(c2.value, c1.value, trump);
    });
  }
}

extension CardsPlayedExtension on List<CardPlayed> {
  CardPlayed atPosition(AxisDirection posTable,
      Map<AxisDirection, PlayerPosition> posTableToCardinal) {
    assert(posTableToCardinal != null, "posTable is null");
    return this.firstWhere(
      (element) => element.position == posTableToCardinal[posTable],
      orElse: () => null,
    );
  }
}

CardModel randomCard() {
  var r = Random();
  return CardModel(
      value: CardValue.values[r.nextInt(CardValue.values.length)],
      color: CardColor.values[r.nextInt(CardColor.values.length)]);
}

CardPlayed getRandomCardTable(PlayerPosition position) {
  return CardPlayed(position: position, card: randomCard());
}
