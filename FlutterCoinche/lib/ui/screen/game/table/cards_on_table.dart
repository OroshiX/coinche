import 'dart:math';

import 'package:coinche/domain/dto/card.dart';
import 'package:coinche/domain/dto/player_position.dart';
import 'package:coinche/domain/dto/table_state.dart';
import 'package:coinche/domain/extensions/card_won_or_center.dart';
import 'package:coinche/domain/extensions/cards_extension.dart';
import 'package:coinche/domain/logic/calculus.dart';
import 'package:coinche/state/cards_on_table_model.dart';
import 'package:coinche/state/game_model.dart';
import 'package:coinche/theme/dimens.dart';
import 'package:coinche/ui/widget/card_widget.dart';
import 'package:coinche/ui/widget/neumorphic_no_state.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:tuple/tuple.dart';

class CardsOnTable extends StatelessWidget {
  final double minPadding;
  final double? maxHeightCard;
  final double minHeightCard;

  const CardsOnTable(
      {Key? key,
      double? maxHeightCard,
      this.minPadding = 0,
      double? minHeightCard})
      : this.maxHeightCard = maxHeightCard ?? 400,
        this.minHeightCard = minHeightCard ?? 20,
        assert((minHeightCard ?? 20) <= (maxHeightCard ?? 400)),
        super(key: key);
  @override
  Widget build(BuildContext context) {
    return LayoutBuilder(
      builder: (context, constraints) {
        final availableSpace = min(constraints.maxWidth, constraints.maxHeight);
        final minCardHeight =
            max(minHeightCard, availableSpace / 2 - minPadding);
        double cardHeight;
        if (maxHeightCard == null) {
          cardHeight = minCardHeight;
        } else {
          cardHeight = min(maxHeightCard!, minCardHeight);
        }
        final cardWidth = cardHeight / golden; // Golden ratio / nombre d'or
        return Container(
          width: constraints.maxWidth,
          height: constraints.maxHeight,
          child: ChangeNotifierProvider(
            create: (context) => CardsOnTableModel(),
            child: Selector<
                GameModel,
                Tuple5<TableState, PlayerPosition, List<CardPlayed>,
                    List<CardPlayed>, PlayerPosition>>(
              selector: (ctx, gameModel) => Tuple5(
                  gameModel.game.state,
                  gameModel.game.myPosition,
                  gameModel.game.onTable,
                  gameModel.game.lastTrick,
                  gameModel.game.winnerLastTrick),
              builder: (context, statePos, child) {
                if (statePos.item1 != TableState.playing &&
                    statePos.item1 != TableState.betweenGames &&
                    statePos.item1 != TableState.ended) return SizedBox();
                return _CardsOnTable(
                  cardWidth: cardWidth,
                  cardHeight: cardHeight,
                  lastTrick: statePos.item4,
                  myPosition: statePos.item2,
                  onTable: statePos.item3,
                  winnerLastTrick: statePos.item5,
                );
              },
            ),
          ),
        );
      },
    );
  }
}

class _CardsOnTable extends StatefulWidget {
  static int nbBuild = 0;
  static int inFunction = 0;

  final double cardWidth;
  final double cardHeight;
  final PlayerPosition myPosition;
  final List<CardPlayed> onTable;
  final List<CardPlayed>? lastTrick;
  final PlayerPosition? winnerLastTrick;
  _CardsOnTable(
      {required this.cardWidth,
      required this.cardHeight,
      required this.myPosition,
      required this.onTable,
      required this.lastTrick,
      required this.winnerLastTrick});

  @override
  _CardsOnTableState createState() => _CardsOnTableState();
}

class _CardsOnTableState extends State<_CardsOnTable> {
  CardPlayed? cardLeft, cardTop, cardRight, cardMe;
  late Map<AxisDirection, PlayerPosition> _posTableToCardinal;
  List<CardPlayed> currentCardsOnTable = [];
  bool firstRender = true;
  List<MapEntry<AxisDirection, CardPlayed?>> orderedCards =
      List.generate(4, (index) => MapEntry(AxisDirection.values[index], null));

