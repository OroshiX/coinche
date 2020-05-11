import 'package:FlutterCoinche/ui/screen/game/cards_on_table.dart';
import 'package:FlutterCoinche/ui/screen/game/drag_area.dart';
import 'package:flutter/material.dart';

class MiddleArea extends StatelessWidget {
  final double cardWidth, cardHeight;
  final Size screenSize;

  const MiddleArea({
    @required this.cardWidth,
    @required this.cardHeight,
    @required this.screenSize,
  });

  @override
  Widget build(BuildContext context) {
    return Stack(
      children: [
        CardsOnTable(
          minPadding: 2,
        ),
        Center(
          child: DragArea(
            screenSize: screenSize,
            minPadding: 2,
          ),
        ),
      ],
    );
  }
}
