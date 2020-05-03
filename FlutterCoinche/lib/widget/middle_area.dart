import 'package:FlutterCoinche/bloc/games_bloc.dart';
import 'package:FlutterCoinche/dto/card.dart';
import 'package:FlutterCoinche/dto/player_position.dart';
import 'package:FlutterCoinche/resources/dimens.dart';
import 'package:FlutterCoinche/rest/server_communication.dart';
import 'package:FlutterCoinche/widget/card_widget.dart';
import 'package:FlutterCoinche/widget/cards_on_table.dart';
import 'package:FlutterCoinche/widget/game_inherited.dart';
import 'package:bloc_provider/bloc_provider.dart';
import 'package:flushbar/flushbar_helper.dart';
import 'package:flutter/material.dart';

class MiddleArea extends StatelessWidget {
  final double cardWidth, cardHeight;
  final Size screenSize;
  final PlayerPosition left, right, top;

  const MiddleArea({
    @required this.cardWidth,
    @required this.cardHeight,
    @required this.screenSize,
    @required this.left,
    @required this.right,
    @required this.top,
  });

  @override
  Widget build(BuildContext context) {
    final state =
        GameInherited.of(context, aspectType: Aspects.STATE).game.state;
    final id = GameInherited.of(context, aspectType: Aspects.ID).game.id;
    final myPosition =
        GameInherited.of(context, aspectType: Aspects.MY_POSITION)
            .game
            .myPosition;
    final onTable =
        GameInherited.of(context, aspectType: Aspects.ON_TABLE).game.onTable;
    final nextPlayer =
        GameInherited.of(context, aspectType: Aspects.NEXT_PLAYER)
            .game
            .nextPlayer;
    return Stack(
      children: [
        CardsOnTable(
          state: state,
          cardsOnTable: onTable,
          posTableToCardinal: {
            AxisDirection.left: left,
            AxisDirection.up: top,
            AxisDirection.right: right,
            AxisDirection.down: myPosition
          },
          minPadding: 2,
        ),
        Center(
          child: DragTarget<CardModel>(
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
          ),
        ),
      ],
    );
  }
}
