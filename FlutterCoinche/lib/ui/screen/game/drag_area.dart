import 'dart:math';

import 'package:FlutterCoinche/domain/dto/card.dart';
import 'package:FlutterCoinche/domain/dto/game.dart';
import 'package:FlutterCoinche/domain/extensions/game_extensions.dart';
import 'package:FlutterCoinche/service/network/server_communication.dart';
import 'package:FlutterCoinche/state/games_bloc.dart';
import 'package:FlutterCoinche/ui/resources/dimens.dart';
import 'package:FlutterCoinche/ui/widget/card_widget.dart';
import 'package:bloc_provider/bloc_provider.dart';
import 'package:flushbar/flushbar_helper.dart';
import 'package:flutter/material.dart';
import 'package:states_rebuilder/states_rebuilder.dart';

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
        return StateBuilder<Game>(
          models: [RM.get<Game>()],
          tag: [Aspects.ID, Aspects.MY_POSITION, Aspects.NEXT_PLAYER],
          builder: (context, model) {
            final id = model.state.id;
            final myPosition = model.state.myPosition;
            final nextPlayer = model.state.nextPlayer;

            return DragTarget<CardModel>(
              onWillAccept: (data) {
                // if it is my turn and the card is playable
                return myPosition == nextPlayer &&
                    data.playable != null &&
                    data.playable;
              },
              onAccept: (data) {
                return ServerCommunication.playCard(data, id).then((void _) {},
                    onError: (error) {
                  BlocProvider.of<GamesBloc>(context).playError();
                  return FlushbarHelper.createError(
                          message: "Error: ${error["message"]}",
                          duration: Duration(seconds: 5))
                      .show(context);
                });
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
            );
          },
        );
      },
    );
  }
}
