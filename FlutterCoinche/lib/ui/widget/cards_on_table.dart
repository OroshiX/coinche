import 'dart:math';

import 'package:FlutterCoinche/domain/dto/card.dart';
import 'package:FlutterCoinche/domain/dto/player_position.dart';
import 'package:FlutterCoinche/domain/dto/table_state.dart';
import 'package:FlutterCoinche/ui/resources/dimens.dart';
import 'package:FlutterCoinche/ui/widget/card_widget.dart';
import 'package:FlutterCoinche/ui/widget/neumorphic_no_state.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';

class CardsOnTable extends StatelessWidget {
  static var built = 0;
  final List<CardPlayed> cardsOnTable;
  final Map<AxisDirection, PlayerPosition> posTableToCardinal;
  final CardPlayed cardLeft, cardTop, cardRight, cardMe;
  final double maxHeightCard, minHeightCard;
  final List<MapEntry<AxisDirection, CardPlayed>> orderedCards;
  final double minPadding;
  final TableState state;

  CardsOnTable(
      {this.cardsOnTable,
      this.posTableToCardinal,
      this.maxHeightCard = 400,
      this.minPadding,
      @required this.state,
      this.minHeightCard = 20})
      : assert(minHeightCard <= maxHeightCard),
        cardLeft = posTableToCardinal[AxisDirection.left] != null
            ? cardsOnTable.firstWhere(
                (element) =>
                    element.position == posTableToCardinal[AxisDirection.left],
                orElse: () => null)
            : null,
        cardTop = posTableToCardinal[AxisDirection.up] != null
            ? cardsOnTable.firstWhere(
                (element) =>
                    element.position == posTableToCardinal[AxisDirection.up],
                orElse: () => null)
            : null,
        cardRight = posTableToCardinal[AxisDirection.right] != null
            ? cardsOnTable.firstWhere(
                (element) =>
                    element.position == posTableToCardinal[AxisDirection.right],
                orElse: () => null,
              )
            : null,
        cardMe = posTableToCardinal[AxisDirection.down] != null
            ? cardsOnTable.firstWhere(
                (element) =>
                    element.position == posTableToCardinal[AxisDirection.down],
                orElse: () => null,
              )
            : null,
        orderedCards = List(4) {
    var index = cardsOnTable
        .indexWhere((element) => element.position == cardLeft?.position);
    if (index != -1) {
      orderedCards[index] = MapEntry(AxisDirection.left, cardLeft);
    }
    index = cardsOnTable
        .indexWhere((element) => element.position == cardRight?.position);
    if (index != -1) {
      orderedCards[index] = MapEntry(AxisDirection.right, cardRight);
    }
    index = cardsOnTable
        .indexWhere((element) => element.position == cardMe?.position);
    if (index != -1) {
      orderedCards[index] = MapEntry(AxisDirection.down, cardMe);
    }
    index = cardsOnTable
        .indexWhere((element) => element.position == cardTop?.position);
    if (index != -1) {
      orderedCards[index] = MapEntry(AxisDirection.up, cardTop);
    }
  }

  @override
  Widget build(BuildContext context) {
    print("[CardsOnTable] build nb: ${built++}");
    if (state != TableState.PLAYING) {
      return SizedBox();
    }
    return LayoutBuilder(
      builder: (context, constraints) {
        final availableSpace = min(constraints.maxWidth, constraints.maxHeight);
        print("availableSpace: $availableSpace");
        final minCardHeight =
            max(minHeightCard, availableSpace / 2 - minPadding);
        double cardHeight;
        if (maxHeightCard == null) {
          cardHeight = minCardHeight;
        } else {
          cardHeight = min(maxHeightCard, minCardHeight);
        }
        final cardWidth = cardHeight / golden; // Golden ratio / nombre d'or
        print(
            "minHeight = $minHeightCard, minCardHeight: $minCardHeight, maxHeightCard=$maxHeightCard, cardHeight=$cardHeight");
        return Container(
          child: Stack(
            children: [
              for (var c in orderedCards)
                _positionWidget(c?.key, cardWidth, cardHeight),
            ],
          ),
        );
      },
    );
  }

  Widget _positionWidget(
      AxisDirection axisDirection, double cardWidth, double cardHeight) {
    final duration = Duration(seconds: 2);
    switch (axisDirection) {
      case AxisDirection.up:
        return AnimatedOpacity(
          duration: duration,
          opacity: cardTop != null ? 1 : 0,
          child: Align(
            alignment: Alignment.center,
            child: Transform.translate(
              offset: Offset(0, -cardHeight / 2),
              child: NeumorphicNoStateWidget(
                child: CardContent(
                  width: cardWidth,
                  height: cardHeight,
                  card: cardTop.card,
                  displayPlayable: false,
                ),
              ),
            ),
          ),
        );
      case AxisDirection.right:
        return AnimatedOpacity(
          opacity: cardRight != null ? 1 : 0,
          duration: duration,
          child: Align(
            alignment: Alignment.center,
            child: Transform.translate(
              offset: Offset(cardHeight / 2, 0),
              child: NeumorphicNoStateWidget(
                child: RotatedBox(
                  quarterTurns: 1,
                  child: CardContent(
                    width: cardWidth,
                    height: cardHeight,
                    card: cardRight.card,
                    displayPlayable: false,
                  ),
                ),
              ),
            ),
          ),
        );
      case AxisDirection.down:
        return AnimatedOpacity(
          opacity: cardMe != null ? 1 : 0,
          duration: duration,
          child: Align(
            alignment: Alignment.center,
            child: Transform.translate(
              offset: Offset(0, cardHeight / 2),
              child: CardWidget(
                card: cardMe.card,
                displayPlayable: false,
                height: cardHeight,
                width: cardWidth,
              ),
            ),
          ),
        );
      case AxisDirection.left:
        // for left
        return AnimatedOpacity(
          opacity: cardLeft != null ? 1 : 0,
          duration: duration,
          child: Align(
            alignment: Alignment.center,
            child: Transform.translate(
              offset: Offset(-cardHeight / 2, 0),
              child: NeumorphicNoStateWidget(
                child: RotatedBox(
                  quarterTurns: 3,
                  child: CardContent(
                    card: cardLeft.card,
                    displayPlayable: false,
                    height: cardHeight,
                    width: cardWidth,
                  ),
                ),
              ),
            ),
          ),
        );
    }
    return SizedBox();
  }
}
