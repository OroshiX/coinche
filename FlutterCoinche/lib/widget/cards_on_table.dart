import 'dart:math';

import 'package:FlutterCoinche/dto/card.dart';
import 'package:FlutterCoinche/dto/player_position.dart';
import 'package:FlutterCoinche/dto/table_state.dart';
import 'package:FlutterCoinche/widget/card_widget.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';

class CardsOnTable extends StatelessWidget {
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
      this.maxHeightCard = 300,
      this.minPadding,
      @required this.state,
      this.minHeightCard = 20})
      : cardLeft = posTableToCardinal[AxisDirection.left] != null
            ? cardsOnTable.firstWhere((element) =>
                element.position == posTableToCardinal[AxisDirection.left])
            : null,
        cardTop = posTableToCardinal[AxisDirection.up] != null
            ? cardsOnTable.firstWhere((element) =>
                element.position == posTableToCardinal[AxisDirection.up])
            : null,
        cardRight = posTableToCardinal[AxisDirection.right] != null
            ? cardsOnTable.firstWhere((element) =>
                element.position == posTableToCardinal[AxisDirection.right])
            : null,
        cardMe = posTableToCardinal[AxisDirection.down] != null
            ? cardsOnTable.firstWhere((element) =>
                element.position == posTableToCardinal[AxisDirection.down])
            : null,
        orderedCards = List(4) {
    orderedCards[cardsOnTable
            .indexWhere((element) => element.position == cardLeft.position)] =
        MapEntry(AxisDirection.left, cardLeft);
    orderedCards[cardsOnTable
            .indexWhere((element) => element.position == cardRight.position)] =
        MapEntry(AxisDirection.right, cardRight);
    orderedCards[cardsOnTable
            .indexWhere((element) => element.position == cardMe.position)] =
        MapEntry(AxisDirection.down, cardMe);
    orderedCards[cardsOnTable
            .indexWhere((element) => element.position == cardTop.position)] =
        MapEntry(AxisDirection.up, cardTop);
  }

  @override
  Widget build(BuildContext context) {
    // TODO UNCOMMENT
//    if (state != TableState.PLAYING) {
//      return SizedBox();
//    }
    final availableSpace = min(
        MediaQuery.of(context).size.width, MediaQuery.of(context).size.height);
    final minCardHeight = max(minHeightCard, availableSpace / 2 - minPadding);
    final cardHeight = min(maxHeightCard, minCardHeight);
    final cardWidth = cardHeight * 2 / 3;
    return Container(
      child: Stack(
        children: [
          for (var c in orderedCards)
            _positionWidget(c.key, cardWidth, cardHeight),
        ],
      ),
    );
  }

  Widget _positionWidget(
      AxisDirection axisDirection, double cardWidth, double cardHeight) {
    final duration = Duration(seconds: 2);
    switch (axisDirection) {
      case AxisDirection.up:
        return Align(
          alignment: Alignment.center,
          child: Transform.translate(
            offset: Offset(0, -cardHeight / 2),
            child: AnimatedOpacity(
              opacity: cardTop != null ? 1 : 0,
              duration: duration,
              child: Container(
                width: cardWidth,
                height: cardHeight,
                child: CardWidget(
                  card: cardTop.card,
                  displayPlayable: false,
                ),
              ),
            ),
          ),
        );
      case AxisDirection.right:
        return Align(
          alignment: Alignment.center,
          child: Transform.translate(
            offset: Offset(cardHeight / 2, 0),
            child: Transform.rotate(
              angle: pi / 2,
              child: AnimatedOpacity(
                opacity: cardRight != null ? 1 : 0,
                duration: duration,
                child: Container(
                    width: cardWidth,
                    height: cardHeight,
                    child: CardWidget(
                      card: cardRight.card,
                      displayPlayable: false,
                    )),
              ),
            ),
          ),
        );
      case AxisDirection.down:
        return Align(
          alignment: Alignment.center,
          child: Transform.translate(
            offset: Offset(0, cardHeight / 2),
            child: AnimatedOpacity(
              opacity: cardMe != null ? 1 : 0,
              duration: duration,
              child: Container(
                width: cardWidth,
                height: cardHeight,
                child: CardWidget(
                  card: cardMe.card,
                  displayPlayable: false,
                ),
              ),
            ),
          ),
        );
      case AxisDirection.left:
        // for left
        return Align(
          alignment: Alignment.center,
          child: Transform.translate(
            offset: Offset(-cardWidth / 2, 0),
            child: Transform.rotate(
              angle: -pi / 2,
              child: AnimatedOpacity(
                opacity: cardLeft != null ? 1 : 0,
                duration: duration,
                child: Container(
                    width: cardWidth,
                    height: cardHeight,
                    child: CardWidget(
                      card: cardLeft.card,
                      displayPlayable: false,
                    )),
              ),
            ),
          ),
        );
    }
    return SizedBox();
  }
}
