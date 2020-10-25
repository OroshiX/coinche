import 'dart:math';

import 'package:coinche/domain/dto/card.dart';
import 'package:coinche/domain/dto/player_position.dart';
import 'package:coinche/state/game_model.dart';
import 'package:coinche/ui/resources/dimens.dart';
import 'package:coinche/ui/widget/card_widget.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:tuple/tuple.dart';

class DragArea extends StatelessWidget {
  final double maxHeightCard, minHeightCard;
  final double minPadding;

  final Size screenSize;

  const DragArea({
    Key key,
    @required this.screenSize,
    this.maxHeightCard = 400,
    this.minHeightCard = 20,
    @required this.minPadding,
  }) : super(key: key);

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
          cardHeight = min(maxHeightCard, minCardHeight);
        }
        final cardWidth = cardHeight / golden; // Golden ratio / nombre d'or

        return Selector<GameModel, Tuple2<PlayerPosition, PlayerPosition>>(
          selector: (context, gm) =>
              Tuple2(gm.game.myPosition, gm.game.nextPlayer),
          builder: (context, value, child) => DragTarget<CardModel>(
            onWillAccept: (data) {
              // if it is my turn and the card is playable
              return value.item1 == value.item2 &&
                  data.playable != null &&
                  data.playable;
            },
            onAccept: (data) {
              context.read<GameModel>().playCard(data);
            },
            builder: (context, candidateData, rejectedData) {
              if (candidateData.isNotEmpty) {
                return SizedBox.expand(
                  child: Center(
                    child: Transform.translate(
                      offset: Offset(0, cardHeight * 2 / 2),
                      child: Container(
                        width: cardWidth,
                        height: cardHeight,
                        child: CardWidget(
                          displayPlayable: false,
                          card: candidateData.last,
                          height: cardHeight,
                          width: cardWidth,
                        ),
                      ),
                    ),
                  ),
                );
              }
              if (rejectedData.isNotEmpty) {
                return SizedBox.expand(
                  child: Center(
                    child: Transform.translate(
                      offset: Offset(0, cardHeight * 2 / 2),
                      child: Container(
                        decoration: BoxDecoration(
                          color: Colors.red[900],
                          borderRadius: BorderRadius.circular(
                              isLargeScreen(screenSize) ? 20 : 10),
                        ),
                        width: cardWidth,
                        height: cardHeight,
                        child: Center(
                          child: Icon(
                            Icons.do_not_disturb_alt,
                            color: Colors.white,
                            size: cardWidth * 9 / 10,
                          ),
                        ),
                      ),
                    ),
                  ),
                );
              }
              return SizedBox.expand(child: Container());
            },
          ),
        );
      },
    );
  }
}
