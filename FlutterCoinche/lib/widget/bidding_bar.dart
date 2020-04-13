import 'package:FlutterCoinche/dto/bid.dart';
import 'package:FlutterCoinche/dto/card.dart' as cardModel;
import 'package:FlutterCoinche/dto/player_position.dart';
import 'package:FlutterCoinche/resources/colors.dart';
import 'package:FlutterCoinche/widget/neumorphic_container.dart';
import 'package:FlutterCoinche/widget/neumorphic_no_state.dart';
import 'package:flutter/material.dart';

class BiddingBar extends StatefulWidget {
  final double screenWidth;
  final int minBidPoints;
  final void Function(Bid bid) onBid;
  final double height;
  final PlayerPosition myPosition;
  final bool enabledBid;
  final bool enabledCoinche, enabledSurcoinche;
  final BidType initialBidType;

  final Bid lastSimpleBid;

  const BiddingBar(
      {Key key,
      @required this.screenWidth,
      this.onBid,
      this.minBidPoints = 80,
      @required this.myPosition,
      this.height = 200,
      @required this.enabledBid,
      @required this.enabledCoinche,
      @required this.enabledSurcoinche,
      this.lastSimpleBid,
      this.initialBidType = BidType.Simple})
      : assert(enabledCoinche != null && enabledSurcoinche != null),
        assert(!(enabledSurcoinche && enabledCoinche)),
        super(key: key);

  @override
  _BiddingBarState createState() => _BiddingBarState();
}

class _BiddingBarState extends State<BiddingBar> {
  int points;
  cardModel.CardColor _cardColor;

  bool _belote;

  BidType bidType;

  @override
  void initState() {
    super.initState();
    points = widget.minBidPoints < 160 ? widget.minBidPoints : 160;
    _belote = false;
    bidType = widget.initialBidType;
    _cardColor = cardModel.CardColor.HEART;
  }

