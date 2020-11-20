import 'package:coinche/domain/dto/bid.dart';
import 'package:coinche/domain/dto/card.dart';
import 'package:coinche/domain/dto/game.dart';
import 'package:coinche/domain/dto/player_position.dart';
import 'package:coinche/domain/extensions/bid_extension.dart';
import 'package:coinche/state/game_model.dart';
import 'package:coinche/ui/resources/colors.dart';
import 'package:coinche/ui/resources/dimens.dart';
import 'package:coinche/ui/widget/neumorphic_container.dart';
import 'package:coinche/ui/widget/neumorphic_no_state.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

class BiddingBar extends StatefulWidget {
  final void Function(Bid bid) onBid;

  final BidType initialBidType;
  final Game game;

  const BiddingBar(
      {Key? key,
      required this.onBid,
      this.initialBidType = BidType.simple,
      required this.game})
      : super(key: key);

  @override
  BiddingBarState createState() => BiddingBarState();
}

class BiddingBarState extends State<BiddingBar> {
  late int _points;
  late CardColor _cardColor;

  late bool _belote;

  late BidType bidType;
  late GameModel _gamesModel;

  late int _minPoints;

  @override
  void initState() {
    super.initState();
    _gamesModel = context.read<GameModel>();
    _points = 80;
    _belote = false;
    bidType = widget.initialBidType;
    _cardColor = CardColor.heart;
    _whenChangingState(widget.game, init: true);
  }

  set minPoints(int min) {
    _minPoints = min;
    if (min > 160) return; // No can do, too high!
    if (min > _points) {
      if (_points == min) return;
      setState(() {
        _points = min;
      });
    }
  }

