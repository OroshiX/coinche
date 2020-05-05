import 'package:FlutterCoinche/dto/table_state.dart';
import 'package:FlutterCoinche/widget/card_widget.dart';
import 'package:FlutterCoinche/widget/game_inherited.dart';
import 'package:flutter/material.dart';

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
    final cards =
        GameInherited.of(context, aspectType: Aspects.CARDS).game.cards;
    final me = GameInherited.of(context, aspectType: Aspects.MY_POSITION)
        .game
        .myPosition;
    final nextPlayer =
        GameInherited.of(context, aspectType: Aspects.NEXT_PLAYER)
            .game
            .nextPlayer;
    final myTurn = me == nextPlayer;
    final inPlayMode =
        GameInherited.of(context, aspectType: Aspects.STATE).game.state ==
            TableState.PLAYING;
    return Container(
//        color: colorLightBlue,
//                  padding: EdgeInsets.all(20),
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
                        inPlayMode && myTurn && card.playable != null ? 1 : 0,
//                      hapticFeedbackOnStart: true,
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
//                          color: Colors.white,
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
  }
}
