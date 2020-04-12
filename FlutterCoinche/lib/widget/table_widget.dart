import 'dart:math';

import 'package:FlutterCoinche/business/calculus.dart';
import 'package:FlutterCoinche/dto/bid.dart';
import 'package:FlutterCoinche/dto/card.dart' as cardModel;
import 'package:FlutterCoinche/dto/game.dart';
import 'package:FlutterCoinche/rest/server_communication.dart';
import 'package:FlutterCoinche/widget/bidding_bar.dart';
import 'package:FlutterCoinche/widget/card_widget.dart';
import 'package:FlutterCoinche/widget/cards_hand_widget.dart';
import 'package:FlutterCoinche/widget/recap_widget.dart';
import 'package:flushbar/flushbar_helper.dart';
import 'package:flutter/material.dart';

class TableWidget extends StatelessWidget {
  final Game game;

  TableWidget(this.game);

  @override
  Widget build(BuildContext context) {
    var screenSize = MediaQuery.of(context).size;
    double heightContainerName = 50;
    double widthContainer = 200;
    var top = getPlayerPositionTop(game);
    var left = getPlayerPositionLeft(game);
    var right = getPlayerPositionRight(game);
    var me = game.myPosition;
    final double cardWidth = 100;
    final double cardHeight = 150;
    final double marginCardsPosition = 20;

    return SafeArea(
        child: Container(
      child: Stack(
        children: <Widget>[
          Positioned(
              top: 0,
              child: Transform.translate(
                offset: Offset(screenSize.width / 2 - widthContainer / 2, 0),
                child: Container(
                  width: widthContainer,
                  height: heightContainerName,
                  child: DecoratedBox(
                    decoration: BoxDecoration(color: Colors.deepPurpleAccent),
                    child: Column(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: <Widget>[
                        Text(game.getNickNameOf(top)),
                        Text("($top)"),
                      ],
                    ),
                  ),
                ),
              )),
          Transform.translate(
            offset: Offset(screenSize.width - heightContainerName,
                screenSize.height / 2 - widthContainer / 4),
            child: Transform.rotate(
              origin: Offset(-widthContainer / 2, -heightContainerName / 2),
              angle: -pi / 2,
              child: Container(
                width: widthContainer,
                height: heightContainerName,
                child: DecoratedBox(
                  decoration: BoxDecoration(color: Colors.red),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.center,
                    mainAxisSize: MainAxisSize.min,
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: <Widget>[
                      Text(game.getNickNameOf(right)),
                      Text("($right)"),
                    ],
                  ),
                ),
              ),
            ),
          ),
          Positioned(
              bottom: 0,
              child: Transform.translate(
                offset: Offset(screenSize.width / 2 - widthContainer / 2, 0),
                child: Container(
                  width: widthContainer,
                  height: heightContainerName,
                  child: DecoratedBox(
                    decoration: BoxDecoration(color: Colors.amber),
                    child: Column(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: <Widget>[
                        Text(game.getNickNameOf(me)),
                        Text("($me)"),
                      ],
                    ),
                  ),
                ),
              )),
          Transform.translate(
            offset: Offset(heightContainerName / 2,
                screenSize.height / 2 - 1.5 * widthContainer),
            child: Transform.rotate(
              angle: pi / 2 * 01,
              origin: Offset(-widthContainer / 2 * 01, 0),
              child: Container(
                width: widthContainer,
                height: heightContainerName,
                child: DecoratedBox(
                  decoration: BoxDecoration(color: Colors.blue),
                  child: Column(
                    mainAxisAlignment: MainAxisAlignment.center,
                    mainAxisSize: MainAxisSize.min,
                    children: <Widget>[
                      Text(game.getNickNameOf(left)),
                      Text("($left)"),
                    ],
                  ),
                ),
              ),
            ),
          ),
          Positioned(
              bottom: heightContainerName + marginCardsPosition,
              child: CardsInHandWidget(
                cards: game.cards,
                cardHeight: cardHeight,
                cardWidth: cardWidth,
                screenWidth: screenSize.width,
              )),
          Positioned(
              bottom:
                  heightContainerName + marginCardsPosition + cardHeight + 20,
              child: BiddingBar(
                onBid: (Bid bid) {
                  // TODO on bid do send
                  print("bid: $bid");
                },
                screenWidth: screenSize.width,
              )),
          Positioned(
            top: 10,
            right: 10,
            child: RecapWidget(
              state: game.state,
              bid: game.currentBid,
              whoseTurn: game.nextPlayer,
            ),
          ),
          Center(
            child: Text(game.id),
          ),
          Center(
            child: DragTarget<cardModel.Card>(
              onWillAccept: (data) {
                print(data);
                return data.playable != null && data.playable;
              },
              onAccept: (data) {
                return ServerCommunication.playCard(data, game.id).then((_) {},
                    onError: (error) => FlushbarHelper.createError(
                            message: "Error: ${error["message"]}",
                            duration: Duration(seconds: 5))
                        .show(context));
              },
              builder: (context, candidateData, rejectedData) {
                if (candidateData.isNotEmpty) {
                  return Container(
                    decoration: BoxDecoration(
                        color: Colors.white,
                        borderRadius: BorderRadius.circular(20)),
                    child: Padding(
                      padding: const EdgeInsets.all(8.0),
                      child: Container(
                        width: cardWidth,
                        height: cardHeight,
                        child: CardWidget(candidateData.last),
                      ),
                    ),
                  );
                }
                if (rejectedData.isNotEmpty) {
                  return Container(
                    color: Colors.red,
                    width: cardWidth,
                    height: cardHeight,
                  );
                }
                return Container(
                  decoration: BoxDecoration(
                      color: Colors.blueGrey[900],
                      borderRadius: BorderRadius.circular(20),
                      boxShadow: [
                        BoxShadow(
                            color: Colors.blueGrey[900],
                            spreadRadius: 2,
                            blurRadius: 2,
                            offset: Offset(2, 2))
                      ]),
                  width: cardWidth,
                  height: cardHeight,
                );
              },
            ),
          )
        ],
      ),
    ));
  }
}
