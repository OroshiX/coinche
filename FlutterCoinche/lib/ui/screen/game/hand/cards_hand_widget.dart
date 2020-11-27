import 'package:coinche/domain/dto/card.dart';
import 'package:coinche/domain/dto/table_state.dart';
import 'package:coinche/state/game_model.dart';
import 'package:coinche/ui/widget/card_widget.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:tuple/tuple.dart';
import 'package:coinche/util/list_util.dart';

class CardsInHandWidget extends StatelessWidget {
  final double cardHeight, cardWidth, screenWidth;

  final double paddingVertical;
  final double overlapCardsFactor;

  const CardsInHandWidget({
    required this.cardHeight,
    required this.cardWidth,
    required this.screenWidth,
    required this.paddingVertical,
    this.overlapCardsFactor = 1 / 3,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
        height: cardHeight + paddingVertical * 2,
        width: screenWidth,
        child: Selector<GameModel, Tuple3<bool, List<CardModel>, bool>>(
          // myTurn, cards, inPlayMode
          selector: (ctx, gM) => Tuple3(
              gM.game.myPosition == gM.game.nextPlayer,
              gM.game.cards,
              gM.game.state == TableState.playing),
          builder: (context, value, child) {
            final myTurn = value.item1;
            final cards = value.item2;
            final inPlayMode = value.item3;
            // calculate unitOffset and global offset
            final unitOffset = (1 - overlapCardsFactor) * cardWidth;
            final globalOffset = 0.5 * screenWidth -
                (1 + (1 - overlapCardsFactor) * (cards.length - 1)) *
                    cardWidth *
                    0.5;
            return Stack(
              children: [
                for (var card in cards.indexedValues())
                  Positioned(
                    left: card.i * unitOffset + globalOffset,
                    child: Padding(
                      padding: EdgeInsets.symmetric(
                          vertical: paddingVertical, horizontal: 4),
                      child: Draggable<CardModel>(
                          maxSimultaneousDrags: inPlayMode &&
                                  myTurn &&
                                  card.value.playable != null
                              ? 1
                              : 0,
                          data: card.value,
                          childWhenDragging: Container(
                            constraints: BoxConstraints.tight(
                                Size(cardWidth, cardHeight)),
                            decoration: BoxDecoration(
                                color: Colors.transparent,
                                shape: BoxShape.rectangle,
                                borderRadius: BorderRadius.circular(20),
                                boxShadow: [
                                  BoxShadow(
                                      color: Colors.grey[800]!,
                                      offset: Offset(1, 1),
                                      blurRadius: 4,
                                      spreadRadius: 1),
                                  BoxShadow(
                                      color: Colors.grey[600]!,
                                      offset: Offset(-3, -3),
                                      blurRadius: 0,
                                      spreadRadius: 2)
                                ]),
                          ),
                          feedback: Container(
                            decoration: BoxDecoration(
                              borderRadius: BorderRadius.circular(20),
                              boxShadow: [
                                BoxShadow(
                                  color: Colors.white,
                                  spreadRadius: 2,
                                  blurRadius: 2,
                                )
                              ],
                            ),
                            child: Padding(
                              padding: const EdgeInsets.all(5.0),
                              child: CardWidget(
                                height: cardHeight,
                                width: cardWidth,
                                card: card.value,
                                displayPlayable: inPlayMode && myTurn,
                              ),
                            ),
                          ),
                          child: CardWidget(
                            width: cardWidth,
                            height: cardHeight,
                            card: card.value,
                            displayPlayable: inPlayMode && myTurn,
                          )),
                    ),
                  ),
              ],
            );
          },
        ));
  }
}
