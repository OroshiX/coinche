import 'dart:math';

import 'package:FlutterCoinche/domain/dto/card.dart';
import 'package:FlutterCoinche/domain/dto/game.dart';
import 'package:FlutterCoinche/domain/dto/player_position.dart';
import 'package:FlutterCoinche/domain/dto/table_state.dart';
import 'package:FlutterCoinche/domain/extensions/CardWonOrCenter.dart';
import 'package:FlutterCoinche/domain/extensions/cards_extension.dart';
import 'package:FlutterCoinche/domain/extensions/game_extensions.dart';
import 'package:FlutterCoinche/domain/logic/calculus.dart';
import 'package:FlutterCoinche/ui/resources/dimens.dart';
import 'package:FlutterCoinche/ui/screen/game/managed_state_card.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:states_rebuilder/states_rebuilder.dart';

class CardsOnTable extends StatelessWidget {
  static int nbBuild = 0;
  static int inFunction = 0;
  final double maxHeightCard, minHeightCard;
  final double minPadding;

  CardsOnTable(
      {this.maxHeightCard = 400, this.minPadding, this.minHeightCard = 20})
      : assert(minHeightCard <= maxHeightCard);

  @override
  Widget build(BuildContext context) {
    print("[CardsOnTable] build ${++nbBuild}");
    return LayoutBuilder(
      builder: (context, constraints) {
        final availableSpace = min(constraints.maxWidth, constraints.maxHeight);
        final minCardHeight =
            max(minHeightCard, availableSpace / 2 - minPadding);
        double cardHeight;
        if (maxHeightCard == null) {
          cardHeight = minCardHeight;
        } else {
          cardHeight = min(maxHeightCard, minCardHeight);
        }
        final cardWidth = cardHeight / golden; // Golden ratio / nombre d'or
        CardPlayed cardLeft, cardTop, cardRight, cardMe;
        List<MapEntry<AxisDirection, CardPlayed>> orderedCards = List.generate(
            4, (index) => MapEntry(AxisDirection.values[index], null));
        return Container(
          child: Injector(
            inject: [
              Inject(() => CardWonOrCenter(cardModel: null, position: null),
                  name: AxisDirection.left.simpleName()),
              Inject(() => CardWonOrCenter(cardModel: null, position: null),
                  name: AxisDirection.right.simpleName()),
              Inject(() => CardWonOrCenter(cardModel: null, position: null),
                  name: AxisDirection.up.simpleName()),
              Inject(() => CardWonOrCenter(position: null, cardModel: null),
                  name: AxisDirection.down.simpleName()),
            ],
            builder: (ctx) {
              final rmLeftCard = RM.get<CardWonOrCenter>(
                  name: AxisDirection.left.simpleName());
              final rmRightCard = RM.get<CardWonOrCenter>(
                  name: AxisDirection.right.simpleName());
              final rmUpCard =
                  RM.get<CardWonOrCenter>(name: AxisDirection.up.simpleName());
              final rmMeCard = RM.get<CardWonOrCenter>(
                  name: AxisDirection.down.simpleName());
              PlayerPosition myPosition;
              Map<AxisDirection, PlayerPosition> posTableToCardinal;
              List<CardPlayed> currentCardsOnTable = [];
              bool firstRender = true;

              void _putLastCardOnTable(
                  List<CardPlayed> cardsOnTable,
                  Map<PlayerPosition, AxisDirection> cardinalToPosTable,
                  int timestamp) {
                final lastCard = cardsOnTable.last;
                final AxisDirection last =
                    cardinalToPosTable[lastCard.position];
                final newValue = CardWonOrCenter(
                    cardModel: lastCard.card,
                    position: null,
                    timestamp: timestamp,
                    shouldAnim: !firstRender);
                switch (last) {
                  case AxisDirection.up:
                    rmUpCard.setValue(() => newValue);
                    break;
                  case AxisDirection.right:
                    rmRightCard.setValue(() => newValue);
                    break;
                  case AxisDirection.down:
                    rmMeCard.setValue(() => newValue);
                    break;
                  case AxisDirection.left:
                    rmLeftCard.setValue(() => newValue);
                    break;
                }
              }

              void _initState({
                ReactiveModel<Game> model,
//                    bool fromInit = false
              }) {
                print("in function ${++inFunction}");

                /// Init values
                myPosition = model.state.myPosition;
                posTableToCardinal = getPosTableToCardinal(myPosition);

                List<CardPlayed> cardsOnTable = model.state.onTable;
                if (listEquals(currentCardsOnTable, cardsOnTable)) return;
                int timestamp = DateTime.now().millisecondsSinceEpoch;
                final newCardLeft = cardsOnTable.atPosition(
                    AxisDirection.left, posTableToCardinal);
                final newCardTop = cardsOnTable.atPosition(
                    AxisDirection.up, posTableToCardinal);
                final newCardRight = cardsOnTable.atPosition(
                    AxisDirection.right, posTableToCardinal);
                final newCardMe = cardsOnTable.atPosition(
                    AxisDirection.down, posTableToCardinal);
                final tmpOrderedCards = calculateOrderedCards(
                    cardsOnTable: cardsOnTable,
                    left: newCardLeft,
                    right: newCardRight,
                    up: newCardTop,
                    me: newCardMe);

                final lastTrick = model.state.lastTrick;
                final winnerLast = model.state.winnerLastTrick;
                final initial = lastTrick == null ||
                    lastTrick.isEmpty ||
                    winnerLast == null ||
                    firstRender;
                if (initial) {
                  // no last trick, this is the 1st (if 1st, but all cards are on table,
                  // we should still do something else)
                  if (cardLeft != newCardLeft) {
                    cardLeft = newCardLeft;
                    rmLeftCard.setValue(() => CardWonOrCenter(
                        cardModel: cardLeft.card,
                        position: null,
                        timestamp: timestamp,
                        shouldAnim: !firstRender));
                  }
                  if (cardMe != newCardMe) {
                    cardMe = newCardMe;
                    rmMeCard.setValue(() => CardWonOrCenter(
                        cardModel: cardMe.card,
                        position: null,
                        timestamp: timestamp,
                        shouldAnim: !firstRender));
                  }
                  if (cardRight != newCardRight) {
                    cardRight = newCardRight;
                    rmRightCard.setValue(() => CardWonOrCenter(
                        cardModel: cardRight.card,
                        position: null,
                        timestamp: timestamp,
                        shouldAnim: !firstRender));
                  }
                  if (cardTop != newCardTop) {
                    cardTop = newCardTop;
                    rmUpCard.setValue(() => CardWonOrCenter(
                        cardModel: cardTop.card,
                        position: null,
                        timestamp: timestamp,
                        shouldAnim: !firstRender));
                  }
                  if (cardsOnTable.length != 4) {
                    currentCardsOnTable = cardsOnTable;
                    firstRender = false;
//                    if (!fromInit) {
                    model.setState((g) => orderedCards = tmpOrderedCards);
//                    }
                    return;
                  }
                }
                final cardinalToPosTable = getCardinalToPosTable(myPosition);
                final posWinner = cardinalToPosTable[winnerLast];
                if (cardsOnTable.length == 4) {
                  // First put the last card on the table
                  if (!initial) {
                    _putLastCardOnTable(
                        cardsOnTable, cardinalToPosTable, timestamp);
                  }
                  // Update the data
                  cardMe = cardRight = cardTop = cardLeft = null;

                  // Then animate to win the trick
                  Future.delayed(Duration(milliseconds: initial ? 500 : 2000),
                      () {
                    final tuple = CardWonOrCenter(
                        position: posWinner,
                        cardModel: null,
                        timestamp: timestamp);
                    rmLeftCard.setValue(() => tuple);
                    rmUpCard.setValue(() => tuple);
                    rmRightCard.setValue(() => tuple);
                    rmMeCard.setValue(() => tuple);
                  });
                  currentCardsOnTable = cardsOnTable;
                  firstRender = false;
                  return;
                }
                if (cardLeft == null && newCardLeft != null) {
                  cardLeft = newCardLeft;
                  rmLeftCard.setValue(() => CardWonOrCenter(
                      cardModel: cardLeft.card,
                      position: null,
                      timestamp: timestamp,
                      shouldAnim: !firstRender));
                }
                if (cardRight == null && newCardRight != null) {
                  cardRight = newCardRight;
                  rmRightCard.setValue(() => CardWonOrCenter(
                      cardModel: cardRight.card,
                      position: null,
                      timestamp: timestamp,
                      shouldAnim: !firstRender));
                }
                if (cardTop == null && newCardTop != null) {
                  cardTop = newCardTop;
                  rmUpCard.setValue(() => CardWonOrCenter(
                      cardModel: cardTop.card,
                      position: null,
                      timestamp: timestamp,
                      shouldAnim: !firstRender));
                }
                if (cardMe == null && newCardMe != null) {
                  cardMe = newCardMe;
                  rmMeCard.setValue(() => CardWonOrCenter(
                      cardModel: cardMe.card,
                      position: null,
                      timestamp: timestamp,
                      shouldAnim: !firstRender));
                }
                currentCardsOnTable = cardsOnTable;
                firstRender = false;
//                if (!fromInit) {
                model.setState((g) => orderedCards = tmpOrderedCards);
//                }
              }

              return StateBuilder<Game>(
                models: [RM.get<Game>()],
                tag: [
                  Aspects.ON_TABLE,
                  Aspects.STATE,
                  Aspects.LAST_TRICK,
                  Aspects.MY_POSITION
                ],
                initState: (context, model) {
                  _initState(model: model);
                },
                onSetState: (context, model) {
                  _initState(model: model);
                },
                didChangeDependencies: (context, model) {
                  _initState(model: model);
                },
                builderWithChild: (context, model, child) {
                  if (model.state.state != TableState.PLAYING &&
                      model.state.state != TableState.BETWEEN_GAMES &&
                      model.state.state != TableState.ENDED) return SizedBox();

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
        );
      },
    );
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
