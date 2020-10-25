import 'dart:math';

import 'package:coinche/domain/dto/card.dart';
import 'package:coinche/domain/dto/player_position.dart';
import 'package:coinche/domain/extensions/CardWonOrCenter.dart';
import 'package:coinche/state/cards_on_table_model.dart';
import 'package:coinche/ui/screen/game/table/animated_card.dart';
import 'package:coinche/ui/screen/game/table/move_card.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

class ManagedStateCard extends StatelessWidget {
  final AxisDirection axisDirection;
  final double cardWidth, cardHeight;
  final String offsetRotationName;
  static const String offset = "-offset", rotate = "-rotate";

  ManagedStateCard({
    Key key,
    @required this.axisDirection,
    @required this.cardWidth,
    @required this.cardHeight,
  })  : assert(axisDirection != null, "Axis direction is null!"),
        offsetRotationName = axisDirection.simpleName() + offset + rotate,
        super(key: key);

  @override
  Widget build(BuildContext context) {
    final size = MediaQuery.of(context).size;
    CardModel cardModel;

    final keyCard = GlobalKey<MoveCardState>();
    void _onChangedState(
        CardWonOrCenter cardWonOrCenter, MoveCardState moveCard) {
      if (moveCard == null) {
        print("not yet initialized");
        return;
      }
      // if model is identical to in memory, it means we have 4 cards on table,
      // and we need to force the card to go into our tricks after a delay
      if (cardModel == cardWonOrCenter.cardModel) {
        assert(cardWonOrCenter.position != null,
            "we should win the card $cardModel, but the position of the winner is null");
        print("winning card $cardModel [case1]");
        _winPli(cardWonOrCenter.position, moveCard, size,
            timestamp: cardWonOrCenter.timeStamp,
            shouldAnim: cardWonOrCenter.shouldAnim);
        return;
      }

      // if newCard is null, animate the previous one to the tricks of the winner
      if (cardWonOrCenter.cardModel == null &&
          cardWonOrCenter.position != null) {
        print("winning card $cardModel [case2]");
        _winPli(cardWonOrCenter.position, moveCard, size,
            timestamp: cardWonOrCenter.timeStamp,
            shouldAnim: cardWonOrCenter.shouldAnim);
        return;
      }
      // if newCard is NOT null, and it is a new card, put it in the middle
      if (cardModel != cardWonOrCenter.cardModel &&
          cardWonOrCenter.cardModel != null) {
        assert(
            cardWonOrCenter.position == null,
            "The card should be in the middle, "
            "but it is assigned to be won by player ${cardWonOrCenter.position}.");
        // animate card to the center of the table
        cardModel = cardWonOrCenter.cardModel;
        print("playing card $cardModel");
        _putInCenter(moveCard, cardWonOrCenter.cardModel, size,
            timestamp: cardWonOrCenter.timeStamp,
            shouldAnim: cardWonOrCenter.shouldAnim);
        return;
      }
      throw "Illegal state: We have new values ${cardWonOrCenter.cardModel} "
          "and ${cardWonOrCenter.position} but it should not happen!!!!";
    }

    return Consumer<CardsOnTableModel>(
      builder: (context, value, child) => MoveCard(
          key: keyCard,
          cardWidth: cardWidth,
          cardHeight: cardHeight,
          card: value.fromAxisDirection(axisDirection).cardModel,
          offsetAndRotationName: offsetRotationName),
    );
  }

  _winPli(AxisDirection winnerPosTable, MoveCardState moveCardState,
      Size screenSize,
      {@required int timestamp, @required bool shouldAnim}) {
    print("$winnerPosTable is Winning trick");
    moveCardState.setAnim(
        _offsetAndRotationPlayer(winnerPosTable, screenSize, timestamp),
        origin: _offsetRotationCenter(timestamp),
        shouldAnim: shouldAnim);
  }

  _putInCenter(
      MoveCardState moveCardState, CardModel cardModel, Size screenSize,
      {@required int timestamp, @required bool shouldAnim}) {
    moveCardState.setAnim(_offsetRotationCenter(timestamp),
        origin: _offsetAndRotationPlayer(axisDirection, screenSize, timestamp),
        shouldAnim: shouldAnim);
    moveCardState.cardModel = cardModel;
  }

  OffsetAndRotation _offsetRotationCenter(int timestamp) =>
      OffsetAndRotation(_getOffsetCenter, _getRotationCenter, timestamp);

  Offset get _getOffsetCenter {
    switch (axisDirection) {
      case AxisDirection.up:
        return Offset(0, -cardHeight / 2);
      case AxisDirection.right:
        return Offset(cardHeight / 2, 0);
      case AxisDirection.down:
        return Offset(0, cardHeight / 2);
      case AxisDirection.left:
        return Offset(-cardHeight / 2, 0);
    }
    return Offset.zero;
  }

  double get _getRotationCenter {
    switch (axisDirection) {
      case AxisDirection.up:
      case AxisDirection.down:
        return 0;
      case AxisDirection.right:
      case AxisDirection.left:
        return pi / 2;
    }
    return 0;
  }

  OffsetAndRotation _offsetAndRotationPlayer(
      AxisDirection player, Size screenSize, int timestamp) {
    return OffsetAndRotation(
        _getOffsetPli(player, screenSize), _getRotationPli(player), timestamp);
  }

  Offset _getOffsetPli(AxisDirection winnerPosTable, Size screenSize) {
    switch (winnerPosTable) {
      case AxisDirection.up:
        return Offset(0, -(screenSize.height + cardHeight) / 2);
      case AxisDirection.right:
        return Offset((screenSize.width + cardWidth) / 2, 0);
      case AxisDirection.down:
        return Offset(0, (screenSize.height + cardHeight) / 2);
      case AxisDirection.left:
        return Offset(-(screenSize.width + cardWidth) / 2, 0);
    }
    return Offset.zero;
  }

  double _getRotationPli(AxisDirection winnerPosTable) {
    switch (winnerPosTable) {
      case AxisDirection.up:
      case AxisDirection.down:
        return 0;
      case AxisDirection.right:
      case AxisDirection.left:
        return pi / 2;
    }
    return 0;
  }
}
