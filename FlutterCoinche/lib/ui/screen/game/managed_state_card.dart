import 'dart:math';

import 'package:FlutterCoinche/domain/dto/card.dart';
import 'package:FlutterCoinche/ui/screen/game/moving_card.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:states_rebuilder/states_rebuilder.dart';

class ManagedStateCard extends StatelessWidget {
  final AxisDirection axisDirection;
  final double cardWidth, cardHeight;
  final CardModel card;
  final String offsetName, rotateName;
  static const String offset = "offset", rotate = "rotate";

  ManagedStateCard({
    Key key,
    @required this.axisDirection,
    @required this.cardWidth,
    @required this.cardHeight,
    @required this.card,
  })  : offsetName = axisDirection.toString() + offset,
        rotateName = axisDirection.toString() + rotate,
        super(key: key);

  @override
  Widget build(BuildContext context) {
    final size = MediaQuery.of(context).size;
    return Injector(
      inject: [
        Inject(() => Offset(0, 0), name: offsetName),
        Inject(() => 0.0, name: rotateName),
      ],
      builder: (BuildContext context) {
        final rmOffset = RM.get<Offset>(name: offsetName);
        final rmRotate = RM.get<double>(name: rotateName);
        return Stack(
          children: [
            MovingCard(
                cardWidth: cardWidth,
                cardHeight: cardHeight,
                card: card,
                offsetName: offsetName,
                rotateName: rotateName),
            Positioned(
              right: 0,
              child: Column(children: [
                RaisedButton(
                    onPressed: () {
                      final r = Random();
                      rmOffset.setValue(() => Offset(
                          r.nextDouble() * MediaQuery.of(context).size.width,
                          r.nextDouble() * MediaQuery.of(context).size.height));
                      rmRotate.setValue(() =>
                          r.nextDouble() * 2 * pi * (r.nextBool() ? -1 : 1));
                    },
                    child: Text("New anim")),
                for (var winnerPosTable in AxisDirection.values)
                  RaisedButton(
                    onPressed: () {
                      _winPli(winnerPosTable, rmRotate, rmOffset, size);
                    },
                    child:
                        Text("get pli for ${winnerPosTable.toString().split(".").last}"),
                  ),
                RaisedButton(
                  onPressed: () {
                    rmOffset.setValue(() => _getOffsetCenter());
                    rmRotate.setValue(() => _getRotationCenter());
                  },
                  child: Text("Put in center"),
                )
              ]),
            ),
          ],
        );
      },
    );
  }

  _winPli(AxisDirection winnerPosTable, ReactiveModel<double> rmAngle,
      ReactiveModel<Offset> rmOffset, Size screenSize) {
    rmOffset.setValue(() => _getOffsetPli(winnerPosTable, screenSize));
    rmAngle.setValue(() => _getRotationPli(winnerPosTable));
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
