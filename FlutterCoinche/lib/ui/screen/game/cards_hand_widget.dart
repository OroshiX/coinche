import 'package:FlutterCoinche/domain/dto/game.dart';
import 'package:FlutterCoinche/domain/dto/table_state.dart';
import 'package:FlutterCoinche/domain/extensions/game_extensions.dart';
import 'package:FlutterCoinche/ui/widget/card_widget.dart';
import 'package:flutter/material.dart';
import 'package:states_rebuilder/states_rebuilder.dart';

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
    return StateBuilder<Game>(
      models: [RM.get<Game>()],
      tag: [
        Aspects.CARDS,
        Aspects.MY_POSITION,
        Aspects.NEXT_PLAYER,
        Aspects.STATE
      ],
      builder: (context, model) {
        final cards = model.state.cards;
        final me = model.state.myPosition;
        final nextPlayer = model.state.nextPlayer;
        final myTurn = me == nextPlayer;
        final inPlayMode = model.state.state == TableState.PLAYING;
        return Container(
            height: cardHeight + paddingVertical * 2,
            width: screenWidth,
            child: ListView.builder(
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
                }));
      },
    );
  }
}
