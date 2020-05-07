import 'package:FlutterCoinche/state/games_bloc.dart';
import 'package:FlutterCoinche/domain/dto/bid.dart';
import 'package:FlutterCoinche/domain/dto/card.dart';
import 'package:FlutterCoinche/domain/dto/player_position.dart';
import 'package:FlutterCoinche/domain/extensions/bid_extension.dart';
import 'package:FlutterCoinche/ui/resources/colors.dart';
import 'package:FlutterCoinche/ui/resources/dimens.dart';
import 'package:FlutterCoinche/state/game_inherited.dart';
import 'package:FlutterCoinche/ui/widget/neumorphic_container.dart';
import 'package:FlutterCoinche/ui/widget/neumorphic_no_state.dart';
import 'package:bloc_provider/bloc_provider.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';

class BiddingBar extends StatefulWidget {
//  final double screenWidth;
  final void Function(Bid bid) onBid;

//  final double height;
  final BidType initialBidType;

  const BiddingBar(
      {Key key,
//      @required this.screenWidth,
      this.onBid,
//      this.height = 200,
      this.initialBidType = BidType.Simple})
      : super(key: key);

  @override
  _BiddingBarState createState() => _BiddingBarState();
}

class _BiddingBarState extends State<BiddingBar> {
  int points;
  CardColor _cardColor;

  bool _belote;

  BidType bidType;
  GamesBloc gamesBloc;

  @override
  void initState() {
    super.initState();
    gamesBloc = BlocProvider.of<GamesBloc>(context);
    points = 80;
    _belote = false;
    bidType = widget.initialBidType;
    _cardColor = CardColor.HEART;
  }

  Widget _getDropDownSuit(Size screenSize) {
    return DropdownButtonHideUnderline(
      child: DropdownButton<CardColor>(
          value: _cardColor,
          items: CardColor.values
              .map((e) => DropdownMenuItem<CardColor>(
                  value: e,
                  child: Container(
                    width: isLargeScreen(screenSize) ? 50 : 30,
                    child: Image.asset(
                      getAssetImageFromColor(e),
                      fit: BoxFit.contain,
                    ),
                  )))
              .toList(),
          onChanged: (cardColor) {
            setState(() {
              _cardColor = cardColor;
            });
          }),
    );
  }