  set points(int p) {
    if (p == _points) return;
    setState(() {
      _points = p;
    });
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
          onChanged: (CardColor? cardColor) {
            if (cardColor != null) {
              setState(() {
                _cardColor = cardColor;
              });
            }
          }),
    );
  }

  Widget _normalTabBar(Size screenSize, int minPoints) {
    return Padding(
      padding: const EdgeInsets.only(top: 8.0, bottom: 8),
      child: NeumorphicNoStateWidget(
        borderRadius: 10,
        sizeShadow: SizeShadow.small,
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
                  sizeShadow: SizeShadow.medium,
                  onTap: () {
                    _gamesModel.playHardButton();
                    if (_points > minPoints) {
                      setState(() {
                        _points -= 10;
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
                _points.toString(),
                style: TextStyle(color: colorText),
              ),
              Padding(
                padding: const EdgeInsets.symmetric(horizontal: 8),
                child: NeumorphicWidget(
                  sizeShadow: SizeShadow.medium,
                  child: Padding(
                    padding: const EdgeInsets.all(2.0),
                    child: Icon(Icons.add, color: colorText),
                  ),
                  onTap: () {
                    _gamesModel.playPlop();
                    if (_points < 160) {
                      setState(() {
                        _points += 10;
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
        sizeShadow: SizeShadow.small,
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
                    _gamesModel.playSoftButton();
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

  List<Bid>? bids;
  late PlayerPosition me, nextPlayer;
  bool enabledBid = false, canSurcoinche = false, canCoinche = false;
  Bid? lastBid;

  void _whenChangingState(Game game, {bool init = false}) {
    bids = game.bids;
    me = game.myPosition;
    nextPlayer = game.nextPlayer;
    enabledBid = nextPlayer == me;

    canSurcoinche = bids?.canSurcoinche(me) ?? false;
    canCoinche = bids?.canCoinche(me) ?? false;
    lastBid = bids?.lastBidCapotGeneral();
    final calculateMin = (bids?.lastWhere(
          (element) => element is SimpleBid,
          orElse: () => SimpleBid(points: 70),
        ) as SimpleBid)
            .points +
        10;
    if (init) {
      _minPoints = calculateMin;
    } else {
      minPoints = calculateMin;
    }
    assert(!(canSurcoinche && canCoinche));
  }

  @override
  void didUpdateWidget(covariant BiddingBar oldWidget) {
    super.didUpdateWidget(oldWidget);
    if (oldWidget.game != widget.game) {
      _whenChangingState(widget.game);
    }
  }

  @override
  Widget build(BuildContext context) {
    final screenSize = MediaQuery.of(context).size;
    final portrait = MediaQuery.of(context).orientation == Orientation.portrait;

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
                      me: me, minPoints: _minPoints, enabledBid: enabledBid)
                ],
              ),
            )
          : Row(
              mainAxisAlignment: MainAxisAlignment.start,
              mainAxisSize: MainAxisSize.min,
              crossAxisAlignment: CrossAxisAlignment.end,
              children: <Widget>[
                _mainWidget(screenSize,
                    me: me, minPoints: _minPoints, enabledBid: enabledBid),
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
      {required bool canCoinche,
      required Bid? lastSimpleBid,
      required PlayerPosition me}) {
    return Visibility(
      visible: canCoinche,
      child: Padding(
        padding: const EdgeInsets.all(8.0),
        child: NeumorphicWidget(
          sizeShadow: SizeShadow.small,
          onTap: () {
            _gamesModel.playPlop();
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
      {required bool canSurcoinche,
      required Bid? lastSimpleBid,
      required PlayerPosition me}) {
    return Visibility(
      visible: canSurcoinche,
      child: Padding(
        padding: const EdgeInsets.all(8.0),
        child: NeumorphicWidget(
          sizeShadow: SizeShadow.small,
          onTap: () {
            _gamesModel.playPlop();
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

  Widget _passWidget({required PlayerPosition me, required bool enabledBid}) {
    return Padding(
      padding: const EdgeInsets.all(8.0),
      child: Visibility(
        visible: enabledBid,
        child: NeumorphicWidget(
          interactable: enabledBid,
          sizeShadow: SizeShadow.small,
          borderRadius: 5,
          onTap: () {
            _gamesModel.playPlop();
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
      {required PlayerPosition me,
      required int minPoints,
      required bool enabledBid}) {
    return NeumorphicNoStateWidget(
      borderRadius: getBorderRadiusBidding(screenSize),
      sizeShadow:
          isLargeScreen(screenSize) ? SizeShadow.large : SizeShadow.medium,
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
                      _gamesModel.playSoftButton();
                      setState(() {
                        bidType = BidType.simple;
                      });
                    },
                    child: Padding(
                      padding: getPaddingBetweenButtonsTypeBid(screenSize),
                      child: NeumorphicNoStateWidget(
                        sizeShadow: SizeShadow.small,
                        pressed: bidType == BidType.simple,
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
                      _gamesModel.playSoftButton();
                      setState(() {
                        bidType = BidType.capot;
                      });
                    },
                    child: Padding(
                      padding: getPaddingBetweenButtonsTypeBid(screenSize),
                      child: NeumorphicNoStateWidget(
                        sizeShadow: SizeShadow.small,
                        pressed: bidType == BidType.capot,
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
                      _gamesModel.playSoftButton();
                      setState(() {
                        bidType = BidType.generale;
                      });
                    },
                    child: Padding(
                      padding: getPaddingBetweenButtonsTypeBid(screenSize),
                      child: NeumorphicNoStateWidget(
                        sizeShadow: SizeShadow.small,
                        pressed: bidType == BidType.generale,
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
                  _gamesModel.playPlop();
                  switch (bidType) {
                    case BidType.simple:
                      bid = SimpleBid(
                          points: _points, color: _cardColor, position: me);
                      break;
                    case BidType.capot:
                      bid = Capot(
                          color: _cardColor, position: me, belote: _belote);
                      break;
                    case BidType.generale:
                      bid = General(
                          color: _cardColor, position: me, belote: _belote);
                      break;
                  }
                  widget.onBid(bid);
                },
                borderRadius: 3,
                interactable: enabledBid,
                sizeShadow: SizeShadow.small,
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

  Widget _getTab(BidType bidType, Size screenSize, {required int minPoints}) {
    switch (bidType) {
      case BidType.simple:
        return _normalTabBar(screenSize, minPoints);
      case BidType.capot:
        return _capotGeneraleTabView(false, screenSize);
      case BidType.generale:
        return _capotGeneraleTabView(true, screenSize);
    }
  }
}

class BiddingBarProvided extends StatelessWidget {
  final BidType initialBidType;

  const BiddingBarProvided({
    Key? key,
    this.initialBidType = BidType.simple,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Consumer<GameModel>(
      builder: (context, gameModel, child) => BiddingBar(
        initialBidType: initialBidType,
        onBid: (bid) {
          gameModel.bid(bid);
        },
        game: gameModel.game,
      ),
    );
  }
}

enum BidType { simple, capot, generale }
