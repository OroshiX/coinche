import 'package:FlutterCoinche/business/calculus.dart';
import 'package:FlutterCoinche/dto/bid.dart';
import 'package:FlutterCoinche/dto/player_position.dart';
import 'package:FlutterCoinche/dto/table_state.dart';
import 'package:FlutterCoinche/resources/colors.dart';
import 'package:FlutterCoinche/resources/dimens.dart';
import 'package:FlutterCoinche/rest/server_communication.dart';
import 'package:FlutterCoinche/widget/bidding_bar.dart';
import 'package:FlutterCoinche/widget/cards_hand_widget.dart';
import 'package:FlutterCoinche/widget/game_inherited.dart';
import 'package:FlutterCoinche/widget/landscape/landscape_score_widget.dart';
import 'package:FlutterCoinche/widget/middle_area.dart';
import 'package:FlutterCoinche/widget/neumorphic_container.dart';
import 'package:FlutterCoinche/widget/player_avatar.dart';
import 'package:FlutterCoinche/widget/portrait/portrait_score_widget.dart';
import 'package:FlutterCoinche/widget/recap_widget.dart';
import 'package:auto_size_text/auto_size_text.dart';
import 'package:bubble/bubble.dart';
import 'package:flushbar/flushbar_helper.dart';
import 'package:flutter/material.dart';

class TableWidget extends StatefulWidget {
  final Function quit;

  TableWidget({@required this.quit});

  @override
  _TableWidgetState createState() => _TableWidgetState();
}

class _TableWidgetState extends State<TableWidget> {
  AutoSizeGroup autoSizeGroup;

  @override
  void initState() {
    super.initState();
    autoSizeGroup = AutoSizeGroup();
  }