  Widget _normalTabBar(Size screenSize, int minPoints) {
    return Padding(
      padding: const EdgeInsets.only(top: 8.0, bottom: 8),
      child: NeumorphicNoStateWidget(
        borderRadius: 10,
        sizeShadow: SizeShadow.SMALL,
        pressed: true,
        child: Padding(
          padding: getPaddingCapot(screenSize),
          child: Row(
            crossAxisAlignment: CrossAxisAlignment.center,
            mainAxisSize: MainAxisSize.max,
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: <Widget>[
              _getDropDownSuit(screenSize),
              Padding(
                padding: const EdgeInsets.symmetric(horizontal: 8),
                child: NeumorphicWidget(
                  sizeShadow: SizeShadow.MEDIUM,
                  onTap: () {
                    gamesBloc.playHardButton();
                    if (points > minPoints) {
                      setState(() {
                        points -= 10;
                      });
                    }
                  },
                  child: Padding(
                    padding: const EdgeInsets.all(2),
                    child: Icon(
                      Icons.remove,
                      color: colorText,
                    ),
                  ),
                ),
              ),
              Text(
                points.toString(),
                style: TextStyle(color: colorText),
              ),
              Padding(
                padding: const EdgeInsets.symmetric(horizontal: 8),
                child: NeumorphicWidget(
                  sizeShadow: SizeShadow.MEDIUM,
                  child: Padding(
                    padding: const EdgeInsets.all(2.0),
                    child: Icon(Icons.add, color: colorText),
                  ),
                  onTap: () {
                    gamesBloc.playPlop();
                    if (points < 160) {
                      setState(() {
                        points += 10;
                      });
                    }
                  },
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _capotGeneraleTabView(bool generale, Size screenSize) {
    return Padding(
      padding: const EdgeInsets.only(top: 8.0, bottom: 8),
      child: NeumorphicNoStateWidget(
        sizeShadow: SizeShadow.SMALL,
        pressed: true,
        borderRadius: 10,
        child: Padding(
          padding: getPaddingCapot(screenSize),
          child: Row(
            crossAxisAlignment: CrossAxisAlignment.center,
            mainAxisSize: MainAxisSize.max,
            children: <Widget>[
              _getDropDownSuit(screenSize),
              Switch(
                  value: _belote,
                  onChanged: (value) {
                    gamesBloc.playSoftButton();
                    setState(() {
                      _belote = value;
                    });
                  }),
              Text(
                "Belote-ed",
                style: TextStyle(color: colorText),
              )
            ],
          ),
        ),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    final screenSize = MediaQuery.of(context).size;
    final portrait = MediaQuery.of(context).orientation == Orientation.portrait;
    final bids = GameInherited.of(context, aspectType: Aspects.BIDS).game.bids;
    final me = GameInherited.of(context, aspectType: Aspects.MY_POSITION)
        .game
        .myPosition;
    final nextPlayer =
        GameInherited.of(context, aspectType: Aspects.NEXT_PLAYER)
            .game
            .nextPlayer;
    final enabledBid = nextPlayer == me;

    final canSurcoinche = bids?.canSurcoinche(me) ?? false;
    final canCoinche = bids?.canCoinche(me) ?? false;
    final lastBid = bids?.lastBidCapotGeneral();
    final minPoints = (bids?.lastWhere(
          (element) => element is SimpleBid,
          orElse: () => SimpleBid(points: 70),
        ) as SimpleBid)
            .points +
        10;
    assert(!(canSurcoinche && canCoinche));
    return Container(
      padding: const EdgeInsets.only(left: 2),
      child: portrait
          ? IntrinsicWidth(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.end,
                mainAxisSize: MainAxisSize.min,
                crossAxisAlignment: CrossAxisAlignment.center,
                children: [
                  Row(
                    mainAxisSize: MainAxisSize.max,
                    crossAxisAlignment: CrossAxisAlignment.end,
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      _coincheWidget(
                          canCoinche: canCoinche,
                          lastSimpleBid: lastBid,
                          me: me),
                      _surcoincheWidget(
                          canSurcoinche: canSurcoinche,
                          lastSimpleBid: lastBid,
                          me: me),
                      _passWidget(me: me, enabledBid: enabledBid)
                    ],
                  ),
                  SizedBox(
                    height: isLargeScreen(screenSize) ? 10 : 5,
                  ),
                  _mainWidget(screenSize,
                      me: me, minPoints: minPoints, enabledBid: enabledBid)
                ],
              ),
            )
          : Row(
              mainAxisAlignment: MainAxisAlignment.start,
              mainAxisSize: MainAxisSize.min,
              crossAxisAlignment: CrossAxisAlignment.end,
              children: <Widget>[
                _mainWidget(screenSize,
                    me: me, minPoints: minPoints, enabledBid: enabledBid),
                SizedBox(
                  width: isLargeScreen(screenSize) ? 20 : 10,
                ),
                IntrinsicWidth(
                  child: Column(
                    mainAxisSize: MainAxisSize.min,
                    mainAxisAlignment: MainAxisAlignment.end,
                    crossAxisAlignment: CrossAxisAlignment.stretch,
                    children: <Widget>[
                      _coincheWidget(
                          canCoinche: canCoinche,
                          lastSimpleBid: lastBid,
                          me: me),
                      _surcoincheWidget(
                          canSurcoinche: canSurcoinche,
                          lastSimpleBid: lastBid,
                          me: me),
                      _passWidget(me: me, enabledBid: enabledBid),
                    ],
                  ),
                ),
              ],
            ),
    );
  }

  Widget _coincheWidget(
      {@required bool canCoinche,
      @required Bid lastSimpleBid,
      @required PlayerPosition me}) {
    return Visibility(
      visible: canCoinche,
      child: Padding(
        padding: const EdgeInsets.all(8.0),
        child: NeumorphicWidget(
          sizeShadow: SizeShadow.SMALL,
          onTap: () {
            gamesBloc.playPlop();
            widget.onBid(Coinche(
                position: me, surcoinche: false, annonce: lastSimpleBid));
          },
          borderRadius: 5,
          child: Padding(
            padding: EdgeInsets.all(8),
            child: Text(
              "Coinche",
              style: TextStyle(color: colorText),
              textAlign: TextAlign.center,
            ),
          ),
        ),
      ),
    );
  }

  Widget _surcoincheWidget(
      {@required bool canSurcoinche,
      @required Bid lastSimpleBid,
      @required PlayerPosition me}) {
    return Visibility(
      visible: canSurcoinche,
      child: Padding(
        padding: const EdgeInsets.all(8.0),
        child: NeumorphicWidget(
          sizeShadow: SizeShadow.SMALL,
          onTap: () {
            gamesBloc.playPlop();
            widget.onBid(Coinche(
                position: me, surcoinche: true, annonce: lastSimpleBid));
          },
          borderRadius: 5,
          child: Padding(
            padding: const EdgeInsets.all(8),
            child: Text(
              "Surcoinche",
              style: TextStyle(color: colorText),
              textAlign: TextAlign.center,
            ),
          ),
        ),
      ),
    );
  }

  Widget _passWidget({@required PlayerPosition me, @required bool enabledBid}) {
    return Padding(
      padding: const EdgeInsets.all(8.0),
      child: Visibility(
        visible: enabledBid,
        child: NeumorphicWidget(
          interactable: enabledBid,
          sizeShadow: SizeShadow.SMALL,
          borderRadius: 5,
          onTap: () {
            gamesBloc.playPlop();
            widget.onBid(Pass(position: me));
          },
          child: Padding(
            padding: const EdgeInsets.all(8.0),
            child: Text(
              "Pass",
              style: TextStyle(color: colorText),
              textAlign: TextAlign.center,
            ),
          ),
        ),
      ),
    );
  }

  Widget _mainWidget(Size screenSize,
      {@required PlayerPosition me,
      @required int minPoints,
      @required bool enabledBid}) {
    return NeumorphicNoStateWidget(
      borderRadius: getBorderRadiusBidding(screenSize),
      sizeShadow:
          isLargeScreen(screenSize) ? SizeShadow.LARGE : SizeShadow.MEDIUM,
      child: Padding(
        padding: const EdgeInsets.all(8.0),
        child: IntrinsicWidth(
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: <Widget>[
              Row(
                mainAxisSize: MainAxisSize.max,
                crossAxisAlignment: CrossAxisAlignment.center,
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: <Widget>[
                  GestureDetector(
                    onTap: () {
                      gamesBloc.playSoftButton();
                      setState(() {
                        bidType = BidType.Simple;
                      });
                    },
                    child: Padding(
                      padding: getPaddingBetweenButtonsTypeBid(screenSize),
                      child: NeumorphicNoStateWidget(
                        sizeShadow: SizeShadow.SMALL,
                        pressed: bidType == BidType.Simple,
                        child: Container(
                          padding: getPaddingButtonTypeBid(screenSize),
                          child: Text(
                            "Normal",
                            style: TextStyle(color: colorText),
                          ),
                        ),
                      ),
                    ),
                  ),
                  GestureDetector(
                    onTap: () {
                      gamesBloc.playSoftButton();
                      setState(() {
                        bidType = BidType.Capot;
                      });
                    },
                    child: Padding(
                      padding: getPaddingBetweenButtonsTypeBid(screenSize),
                      child: NeumorphicNoStateWidget(
                        sizeShadow: SizeShadow.SMALL,
                        pressed: bidType == BidType.Capot,
                        child: Container(
                          padding: getPaddingButtonTypeBid(screenSize),
                          child:
                              Text("Capot", style: TextStyle(color: colorText)),
                        ),
                      ),
                    ),
                  ),
                  GestureDetector(
                    onTap: () {
                      gamesBloc.playSoftButton();
                      setState(() {
                        bidType = BidType.Generale;
                      });
                    },
                    child: Padding(
                      padding: getPaddingBetweenButtonsTypeBid(screenSize),
                      child: NeumorphicNoStateWidget(
                        sizeShadow: SizeShadow.SMALL,
                        pressed: bidType == BidType.Generale,
                        child: Container(
                          padding: getPaddingButtonTypeBid(screenSize),
                          child: Text("General",
                              style: TextStyle(color: colorText)),
                        ),
                      ),
                    ),
                  )
                ],
              ),
              _getTab(bidType, screenSize, minPoints: minPoints),
              NeumorphicWidget(
                onTap: () {
                  Bid bid;
                  gamesBloc.playPlop();
                  switch (bidType) {
                    case BidType.Simple:
                      bid = SimpleBid(
                          points: points, color: _cardColor, position: me);
                      break;
                    case BidType.Capot:
                      bid = Capot(
                          color: _cardColor, position: me, belote: _belote);
                      break;
                    case BidType.Generale:
                      bid = General(
                          color: _cardColor, position: me, belote: _belote);
                      break;
                  }
                  widget.onBid(bid);
                },
                borderRadius: 3,
                interactable: enabledBid,
                sizeShadow: SizeShadow.SMALL,
                child: Padding(
                  padding: const EdgeInsets.all(8.0),
                  child: Text("Bid", style: TextStyle(color: colorText)),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _getTab(BidType bidType, Size screenSize, {int minPoints}) {
    switch (bidType) {
      case BidType.Simple:
        return _normalTabBar(screenSize, minPoints);
      case BidType.Capot:
        return _capotGeneraleTabView(false, screenSize);
      case BidType.Generale:
        return _capotGeneraleTabView(true, screenSize);
    }
    return SizedBox();
  }
}

enum BidType { Simple, Capot, Generale }
