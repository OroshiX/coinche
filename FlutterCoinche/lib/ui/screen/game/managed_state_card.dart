import 'dart:math';

import 'package:FlutterCoinche/domain/dto/card.dart';
import 'package:FlutterCoinche/domain/dto/player_position.dart';
import 'package:FlutterCoinche/domain/extensions/CardWonOrCenter.dart';
import 'package:FlutterCoinche/ui/screen/game/animated_card.dart';
import 'package:FlutterCoinche/ui/screen/game/move_card.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:states_rebuilder/states_rebuilder.dart';

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
        ReactiveModel<CardWonOrCenter> model, MoveCardState moveCard,
        {bool init = false}) {
      // if model is identical to in memory, it means we have 4 cards on table,
      // and we need to force the card to go into our tricks after a delay
      if (cardModel == model.value.item1) {
        assert(model.value.item2 != null,
            "we should win the card $cardModel, but the position of the winner is null");
        print("winning card $cardModel [case1]");
        _winPli(model.value.item2, moveCard, size);
        return;
      }

      // if newCard is null, animate the previous one to the tricks of the winner
      if (model.value.item1 == null && model.value.item2 != null) {
        print("winning card $cardModel [case2]");
        _winPli(model.value.item2, moveCard, size);
        return;
      }
      // if newCard is NOT null, and it is a new card, put it in the middle
      if (cardModel != model.value.item1 && model.value.item1 != null) {
        assert(
            model.value.item2 == null,
            "The card should be in the middle, "
            "but it is assigned to be won by player ${model.value.item2}.");
        // animate card to the center of the table
        cardModel = model.value.item1;
        print("playing card $cardModel");
        _putInCenter(moveCard, model.value.item1, size);
        return;
      }
      throw "Illegal state: We have new values ${model.value.item1} "
          "and ${model.value.item2} but it should not happen!!!!";
    }

    return StateBuilder<CardWonOrCenter>(
      models: [RM.get<CardWonOrCenter>(name: axisDirection.simpleName())],
      initState: (context, model) {
        model.whenConnectionState(onIdle: () {
          print("[managed] for $axisDirection: idle");
        }, onWaiting: () {
          print("[managed] for $axisDirection: Waiting");
        }, onData: (state) {
          print("[managed] for $axisDirection: received data: $state");
          _onChangedState(model, keyCard.currentState, init: true);
        }, onError: (e) {
          print("[managed] for $axisDirection: Error $e");
        });
      },
      onSetState: (context, model) {
        _onChangedState(model, keyCard.currentState);
      },
      builderWithChild: (context, model, child) => child,
      child: MoveCard(
          key: keyCard,
          cardWidth: cardWidth,
          cardHeight: cardHeight,
          card: cardModel,
          offsetAndRotationName: offsetRotationName),
    );
  }

  _winPli(AxisDirection winnerPosTable, MoveCardState moveCardState,
      Size screenSize) {
    print("$winnerPosTable is Winning trick");
    moveCardState.setAnim(_offsetAndRotationPlayer(winnerPosTable, screenSize),
        origin: _offsetRotationCenter);
  }

  _putInCenter(
      MoveCardState moveCardState, CardModel cardModel, Size screenSize) {
    print("we put card in center");
    moveCardState.setAnim(_offsetRotationCenter,
        origin: _offsetAndRotationPlayer(axisDirection, screenSize));
    moveCardState.setCard(cardModel);
  }

  OffsetAndRotation get _offsetRotationCenter =>
      OffsetAndRotation(_getOffsetCenter, _getRotationCenter);

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
      AxisDirection player, Size screenSize) {
    return OffsetAndRotation(
        _getOffsetPli(player, screenSize), _getRotationPli(player));
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
