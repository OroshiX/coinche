import 'dart:math';

import 'package:FlutterCoinche/domain/dto/card.dart';
import 'package:FlutterCoinche/domain/dto/player_position.dart';
import 'package:FlutterCoinche/domain/dto/table_state.dart';
import 'package:FlutterCoinche/domain/extensions/CardWonOrCenter.dart';
import 'package:FlutterCoinche/domain/extensions/cards_extension.dart';
import 'package:FlutterCoinche/domain/logic/calculus.dart';
import 'package:FlutterCoinche/state/cards_on_table_model.dart';
import 'package:FlutterCoinche/state/game_model.dart';
import 'package:FlutterCoinche/ui/resources/dimens.dart';
import 'package:FlutterCoinche/ui/screen/game/table/managed_state_card.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

class CardsOnTable extends StatefulWidget {
  static int nbBuild = 0;
  static int inFunction = 0;
  final double maxHeightCard, minHeightCard;
  final double minPadding;

  CardsOnTable(
      {this.maxHeightCard = 400, this.minPadding, this.minHeightCard = 20})
      : assert(minHeightCard <= maxHeightCard);

  @override
  _CardsOnTableState createState() => _CardsOnTableState();
}

class _CardsOnTableState extends State<CardsOnTable> {
  CardPlayed cardLeft, cardTop, cardRight, cardMe;
  PlayerPosition myPosition;
  Map<AxisDirection, PlayerPosition> posTableToCardinal;
  List<CardPlayed> currentCardsOnTable = [];
  bool firstRender = true;
  List<MapEntry<AxisDirection, CardPlayed>> orderedCards =
      List.generate(4, (index) => MapEntry(AxisDirection.values[index], null));

  @override
  Widget build(BuildContext context) {
    print("[CardsOnTable] build ${++CardsOnTable.nbBuild}");
    return LayoutBuilder(
      builder: (context, constraints) {
        final availableSpace = min(constraints.maxWidth, constraints.maxHeight);
        final minCardHeight =
            max(widget.minHeightCard, availableSpace / 2 - widget.minPadding);
        double cardHeight;
        if (widget.maxHeightCard == null) {
          cardHeight = minCardHeight;
        } else {
          cardHeight = min(widget.maxHeightCard, minCardHeight);
        }
        final cardWidth = cardHeight / golden; // Golden ratio / nombre d'or
        return Container(
            child: ChangeNotifierProvider(
          create: (context) => CardsOnTableModel(),
          child: Consumer<CardsOnTableModel>(
            builder: (context, cardsOnTableModel, child) {
              return Selector<GameModel, TableState>(
                selector: (ctx, gameModel) => gameModel.game.state,
                builder: (context, state, child) {
                  if (state != TableState.PLAYING &&
                      state != TableState.BETWEEN_GAMES &&
                      state != TableState.ENDED) return SizedBox();

                  return child;
                },
                child: Container(
                  width: constraints.maxWidth,
                  height: constraints.maxHeight,
                  child: Stack(
                    children: orderedCards
                        .map((e) => ManagedStateCard(
                            axisDirection: e.key,
                            cardWidth: cardWidth,
                            cardHeight: cardHeight))
                        .toList(),
                  ),
                ),
              );
            },
          ),
        ));
      },
    );
  }

  void _putLastCardOnTable(List<CardPlayed> cardsOnTable,
      Map<PlayerPosition, AxisDirection> cardinalToPosTable, int timestamp,
      {@required CardsOnTableModel cardsOnTableModel}) {
    final lastCard = cardsOnTable.last;
    final AxisDirection last = cardinalToPosTable[lastCard.position];
    final newValue = CardWonOrCenter(
        cardModel: lastCard.card,
        position: null,
        timestamp: timestamp,
        shouldAnim: !firstRender);
    switch (last) {
      case AxisDirection.up:
        cardsOnTableModel.up = newValue;
        break;
      case AxisDirection.right:
        cardsOnTableModel.right = newValue;
        break;
      case AxisDirection.down:
        cardsOnTableModel.down = newValue;
        break;
      case AxisDirection.left:
        cardsOnTableModel.left = newValue;
        break;
    }
  }

