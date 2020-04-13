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
import 'package:bubble/bubble.dart';
import 'package:flushbar/flushbar_helper.dart';
import 'package:flutter/material.dart';

class TableWidget extends StatefulWidget {
  final Game game;

  TableWidget(this.game);

  @override
  _TableWidgetState createState() => _TableWidgetState();
}

class _TableWidgetState extends State<TableWidget> {
  @override
  Widget build(BuildContext context) {
    var screenSize = MediaQuery.of(context).size;
    double heightContainerName = 50;
    double widthContainer = 200;
    var top = getPlayerPositionTop(widget.game);
    var left = getPlayerPositionLeft(widget.game);
    var right = getPlayerPositionRight(widget.game);
    var me = widget.game.myPosition;
    final double cardWidth = 100;
    final double cardHeight = 150;
    final double marginCardsPosition = 20;
    final double heightBiddingBar = 70;
    var bidLeft = getPlayerBid(widget.game, left);
    var bidTop = getPlayerBid(widget.game, top);
    var bidRight = getPlayerBid(widget.game, right);
    var myBid = getPlayerBid(widget.game, me);
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
                        Text(widget.game.getNickNameOf(top)),
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
                      Text(widget.game.getNickNameOf(right)),
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
                        Text(widget.game.getNickNameOf(me)),
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
                      Text(widget.game.getNickNameOf(left)),
                      Text("($left)"),
                    ],
                  ),
                ),
              ),
            ),
          ),
          Align(
              alignment: Alignment.bottomCenter,
              child: Transform.translate(
                offset: Offset(0, -heightContainerName - marginCardsPosition),
                child: CardsInHandWidget(
                  cards: widget.game.cards,
                  cardHeight: cardHeight,
                  cardWidth: cardWidth,
                  screenWidth: screenSize.width,
                ),
              )),
          Positioned(
              bottom:
                  heightContainerName + marginCardsPosition + cardHeight + 20,
              child: BiddingBar(
                height: heightBiddingBar,
                onBid: (Bid bid) {
                  // TODO on bid do send
                  print("bid: $bid");
                },
                screenWidth: screenSize.width,
                myPosition: widget.game.myPosition,
              )),
          if (bidLeft != null)
            Transform.translate(
              offset: Offset(heightContainerName + 4, -widthContainer / 2),
              child: Bubble(
                color: Colors.white,
                child: Text(bidLeft.toString()),
                alignment: Alignment.centerLeft,
                nip: BubbleNip.leftTop,
                elevation: 10,
              ),
            ),
          if (bidRight != null)
            Transform.translate(
              offset: Offset(-heightContainerName - 4, -widthContainer / 2),
              child: Bubble(
                alignment: Alignment.centerRight,
                color: Colors.white,
                child: Text(bidRight.toString()),
                nip: BubbleNip.rightTop,
                elevation: 10,
              ),
            ),
          if (bidTop != null)
            Transform.translate(
              offset: Offset(0, heightContainerName + 4),
              child: Bubble(
                alignment: Alignment.topCenter,
                color: Colors.white,
                child: Text(bidTop.toString()),
                nip: BubbleNip.no,
                elevation: 10,
              ),
            ),
          if (myBid != null)
            Transform.translate(
              offset: Offset(
                  0,
                  -heightContainerName -
                      cardHeight -
                      marginCardsPosition -
                      20 -
                      heightBiddingBar -
                      10),
              child: Bubble(
                alignment: Alignment.bottomCenter,
                color: Colors.white,
                child: Text(myBid.toString()),
                nip: BubbleNip.no,
                elevation: 10,
              ),
            ),
          Positioned(
            top: 10,
            right: 10,
            child: RecapWidget(
              state: widget.game.state,
              bid: widget.game.currentBid,
              whoseTurn: widget.game.nextPlayer,
            ),
          ),
          Center(
            child: Text(widget.game.id),
          ),
          Center(
            child: DragTarget<cardModel.Card>(
              onWillAccept: (data) {
                // if it is my turn and the card is playable
                return widget.game.myPosition == widget.game.nextPlayer &&
                    data.playable != null &&
                    data.playable;
              },
              onAccept: (data) {
                return ServerCommunication.playCard(data, widget.game.id).then(
                    (_) {},
                    onError: (error) => FlushbarHelper.createError(
                            message: "Error: ${error["message"]}",
                            duration: Duration(seconds: 5))
                        .show(context));
              },
              builder: (context, candidateData, rejectedData) {
                if (widget.game.onTable
                    .map((e) => e.position)
                    .contains(widget.game.myPosition)) {
                  return Container(
                    width: cardWidth,
                    height: cardHeight,
                    padding: EdgeInsets.all(8),
                    child: CardWidget(widget.game.onTable
                        .firstWhere((element) =>
                            element.position == widget.game.myPosition)
                        .card),
                  );
                }
                if (candidateData.isNotEmpty) {
                  return Container(
                    decoration: BoxDecoration(
                        color: Colors.white,
                        borderRadius: BorderRadius.circular(20)),
                    child: Padding(
                      padding: const EdgeInsets.all(8.0),
                      child: Container(
                        width: cardWidth + 20,
                        height: cardHeight + 20,
                        child: CardWidget(candidateData.last),
                      ),
                    ),
                  );
                }
                if (rejectedData.isNotEmpty) {
                  return Container(
                    decoration: BoxDecoration(
                      color: Colors.red[900],
                      borderRadius: BorderRadius.circular(20),
                    ),
                    width: cardWidth + 20,
                    height: cardHeight + 20,
                    child: Center(
                      child: Icon(
                        Icons.do_not_disturb_alt,
                        color: Colors.white,
                        size: 120,
                      ),
                    ),
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
                  width: cardWidth + 20,
                  height: cardHeight + 20,
                );
              },
            ),
          )
        ],
      ),
    ));
  }
}