  Widget _normalTabBar() {
    return Padding(
      padding: const EdgeInsets.all(8.0),
      child: NeumorphicNoStateWidget(
        borderRadius: 10,
        sizeShadow: SizeShadow.SMALL,
        pressed: true,
        child: Padding(
          padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
          child: Row(
            crossAxisAlignment: CrossAxisAlignment.center,
            mainAxisSize: MainAxisSize.min,
            children: <Widget>[
              Padding(padding: const EdgeInsets.only(left: 8)),
              Padding(
                padding: const EdgeInsets.symmetric(horizontal: 8),
                child: NeumorphicWidget(
                  sizeShadow: SizeShadow.MEDIUM,
                  onTap: () {
                    if (points > widget.minBidPoints) {
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
                    if (points < 160) {
                      setState(() {
                        points += 10;
                      });
                    }
                  },
                ),
              ),
              DropdownButtonHideUnderline(
                child: DropdownButton<cardModel.CardColor>(
                    value: _cardColor,
                    items: cardModel.CardColor.values
                        .map((e) => DropdownMenuItem<cardModel.CardColor>(
                            value: e,
                            child: Image.asset(
                              "images/${cardModel.getAssetImageFromColor(e)}",
                              fit: BoxFit.contain,
                            )))
                        .toList(),
                    onChanged: (cardColor) {
                      setState(() {
                        _cardColor = cardColor;
                      });
                    }),
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _capotGeneraleTabView(bool generale) {
    return Padding(
      padding: const EdgeInsets.all(8.0),
      child: NeumorphicNoStateWidget(
        sizeShadow: SizeShadow.SMALL,
        pressed: true,
        borderRadius: 10,
        child: Padding(
          padding: const EdgeInsets.symmetric(horizontal: 8.0, vertical: 4),
          child: Row(
            crossAxisAlignment: CrossAxisAlignment.center,
            mainAxisSize: MainAxisSize.min,
            children: <Widget>[
              DropdownButtonHideUnderline(
                child: DropdownButton<cardModel.CardColor>(
                    value: _cardColor,
                    items: cardModel.CardColor.values
                        .map((e) => DropdownMenuItem<cardModel.CardColor>(
                            value: e,
                            child: Image.asset(
                              "images/${cardModel.getAssetImageFromColor(e)}",
                              fit: BoxFit.contain,
                            )))
                        .toList(),
                    onChanged: (cardColor) {
                      setState(() {
                        _cardColor = cardColor;
                      });
                    }),
              ),
              Switch(
                  value: _belote,
                  onChanged: (value) {
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
    return Container(
      padding: const EdgeInsets.only(bottom: 20, left: 15, right: 15),
      color: Colors.white,
      width: widget.screenWidth,
      height: widget.height,
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        mainAxisSize: MainAxisSize.min,
        crossAxisAlignment: CrossAxisAlignment.end,
        children: <Widget>[
          NeumorphicNoStateWidget(
            borderRadius: 30,
            sizeShadow: SizeShadow.LARGE,
            child: Padding(
              padding: const EdgeInsets.all(8.0),
              child: Column(
                mainAxisSize: MainAxisSize.min,
                children: <Widget>[
                  Row(
                    mainAxisSize: MainAxisSize.min,
                    crossAxisAlignment: CrossAxisAlignment.center,
                    mainAxisAlignment: MainAxisAlignment.start,
                    children: <Widget>[
                      GestureDetector(
                        onTap: () {
                          setState(() {
                            bidType = BidType.Simple;
                          });
                        },
                        child: Padding(
                          padding: const EdgeInsets.all(8.0),
                          child: NeumorphicNoStateWidget(
                            sizeShadow: SizeShadow.SMALL,
                            pressed: bidType == BidType.Simple,
                            child: Container(
                              padding: const EdgeInsets.all(8),
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
                          setState(() {
                            bidType = BidType.Capot;
                          });
                        },
                        child: Padding(
                          padding: const EdgeInsets.all(8.0),
                          child: NeumorphicNoStateWidget(
                            sizeShadow: SizeShadow.SMALL,
                            pressed: bidType == BidType.Capot,
                            child: Container(
                              padding: const EdgeInsets.all(8),
                              child: Text("Capot",
                                  style: TextStyle(color: colorText)),
                            ),
                          ),
                        ),
                      ),
                      GestureDetector(
                        onTap: () {
                          setState(() {
                            bidType = BidType.Generale;
                          });
                        },
                        child: Padding(
                          padding: const EdgeInsets.all(8.0),
                          child: NeumorphicNoStateWidget(
                            sizeShadow: SizeShadow.SMALL,
                            pressed: bidType == BidType.Generale,
                            child: Container(
                              padding: const EdgeInsets.all(8),
                              child: Text("General",
                                  style: TextStyle(color: colorText)),
                            ),
                          ),
                        ),
                      )
                    ],
                  ),
                  _getTab(bidType),
                  NeumorphicWidget(
                    onTap: () {
                      Bid bid;
                      switch (bidType) {
                        case BidType.Simple:
                          bid = SimpleBid(
                              points: points,
                              color: _cardColor,
                              position: widget.myPosition);
                          break;
                        case BidType.Capot:
                          bid = Capot(
                              color: _cardColor,
                              position: widget.myPosition,
                              belote: _belote);
                          break;
                        case BidType.Generale:
                          bid = General(
                              color: _cardColor,
                              position: widget.myPosition,
                              belote: _belote);
                          break;
                      }
                      widget.onBid(bid);
                    },
                    borderRadius: 3,
                    interactable: widget.enabledBid,
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
          IntrinsicWidth(
            child: Column(
              mainAxisSize: MainAxisSize.min,
              mainAxisAlignment: MainAxisAlignment.end,
              crossAxisAlignment: CrossAxisAlignment.stretch,
              children: <Widget>[
                Visibility(
                  visible: widget.enabledCoinche,
                  child: Padding(
                    padding: const EdgeInsets.all(8.0),
                    child: NeumorphicWidget(
                      sizeShadow: SizeShadow.SMALL,
                      onTap: () {
                        widget.onBid(Coinche(
                            position: widget.myPosition,
                            surcoinche: false,
                            annonce: widget.lastSimpleBid));
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
                ),
                Visibility(
                  visible: widget.enabledSurcoinche,
                  child: Padding(
                    padding: const EdgeInsets.all(8.0),
                    child: NeumorphicWidget(
                      sizeShadow: SizeShadow.SMALL,
                      onTap: () {
                        widget.onBid(Coinche(
                            position: widget.myPosition,
                            surcoinche: true,
                            annonce: widget.lastSimpleBid));
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
                ),
                Padding(
                  padding: const EdgeInsets.all(8.0),
                  child: NeumorphicWidget(
                    sizeShadow: SizeShadow.SMALL,
                    borderRadius: 5,
                    onTap: () =>
                        widget.onBid(Pass(position: widget.myPosition)),
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
              ],
            ),
          ),
        ],
      ),
    );
  }

  Widget _getTab(BidType bidType) {
    switch (bidType) {
      case BidType.Simple:
        return _normalTabBar();
      case BidType.Capot:
        return _capotGeneraleTabView(false);
      case BidType.Generale:
        return _capotGeneraleTabView(true);
    }
    return SizedBox();
  }
}

enum BidType { Simple, Capot, Generale }