  @override
  void initState() {
    super.initState();
    final me = context.read<GameModel>().game.myPosition;

    /// Init values
    _posTableToCardinal = getPosTableToCardinal(me);
    _initState(
        winnerLast: widget.winnerLastTrick,
        lastTrick: widget.lastTrick,
        cardsOnTable: widget.onTable,
        me: widget.myPosition,
        fromInit: true);
  }

  @override
  void didUpdateWidget(covariant _CardsOnTable oldWidget) {
    super.didUpdateWidget(oldWidget);
    _initState(
      me: widget.myPosition,
      cardsOnTable: widget.onTable,
      lastTrick: widget.lastTrick,
      winnerLast: widget.winnerLastTrick,
    );
    _orderCards();
  }

  void _orderCards() {
    if (_posTableToCardinal[AxisDirection.left] != null) {
      var i = widget.onTable.indexWhere((element) =>
          element.position == _posTableToCardinal[AxisDirection.left]);
      if (i != -1) {
        cardLeft = widget.onTable[i];
      } else {
        cardLeft = null;
      }
    } else {
      cardLeft = null;
    }
    if (_posTableToCardinal[AxisDirection.up] != null) {
      var i = widget.onTable.indexWhere((element) =>
          element.position == _posTableToCardinal[AxisDirection.up]);
      if (i != -1) {
        cardTop = widget.onTable[i];
      } else {
        cardTop = null;
      }
    } else {
      cardTop = null;
    }
    if (_posTableToCardinal[AxisDirection.right] != null) {
      var i = widget.onTable.indexWhere((element) =>
          element.position == _posTableToCardinal[AxisDirection.right]);
      if (i != -1) {
        cardRight = widget.onTable[i];
      } else {
        cardRight = null;
      }
    } else {
      cardRight = null;
    }
    if (_posTableToCardinal[AxisDirection.down] != null) {
      var i = widget.onTable.indexWhere((element) =>
          element.position == _posTableToCardinal[AxisDirection.down]);
      if (i != -1) {
        cardMe = widget.onTable[i];
      } else {
        cardMe = null;
      }
    } else {
      cardMe = null;
    }

    var index = widget.onTable
        .indexWhere((element) => element.position == cardLeft?.position);
    if (index != -1) {
      orderedCards[index] = MapEntry(AxisDirection.left, cardLeft);
    }
    index = widget.onTable
        .indexWhere((element) => element.position == cardRight?.position);
    if (index != -1) {
      orderedCards[index] = MapEntry(AxisDirection.right, cardRight);
    }
    index = widget.onTable
        .indexWhere((element) => element.position == cardMe?.position);
    if (index != -1) {
      orderedCards[index] = MapEntry(AxisDirection.down, cardMe);
    }
    index = widget.onTable
        .indexWhere((element) => element.position == cardTop?.position);
    if (index != -1) {
      orderedCards[index] = MapEntry(AxisDirection.up, cardTop);
    }
  }

  @override
  Widget build(BuildContext context) {
    print("[CardsOnTable] build ${++_CardsOnTable.nbBuild}");

    return Stack(
      children: orderedCards
          .map((e) => _positionWidget(
                e.key,
                widget.cardWidth,
                widget.cardHeight,
                cardLeft: cardLeft,
                cardMe: cardMe,
                cardRight: cardRight,
                cardTop: cardTop,
              ))
          .toList(),
    );
  }

