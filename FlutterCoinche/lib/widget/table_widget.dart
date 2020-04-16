import 'package:FlutterCoinche/business/calculus.dart';
import 'package:FlutterCoinche/dto/bid.dart';
import 'package:FlutterCoinche/dto/game.dart';
import 'package:FlutterCoinche/dto/player_position.dart';
import 'package:FlutterCoinche/dto/table_state.dart';
import 'package:FlutterCoinche/resources/colors.dart';
import 'package:FlutterCoinche/resources/dimens.dart';
import 'package:FlutterCoinche/rest/server_communication.dart';
import 'package:FlutterCoinche/widget/bidding_bar.dart';
import 'package:FlutterCoinche/widget/cards_hand_widget.dart';
import 'package:FlutterCoinche/widget/middle_area.dart';
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
    final double cardWidth = getCardWidth(screenSize);
    final double cardHeight = cardWidth * golden;
    final double marginCardsPosition = getMarginCardsPosition(screenSize);
    final double heightBiddingBar = getHeightBidding(screenSize);
    final double paddingHeightCards = getPaddingHeightCard(screenSize);
    var bidLeft = getPlayerBid(widget.game, left);
    var bidTop = getPlayerBid(widget.game, top);
    var bidRight = getPlayerBid(widget.game, right);
    var myBid = getPlayerBid(widget.game, me);
    Coinche coinche = (widget.game.bids.lastWhere(
      (element) => element is Coinche,
      orElse: () => null,
    ) as Coinche);
    Bid lastBidCapotGeneralOpposite = (widget.game.bids.lastWhere(
      (element) =>
          (oppositeTeam(widget.game.myPosition).contains(element.position)) &&
          (element is SimpleBid || element is Capot || element is General),
      orElse: () => null,
    ));
    bool enableSurcoinche = false;
    bool enableCoinche = false;
    if (coinche != null && !coinche.surcoinche) {
      enableSurcoinche = true;
    } else if (coinche == null && lastBidCapotGeneralOpposite != null) {
      enableCoinche = true;
    }
    print(screenSize);
    return Column(
      mainAxisSize: MainAxisSize.max,
      crossAxisAlignment: CrossAxisAlignment.center,
      mainAxisAlignment: MainAxisAlignment.start,
      children: <Widget>[
        Expanded(
          child: Stack(
            children: <Widget>[
              Align(
                alignment: Alignment.topCenter,
                child: Container(
                  width: widthContainer,
                  height: heightContainerName,
                  child: DecoratedBox(
                    decoration: BoxDecoration(
                        color: widget.game.nextPlayer == top
                            ? Colors.amber
                            : Colors.transparent),
                    child: Column(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: <Widget>[
                        Text(
                          widget.game.getNickNameOf(top),
                          style: TextStyle(color: colorTextDark),
                        ),
                        Text(
                          "(${top.toString().split(".").last})",
                          style: TextStyle(color: colorTextDark),
                        ),
                      ],
                    ),
                  ),
                ),
              ),

              Align(
                alignment: Alignment.centerRight,
                child: RotatedBox(
                  quarterTurns: -1,
                  child: Container(
                    width: widthContainer,
                    height: heightContainerName,
                    child: DecoratedBox(
                      decoration: BoxDecoration(
                          color: widget.game.nextPlayer == right
                              ? Colors.amber
                              : Colors.transparent),
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.center,
                        mainAxisSize: MainAxisSize.min,
                        mainAxisAlignment: MainAxisAlignment.center,
                        children: <Widget>[
                          Text(
                            widget.game.getNickNameOf(right),
                            style: TextStyle(color: colorTextDark),
                          ),
                          Text(
                            "(${right.toString().split(".").last})",
                            style: TextStyle(color: colorTextDark),
                          ),
                        ],
                      ),
                    ),
                  ),
                ),
              ),

              Align(
                alignment: Alignment.centerLeft,
                child: RotatedBox(
                  quarterTurns: 1,
                  child: Container(
                    width: widthContainer,
                    height: heightContainerName,
                    child: DecoratedBox(
                      decoration: BoxDecoration(
                          color: widget.game.nextPlayer == left
                              ? Colors.amber
                              : Colors.transparent),
                      child: Column(
                        mainAxisAlignment: MainAxisAlignment.center,
                        mainAxisSize: MainAxisSize.min,
                        children: <Widget>[
                          Text(
                            widget.game.getNickNameOf(left),
                            style: TextStyle(color: colorTextDark),
                          ),
                          Text(
                            "(${left.toString().split(".").last})",
                            style: TextStyle(color: colorTextDark),
                          ),
                        ],
                      ),
                    ),
                  ),
                ),
              ),
              // The last bids of the players
              if (bidLeft != null && widget.game.state == TableState.BIDDING)
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
              if (bidRight != null && widget.game.state == TableState.BIDDING)
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
              if (bidTop != null && widget.game.state == TableState.BIDDING)
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
              if (myBid != null && widget.game.state == TableState.BIDDING)
                Transform.translate(
                  offset: Offset(
                      0,
                      -heightContainerName -
                          cardHeight -
                          marginCardsPosition -
                          paddingHeightCards * 2 -
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

              Padding(
                padding: EdgeInsets.only(
                    top: heightContainerName,
                    bottom: marginCardsPosition,
                    left: heightContainerName,
                    right: heightContainerName),
                child: SizedBox.expand(
                  child: MiddleArea(
                    cardHeight: cardHeight,
                    cardWidth: cardWidth,
                    game: widget.game,
                    me: me,
                    left: left,
                    screenSize: screenSize,
                    right: right,
                    top: top,
                  ),
                ),
              ),
              AnimatedPositioned(
                bottom: getBottomOfBiddingBar(screenSize),
                left: widget.game.state == TableState.BIDDING
                    ? heightContainerName
                    : -screenSize.width,
                duration: Duration(milliseconds: 500),
                child: AnimatedOpacity(
                  duration: Duration(milliseconds: 500),
                  opacity: widget.game.state == TableState.BIDDING ? 1 : 0,
                  child: BiddingBar(
                    minBidPoints: (widget.game.bids.lastWhere(
                          (element) => element is SimpleBid,
                          orElse: () => SimpleBid(points: 70),
                        ) as SimpleBid)
                            .points +
                        10,
                    enabledBid:
                        widget.game.nextPlayer == widget.game.myPosition,
//                height: heightBiddingBar,
                    onBid: (Bid bid) {
                      ServerCommunication.bid(bid, widget.game.id).then(
                          (success) {
                        print("success");
                        FlushbarHelper.createSuccess(message: "bid $bid placed")
                            .show(context);
                      }, onError: (error) {
                        print("Error: $error");
                        FlushbarHelper.createError(
                                message: "Error placing bid: $error")
                            .show(context);
                      });
                    },
//                screenWidth: screenSize.width,
                    myPosition: widget.game.myPosition,
                    enabledSurcoinche: enableSurcoinche,
                    enabledCoinche: enableCoinche,
                    lastSimpleBid: lastBidCapotGeneralOpposite,
                  ),
                ),
              ),
              // Top right recap widget
              Positioned(
                top: 10,
                right: 10,
                child: RecapWidget(
                  state: widget.game.state,
                  bid: widget.game.currentBid,
                  whoseTurn: widget.game.nextPlayer,
                ),
              ),
            ],
          ),
        ),
        // My cards
        CardsInHandWidget(
          inPlayMode: widget.game.state == TableState.PLAYING,
          myTurn: widget.game.myPosition == widget.game.nextPlayer,
          cards: widget.game.cards,
          cardHeight: cardHeight,
          cardWidth: cardWidth,
          screenWidth: screenSize.width,
          paddingVertical: paddingHeightCards,
        ),
        Container(
          width: widthContainer,
          height: heightContainerName,
          child: DecoratedBox(
            decoration: BoxDecoration(
                color: widget.game.nextPlayer == me
                    ? Colors.amber
                    : Colors.transparent),
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: <Widget>[
                Text(
                  widget.game.getNickNameOf(me),
                  style: TextStyle(color: colorTextDark),
                ),
                Text(
                  "(${me.toString().split(".").last})",
                  style: TextStyle(color: colorTextDark),
                ),
              ],
            ),
          ),
        ),
      ],
    );
  }

  List<PlayerPosition> oppositeTeam(PlayerPosition myPosition) {
    switch (myPosition) {
      case PlayerPosition.NORTH:
      case PlayerPosition.SOUTH:
        return [PlayerPosition.EAST, PlayerPosition.WEST];
      case PlayerPosition.EAST:
      case PlayerPosition.WEST:
        return [PlayerPosition.NORTH, PlayerPosition.SOUTH];
    }
    return [];
  }
}
