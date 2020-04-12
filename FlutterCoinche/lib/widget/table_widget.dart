import 'dart:math';

import 'package:FlutterCoinche/business/calculus.dart';
import 'package:FlutterCoinche/dto/card.dart' as cardModel;
import 'package:FlutterCoinche/dto/game.dart';
import 'package:FlutterCoinche/rest/server_communication.dart';
import 'package:FlutterCoinche/widget/card_widget.dart';
import 'package:flutter/material.dart';

class TableWidget extends StatelessWidget {
  final Game game;

  TableWidget(this.game);

  @override
  Widget build(BuildContext context) {
    var screenSize = MediaQuery.of(context).size;
    double heightContainer = 50;
    double widthContainer = 200;
    var top = getPlayerPositionTop(game);
    var left = getPlayerPositionLeft(game);
    var right = getPlayerPositionRight(game);
    var me = game.myPosition;
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
                  height: heightContainer,
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
            offset: Offset(screenSize.width - heightContainer,
                screenSize.height / 2 - widthContainer / 4),
            child: Transform.rotate(
              origin: Offset(-widthContainer / 2, -heightContainer / 2),
              angle: -pi / 2,
              child: Container(
                width: widthContainer,
                height: heightContainer,
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
                  height: heightContainer,
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
            offset: Offset(heightContainer / 2,
                screenSize.height / 2 - 1.5 * widthContainer),
            child: Transform.rotate(
              angle: pi / 2 * 01,
              origin: Offset(-widthContainer / 2 * 01, 0),
              child: Container(
                width: widthContainer,
                height: heightContainer,
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
              bottom: heightContainer + 20,
              child: Container(
                  color: Color.fromRGBO(192, 214, 233, 1),
//                  padding: EdgeInsets.all(20),
                  height: 160,
                  width: screenSize.width,
                  child: ListView.builder(
                      scrollDirection: Axis.horizontal,
                      itemCount: game.cards.length,
                      itemBuilder: (context, index) => Container(
                            width: 90,
//                            height: 200,
                            child: Padding(
                              padding: const EdgeInsets.symmetric(
                                  vertical: 10.0, horizontal: 4),
                              child: LongPressDraggable(
                                  hapticFeedbackOnStart: true,
                                  data: game.cards[index],
                                  childWhenDragging: Container(
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
                                      padding: EdgeInsets.all(10),
                                      width: 150,
                                      height: 200,
                                      decoration: BoxDecoration(
                                        borderRadius: BorderRadius.circular(20),
                                        color: Colors.grey[100],
                                      ),
                                      child: CardWidget(game.cards[index])),
                                  child: CardWidget(game.cards[index])),
                            ),
                          )))),
          Center(
            child: Text(game.id),
          ),
          Center(
            child: DragTarget<cardModel.Card>(
              onWillAccept: (data) {
                print(data);
                return data.playable != null && data.playable;
              },
              onAccept: (data) => ServerCommunication.playCard(data, game.id),
              builder: (context, candidateData, rejectedData) {
                if (candidateData.isNotEmpty) {
                  return Container(
                    color: Colors.orange,
                    width: 100,
                    height: 100,
                    child: CardWidget(candidateData.last),
                  );
                }
                return Text("dragTarget");
              },
            ),
          )
        ],
      ),
    ));
  }
}
