import 'package:FlutterCoinche/dto/card.dart' as cardModel;
import 'package:FlutterCoinche/resources/colors.dart';
import 'package:FlutterCoinche/widget/card_widget.dart';
import 'package:flutter/material.dart';

class CardsInHandWidget extends StatelessWidget {
  final double cardHeight, cardWidth, screenWidth;

  final List<cardModel.Card> cards;

  CardsInHandWidget(
      {@required this.cardHeight,
      @required this.cards,
      @required this.cardWidth,
      @required this.screenWidth});

  @override
  Widget build(BuildContext context) {
    return Container(
        color: colorLightBlue,
//                  padding: EdgeInsets.all(20),
        height: cardHeight + 20,
        width: screenWidth,
        child: ListView.builder(
            scrollDirection: Axis.horizontal,
            itemCount: cards.length,
            itemBuilder: (context, index) => Padding(
                  padding:
                      const EdgeInsets.symmetric(vertical: 10.0, horizontal: 4),
                  child: Draggable(
//                      hapticFeedbackOnStart: true,
                      data: cards[index],
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
                          child: Container(
                              constraints: BoxConstraints.tight(
                                  Size(cardWidth, cardHeight)),
                              child: CardWidget(cards[index])),
                        ),
                      ),
                      child: Container(
                          constraints:
                              BoxConstraints.tight(Size(cardWidth, cardHeight)),
                          child: CardWidget(cards[index]))),
                )));
  }
}