  void _initState({
    GameModel gameModel,
    CardsOnTableModel cardsOnTableModel,
//                    bool fromInit = false
  }) {
    print("in function ${++CardsOnTable.inFunction}");

    /// Init values
    myPosition = gameModel.game.myPosition;
    posTableToCardinal = getPosTableToCardinal(myPosition);

    List<CardPlayed> cardsOnTable = gameModel.game.onTable;
    if (listEquals(currentCardsOnTable, cardsOnTable)) return;
    int timestamp = DateTime.now().millisecondsSinceEpoch;
    final newCardLeft =
        cardsOnTable.atPosition(AxisDirection.left, posTableToCardinal);
    final newCardTop =
        cardsOnTable.atPosition(AxisDirection.up, posTableToCardinal);
    final newCardRight =
        cardsOnTable.atPosition(AxisDirection.right, posTableToCardinal);
    final newCardMe =
        cardsOnTable.atPosition(AxisDirection.down, posTableToCardinal);
    final tmpOrderedCards = calculateOrderedCards(
        cardsOnTable: cardsOnTable,
        left: newCardLeft,
        right: newCardRight,
        up: newCardTop,
        me: newCardMe);

    final lastTrick = gameModel.game.lastTrick;
    final winnerLast = gameModel.game.winnerLastTrick;
    final initial = lastTrick == null ||
        lastTrick.isEmpty ||
        winnerLast == null ||
        firstRender;
    if (initial) {
      // no last trick, this is the 1st (if 1st, but all cards are on table,
      // we should still do something else)
      if (cardLeft != newCardLeft) {
        cardLeft = newCardLeft;
        cardsOnTableModel.left = CardWonOrCenter(
            cardModel: cardLeft.card,
            position: null,
            timestamp: timestamp,
            shouldAnim: !firstRender);
      }
      if (cardMe != newCardMe) {
        cardMe = newCardMe;
        cardsOnTableModel.down = CardWonOrCenter(
            cardModel: cardMe.card,
            position: null,
            timestamp: timestamp,
            shouldAnim: !firstRender);
      }
      if (cardRight != newCardRight) {
        cardRight = newCardRight;
        cardsOnTableModel.right = CardWonOrCenter(
            cardModel: cardRight.card,
            position: null,
            timestamp: timestamp,
            shouldAnim: !firstRender);
      }
      if (cardTop != newCardTop) {
        cardTop = newCardTop;
        cardsOnTableModel.up = CardWonOrCenter(
            cardModel: cardTop.card,
            position: null,
            timestamp: timestamp,
            shouldAnim: !firstRender);
      }
      if (cardsOnTable.length != 4) {
        currentCardsOnTable = cardsOnTable;
        firstRender = false;
        // TODO change ordered cards with tmpOrderedCards
        orderedCards = tmpOrderedCards;
//                    }
        return;
      }
    }
    final cardinalToPosTable = getCardinalToPosTable(myPosition);
    final posWinner = cardinalToPosTable[winnerLast];
    if (cardsOnTable.length == 4) {
      // First put the last card on the table
      if (!initial) {
        _putLastCardOnTable(cardsOnTable, cardinalToPosTable, timestamp,
            cardsOnTableModel: cardsOnTableModel);
      }
      // Update the data
      cardMe = cardRight = cardTop = cardLeft = null;

      // Then animate to win the trick
      Future.delayed(Duration(milliseconds: initial ? 500 : 2000), () {
        final tuple = CardWonOrCenter(
            position: posWinner, cardModel: null, timestamp: timestamp);
        cardsOnTableModel.left = tuple;
        cardsOnTableModel.right = tuple;
        cardsOnTableModel.up = tuple;
        cardsOnTableModel.down = tuple;
      });
      currentCardsOnTable = cardsOnTable;
      firstRender = false;
      return;
    }
    if (cardLeft == null && newCardLeft != null) {
      cardLeft = newCardLeft;
      cardsOnTableModel.left = CardWonOrCenter(
          cardModel: cardLeft.card,
          position: null,
          timestamp: timestamp,
          shouldAnim: !firstRender);
    }
    if (cardRight == null && newCardRight != null) {
      cardRight = newCardRight;
      cardsOnTableModel.right = CardWonOrCenter(
          cardModel: cardRight.card,
          position: null,
          timestamp: timestamp,
          shouldAnim: !firstRender);
    }
    if (cardTop == null && newCardTop != null) {
      cardTop = newCardTop;
      cardsOnTableModel.up = CardWonOrCenter(
          cardModel: cardTop.card,
          position: null,
          timestamp: timestamp,
          shouldAnim: !firstRender);
    }
    if (cardMe == null && newCardMe != null) {
      cardMe = newCardMe;
      cardsOnTableModel.down = CardWonOrCenter(
          cardModel: cardMe.card,
          position: null,
          timestamp: timestamp,
          shouldAnim: !firstRender);
    }
    currentCardsOnTable = cardsOnTable;
    firstRender = false;
//                if (!fromInit) {
//                 cardsOnTableModel.setState((g) =>
    // TODO idem ordered cards
    orderedCards = tmpOrderedCards;
    // );
//                }
  }

  List<MapEntry<AxisDirection, CardPlayed>> calculateOrderedCards(
      {@required List<CardPlayed> cardsOnTable,
      @required CardPlayed left,
      @required CardPlayed right,
      @required CardPlayed up,
      @required CardPlayed me}) {
    final res = List<MapEntry<AxisDirection, CardPlayed>>(4);
    var index = cardsOnTable
        .indexWhere((element) => element.position == left?.position);
    if (index != -1) {
      res[index] = MapEntry(AxisDirection.left, left);
    }
    index = cardsOnTable
        .indexWhere((element) => element.position == right?.position);
    if (index != -1) {
      res[index] = MapEntry(AxisDirection.right, right);
    }
    index =
        cardsOnTable.indexWhere((element) => element.position == me?.position);
    if (index != -1) {
      res[index] = MapEntry(AxisDirection.down, me);
    }
    index =
        cardsOnTable.indexWhere((element) => element.position == up?.position);
    if (index != -1) {
      res[index] = MapEntry(AxisDirection.up, up);
    }
    // Fill empty indexes with missing values
    final unusedKeys = AxisDirection.values.toList();
    res.map((e) => e?.key).forEach((element) => unusedKeys.remove(element));
    while (unusedKeys.isNotEmpty) {
      int index = res.indexOf(null);
      if (index == -1) break;
      res[index] = MapEntry(unusedKeys.first, null);
      unusedKeys.removeAt(0);
    }
    return res;
  }
}
