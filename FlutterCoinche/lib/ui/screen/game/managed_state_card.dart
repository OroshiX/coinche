import 'dart:math';

import 'package:FlutterCoinche/domain/dto/card.dart';
import 'package:FlutterCoinche/domain/dto/player_position.dart';
import 'package:FlutterCoinche/domain/extensions/CardWonOrCenter.dart';
import 'package:FlutterCoinche/ui/screen/game/moving_card.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:states_rebuilder/states_rebuilder.dart';

class ManagedStateCard extends StatelessWidget {
  final AxisDirection axisDirection;
  final double cardWidth, cardHeight;
  final String offsetName, rotateName;
  static const String offset = "-offset", rotate = "-rotate";

  ManagedStateCard({
    Key key,
    @required this.axisDirection,
    @required this.cardWidth,
    @required this.cardHeight,
  })  : assert (axisDirection != null, "Axis direction is null!"),
        offsetName = axisDirection.simpleName() + offset,
        rotateName = axisDirection.simpleName() + rotate,
        super(key: key);

  @override
  Widget build(BuildContext context) {
    final size = MediaQuery.of(context).size;
    CardModel cardModel;
    return Injector(
      inject: [
        Inject(() => Offset(0, 0), name: offsetName),
        Inject(() => 0.0, name: rotateName),
      ],
      builder: (BuildContext context) {
        final rmOffset = RM.get<Offset>(name: offsetName);
        final rmAngle = RM.get<double>(name: rotateName);
        void _onChangedState(ReactiveModel<CardWonOrCenter> model) {
          // if model is identical to in memory, it means we have 4 cards on table,
          // and we need to force the card to go into our tricks after a delay
          if (cardModel == model.value.item1) {
            assert(model.value.item2 != null,
                "we should win the card $cardModel, but the position of the winner is null");
            // TODO put a delay before this
            _winPli(model.value.item2, rmAngle, rmOffset, size);
            return;
          }

          // if newCard is null, animate the previous one to the tricks of the winner
          if (model.value.item1 == null && model.value.item2 != null) {
            _winPli(model.value.item2, rmAngle, rmOffset, size);
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
            _putInCenter(rmAngle, rmOffset);
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
              _onChangedState(model);
            }, onError: (e) {
              print("[managed] for $axisDirection: Error $e");
            });
          },
          onSetState: (context, model) {
            _onChangedState(model);
          },
          builder: (context, model) => MovingCard(
              cardWidth: cardWidth,
              cardHeight: cardHeight,
              card: cardModel,
              offsetName: offsetName,
              rotateName: rotateName),
        );
      },
    );
  }

  _winPli(AxisDirection winnerPosTable, ReactiveModel<double> rmAngle,
      ReactiveModel<Offset> rmOffset, Size screenSize) {
    rmAngle.setValue(() => _getRotationPli(winnerPosTable));
    rmOffset.setValue(() => _getOffsetPli(winnerPosTable, screenSize));
  }

  _putInCenter(ReactiveModel<double> rmAngle, ReactiveModel<Offset> rmOffset) {
    rmAngle.setValue(() => _getRotationCenter());
    rmOffset.setValue(() => _getOffsetCenter());
  }

  Offset _getOffsetCenter() {
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

  double _getRotationCenter() {
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