  @override
  Widget build(BuildContext context) {
    var screenSize = MediaQuery.of(context).size;
    double widthContainerName = 65;
    double heightContainer = 104;
    final nicknames =
        GameInherited.of(context, aspectType: Aspects.NICKNAMES).game.nicknames;
    final bids = GameInherited.of(context, aspectType: Aspects.BIDS).game.bids;
    final me = GameInherited.of(context, aspectType: Aspects.MY_POSITION)
        .game
        .myPosition;
    final state =
        GameInherited.of(context, aspectType: Aspects.STATE).game.state;
    final nextPlayer =
        GameInherited.of(context, aspectType: Aspects.NEXT_PLAYER)
            .game
            .nextPlayer;
    final id = GameInherited.of(context, aspectType: Aspects.ID).game.id;
    final posTableToCardinal = getPosTableToCardinal(me);
    var top = posTableToCardinal[AxisDirection.up];
    var left = posTableToCardinal[AxisDirection.left];
    var right = posTableToCardinal[AxisDirection.right];

    final double cardWidth = getCardWidth(screenSize);
    final double cardHeight = cardWidth * golden;
    final double marginCardsPosition = getMarginCardsPosition(screenSize);
    final double heightBiddingBar = getHeightBidding(screenSize);
    final double paddingHeightCards = getPaddingHeightCard(screenSize);
    var bidLeft = getPlayerBid(bids, left);
    var bidTop = getPlayerBid(bids, top);
    var bidRight = getPlayerBid(bids, right);
    var myBid = getPlayerBid(bids, me);
    Coinche coinche = (bids.lastWhere(
      (element) => element is Coinche,
      orElse: () => null,
    ) as Coinche);
    Bid lastBidCapotGeneralOpposite = (bids.lastWhere(
      (element) =>
          (oppositeTeam(me).contains(element.position)) &&
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
                alignment: Alignment.topLeft,
                child: OrientationBuilder(
                  builder: (context, orientation) {
                    if (orientation == Orientation.portrait)
                      return PortraitScoreWidget(
                        onTapMessages: () {
                          // TODO Messages
                          print("TODO");
                        },
                        onTapExit: () async {
                          var quit = (await widget.quit()) ?? false;
                          if (quit) Navigator.of(context).pop();
                        },
                      );
                    return LandscapeScoreWidget(
                      onTapExit: () async {
                        var quit = (await widget.quit()) ?? false;
                        if (quit) Navigator.of(context).pop();
                      },
                      onTapMessages: () {
                        // TODO messages
                        print("TODO messages");
                      },
                    );
                  },
                ),
              ),
              Align(
                alignment: Alignment.topCenter,
                child: Container(
                  width: widthContainerName,
                  height: heightContainer,
                  child: PlayerAvatar(
                    position: top,
                    nick: nicknames.fromPosition(top),
                    autoSizeGroup: autoSizeGroup,
                  ),
                ),
              ),
              Align(
                alignment: Alignment.centerRight,
                child: Container(
                  width: widthContainerName,
                  height: heightContainer,
                  child: PlayerAvatar(
                    nick: nicknames.fromPosition(right),
                    autoSizeGroup: autoSizeGroup,
                    position: right,
                  ),
                ),
              ),
              Align(
                alignment: Alignment.centerLeft,
                child: Container(
                  width: widthContainerName,
                  height: heightContainer,
                  child: PlayerAvatar(
                    nick: nicknames.fromPosition(left),
                    autoSizeGroup: autoSizeGroup,
                    position: left,
                  ),
                ),
              ),
              // The last bids of the players
              if (bidLeft != null && state == TableState.BIDDING)
                Transform.translate(
                  offset: Offset(widthContainerName + 4, -heightContainer / 2),
                  child: Bubble(
                    color: Colors.white,
                    child: Text(bidLeft.toString()),
                    alignment: Alignment.centerLeft,
                    nip: BubbleNip.leftTop,
                    elevation: 10,
                  ),
                ),
              if (bidRight != null && state == TableState.BIDDING)
                Transform.translate(
                  offset: Offset(-widthContainerName - 4, -heightContainer / 2),
                  child: Bubble(
                    alignment: Alignment.centerRight,
                    color: Colors.white,
                    child: Text(bidRight.toString()),
                    nip: BubbleNip.rightTop,
                    elevation: 10,
                  ),
                ),
              if (bidTop != null && state == TableState.BIDDING)
                Transform.translate(
                  offset: Offset(0, widthContainerName + 4),
                  child: Bubble(
                    alignment: Alignment.topCenter,
                    color: Colors.white,
                    child: Text(bidTop.toString()),
                    nip: BubbleNip.no,
                    elevation: 10,
                  ),
                ),
              if (myBid != null && state == TableState.BIDDING)
                Transform.translate(
                  offset: Offset(
                      0,
                      -widthContainerName -
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
                    top: widthContainerName,
                    bottom: marginCardsPosition,
                    left: widthContainerName,
                    right: widthContainerName),
                child: SizedBox.expand(
                  child: MiddleArea(
                    cardHeight: cardHeight,
                    cardWidth: cardWidth,
                    screenSize: screenSize,
                    posTableToCardinal: posTableToCardinal,
                  ),
                ),
              ),
              AnimatedPositioned(
                bottom: getBottomOfBiddingBar(screenSize),
                left: state == TableState.BIDDING
                    ? widthContainerName
                    : -screenSize.width,
                duration: Duration(milliseconds: 500),
                child: AnimatedOpacity(
                  duration: Duration(milliseconds: 500),
                  opacity: state == TableState.BIDDING ? 1 : 0,
                  child: BiddingBar(
                    minBidPoints: (bids.lastWhere(
                          (element) => element is SimpleBid,
                          orElse: () => SimpleBid(points: 70),
                        ) as SimpleBid)
                            .points +
                        10,
                    enabledBid: nextPlayer == me,
//                height: heightBiddingBar,
                    onBid: (Bid bid) {
                      ServerCommunication.bid(bid, id).then((success) {
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
                    myPosition: me,
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
                child: const RecapWidget(),
              ),
            ],
          ),
        ),
        // My cards
        SizedBox(
          width: screenSize.width,
          child: Stack(children: [
            Center(
              child: Container(
                width: widthContainerName,
                height: heightContainer,
                child: PlayerAvatar(
                  nick: nicknames.fromPosition(me),
                  autoSizeGroup: autoSizeGroup,
                  position: me,
                ),
              ),
            ),
            Positioned(
              right: 2,
              bottom: 2,
              child: Visibility(
                visible: state == TableState.PLAYING,
                child: NeumorphicWidget(
                    onTap: () {},
                    child: Container(
                        padding:
                            EdgeInsets.symmetric(horizontal: 10, vertical: 10),
                        child: Text(
                          "Last Trick",
                          style: TextStyle(color: colorTextDark),
                        )),
                    sizeShadow: SizeShadow.SMALL,
                    borderRadius: 10,
                    interactable: true),
              ),
            )
          ]),
        ),
        CardsInHandWidget(
          inPlayMode: state == TableState.PLAYING,
          myTurn: me == nextPlayer,
          cardHeight: cardHeight,
          cardWidth: cardWidth,
          screenWidth: screenSize.width,
          paddingVertical: paddingHeightCards,
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
