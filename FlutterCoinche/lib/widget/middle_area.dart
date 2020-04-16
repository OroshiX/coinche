import 'package:FlutterCoinche/bloc/games_bloc.dart';
import 'package:FlutterCoinche/dto/card.dart' as cardModel;
import 'package:FlutterCoinche/dto/game.dart';
import 'package:FlutterCoinche/dto/player_position.dart';
import 'package:FlutterCoinche/resources/dimens.dart';
import 'package:FlutterCoinche/rest/server_communication.dart';
import 'package:FlutterCoinche/widget/card_widget.dart';
import 'package:FlutterCoinche/widget/cards_on_table.dart';
import 'package:bloc_provider/bloc_provider.dart';
import 'package:flushbar/flushbar_helper.dart';
import 'package:flutter/material.dart';

class MiddleArea extends StatelessWidget {
  final double cardWidth, cardHeight;
  final Size screenSize;
  final Game game;
  final PlayerPosition left, right, top, me;

  MiddleArea({
    @required this.cardWidth,
    @required this.cardHeight,
    @required this.game,
    @required this.screenSize,
    @required this.left,
    @required this.right,
    @required this.top,
    @required this.me,
  });

  @override
  Widget build(BuildContext context) {
    return Stack(
      children: [
        CardsOnTable(
          state: game.state,
          cardsOnTable: game.onTable,
          posTableToCardinal: {
            AxisDirection.left: left,
            AxisDirection.up: top,
            AxisDirection.right: right,
            AxisDirection.down: me
          },
          minPadding: 2,
        ),
        Center(
          child: DragTarget<cardModel.Card>(
            onWillAccept: (data) {
              // if it is my turn and the card is playable
              return game.myPosition == game.nextPlayer &&
                  data.playable != null &&
                  data.playable;
            },
            onAccept: (data) {
              return ServerCommunication.playCard(data, game.id)
                  .then((void _) {}, onError: (error) {
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
          ),
        ),
      ],
    );
  }
}
