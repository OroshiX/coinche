import 'package:coinche/domain/dto/card.dart';
import 'package:coinche/domain/dto/table_state.dart';
import 'package:coinche/state/game_model.dart';
import 'package:coinche/ui/widget/card_widget.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:tuple/tuple.dart';

class CardsInHandWidget extends StatelessWidget {
  final double cardHeight, cardWidth, screenWidth;

  final double paddingVertical;

  const CardsInHandWidget({
    @required this.cardHeight,
    @required this.cardWidth,
    @required this.screenWidth,
    @required this.paddingVertical,
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
              gM.game.state == TableState.PLAYING),
          builder: (context, value, child) {
            final myTurn = value.item1;
            final cards = value.item2;
            final inPlayMode = value.item3;
            return ListView.builder(
                scrollDirection: Axis.horizontal,
                itemCount: cards.length,
                itemBuilder: (context, index) {
                  final card = cards[index];
                  return Padding(
                    padding: EdgeInsets.symmetric(
                        vertical: paddingVertical, horizontal: 4),
                    child: Draggable(
                        maxSimultaneousDrags:
                            inPlayMode && myTurn && card.playable != null
                                ? 1
                                : 0,
                        data: card,
                        childWhenDragging: Container(
                          constraints:
                              BoxConstraints.tight(Size(cardWidth, cardHeight)),
                          decoration: BoxDecoration(
                              color: Colors.transparent,
                              shape: BoxShape.rectangle,
                              borderRadius: BorderRadius.circular(20),
                              boxShadow: [
                                BoxShadow(
                                    color: Colors.grey[800],
                                    offset: Offset(1, 1),
                                    blurRadius: 4,
                                    spreadRadius: 1),
                                BoxShadow(
                                    color: Colors.grey[600],
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
                              card: cards[index],
                              displayPlayable: inPlayMode && myTurn,
                            ),
                          ),
                        ),
                        child: CardWidget(
                          width: cardWidth,
                          height: cardHeight,
                          card: cards[index],
                          displayPlayable: inPlayMode && myTurn,
                        )),
                  );
                });
          },
        ));
  }
}