  void _putLastCardOnTable(List<CardPlayed> cardsOnTable,
      Map<PlayerPosition, AxisDirection> cardinalToPosTable, int timestamp,
      {required CardsOnTableModel cardsOnTableModel}) {
    final lastCard = cardsOnTable.last;
    final last = cardinalToPosTable[lastCard.position]!;
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

  void _initState(
      {required List<CardPlayed> cardsOnTable,
      required PlayerPosition me,
      required List<CardPlayed>? lastTrick,
      required PlayerPosition? winnerLast,
      bool fromInit = false}) {
    print("in function ${++_CardsOnTable.inFunction}");
    CardsOnTableModel cardsOnTableModel = context.read<CardsOnTableModel>();
    if (listEquals(currentCardsOnTable, cardsOnTable)) return;
    var timestamp = DateTime.now().millisecondsSinceEpoch;
    final newCardLeft =
        cardsOnTable.atPosition(AxisDirection.left, _posTableToCardinal);
    final newCardTop =
        cardsOnTable.atPosition(AxisDirection.up, _posTableToCardinal);
    final newCardRight =
        cardsOnTable.atPosition(AxisDirection.right, _posTableToCardinal);
    final newCardMe =
        cardsOnTable.atPosition(AxisDirection.down, _posTableToCardinal);
    final tmpOrderedCards = calculateOrderedCards(
        cardsOnTable: cardsOnTable,
        left: newCardLeft,
        right: newCardRight,
        up: newCardTop,
        me: newCardMe);

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
            cardModel: cardLeft?.card,
            position: null,
            timestamp: timestamp,
            shouldAnim: !firstRender);
      }
      if (cardMe != newCardMe) {
        cardMe = newCardMe;
        cardsOnTableModel.down = CardWonOrCenter(
            cardModel: cardMe?.card,
            position: null,
            timestamp: timestamp,
            shouldAnim: !firstRender);
      }
      if (cardRight != newCardRight) {
        cardRight = newCardRight;
        cardsOnTableModel.right = CardWonOrCenter(
            cardModel: cardRight?.card,
            position: null,
            timestamp: timestamp,
            shouldAnim: !firstRender);
      }
      if (cardTop != newCardTop) {
        cardTop = newCardTop;
        cardsOnTableModel.up = CardWonOrCenter(
            cardModel: cardTop?.card,
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
    final cardinalToPosTable = getCardinalToPosTable(me);
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
          cardModel: newCardLeft.card,
          position: null,
          timestamp: timestamp,
          shouldAnim: !firstRender);
    }
    if (cardRight == null && newCardRight != null) {
      cardRight = newCardRight;
      cardsOnTableModel.right = CardWonOrCenter(
          cardModel: newCardRight.card,
          position: null,
          timestamp: timestamp,
          shouldAnim: !firstRender);
    }
    if (cardTop == null && newCardTop != null) {
      cardTop = newCardTop;
      cardsOnTableModel.up = CardWonOrCenter(
          cardModel: newCardTop.card,
          position: null,
          timestamp: timestamp,
          shouldAnim: !firstRender);
    }
    if (cardMe == null && newCardMe != null) {
      cardMe = newCardMe;
      cardsOnTableModel.down = CardWonOrCenter(
          cardModel: newCardMe.card,
          position: null,
          timestamp: timestamp,
          shouldAnim: !firstRender);
    }
    currentCardsOnTable = cardsOnTable;
    firstRender = false;
    if (!fromInit) {
//                 cardsOnTableModel.setState((g) =>
      // TODO idem ordered cards
      setState(() {
        orderedCards = tmpOrderedCards;
      });
      // );
    } else {
      orderedCards = tmpOrderedCards;
    }
  }

  Widget _positionWidget(
    AxisDirection axisDirection,
    double cardWidth,
    double cardHeight, {
    CardPlayed? cardTop,
    CardPlayed? cardRight,
    CardPlayed? cardMe,
    CardPlayed? cardLeft,
  }) {
    final duration = Duration(milliseconds: 200);
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
                  card: cardTop?.card,
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
                    card: cardRight?.card,
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
                card: cardMe?.card,
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
                    card: cardLeft?.card,
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
  }

  List<MapEntry<AxisDirection, CardPlayed?>> calculateOrderedCards(
      {required List<CardPlayed> cardsOnTable,
      required CardPlayed? left,
      required CardPlayed? right,
      required CardPlayed? up,
      required CardPlayed? me}) {
    final res = List<MapEntry<AxisDirection?, CardPlayed?>?>.filled(4, null);
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
    res.map((e) => e?.key).forEach(unusedKeys.remove);
    while (unusedKeys.isNotEmpty) {
      var index = res.indexOf(null);
      if (index == -1) break;
      res[index] = MapEntry(unusedKeys.first, null);
      unusedKeys.removeAt(0);
    }
    var list = res.map((e) => MapEntry(e!.key!, e.value)).toList();
    return list;
  }
}
