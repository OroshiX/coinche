import 'package:FlutterCoinche/dto/bid.dart';
import 'package:FlutterCoinche/dto/card.dart' as cardModel;
import 'package:FlutterCoinche/dto/player_position.dart';
import 'package:FlutterCoinche/widget/neumorphic_container.dart';
import 'package:flutter/material.dart';

class BiddingBar extends StatefulWidget {
  final double screenWidth;
  final int minBidPoints;
  final void Function(Bid bid) onBid;

  final PlayerPosition myPosition;

  const BiddingBar(
      {Key key,
      @required this.screenWidth,
      this.onBid,
      this.minBidPoints = 80,
      @required this.myPosition})
      : super(key: key);

  @override
  _BiddingBarState createState() => _BiddingBarState();
}

class _BiddingBarState extends State<BiddingBar> {
  int points;
  cardModel.CardColor _cardColor;

  @override
  void initState() {
    super.initState();
    points = widget.minBidPoints;
    _cardColor = cardModel.CardColor.HEART;
  }

  @override
  Widget build(BuildContext context) {
    return Container(
      color: Colors.white,
      width: widget.screenWidth,
      height: 70,
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        mainAxisSize: MainAxisSize.min,
        crossAxisAlignment: CrossAxisAlignment.center,
        children: <Widget>[
          Padding(
            padding: const EdgeInsets.all(8.0),
            child: NeumorphicWidget(
              borderRadius: 10,
              sizeShadow: SizeShadow.MEDIUM,
              child: Row(
                crossAxisAlignment: CrossAxisAlignment.center,
                mainAxisSize: MainAxisSize.min,
                children: <Widget>[
                  FlatButton(
                    onPressed: () {
                      if (points > widget.minBidPoints) {
                        setState(() {
                          points -= 10;
                        });
                      }
                    },
                    padding: EdgeInsets.zero,
                    child: Container(
                        decoration: BoxDecoration(
                            color: Colors.white,
                            borderRadius: BorderRadius.circular(10)),
                        child: Icon(Icons.remove)),
                  ),
                  Text(points.toString()),
                  FlatButton(
                    onPressed: () {
                      if (points < 160) {
                        setState(() {
                          points += 10;
                        });
                      }
                    },
                    padding: EdgeInsets.zero,
                    child: Container(
                        decoration: BoxDecoration(
                            color: Colors.white,
                            borderRadius: BorderRadius.circular(10)),
                        child: Icon(Icons.add)),
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
                  GestureDetector(
                    onTap: () {
                      widget.onBid(SimpleBid(
                          points: points,
                          color: _cardColor,
                          position: widget.myPosition));
                    },
                    child: Container(
                      padding: EdgeInsets.all(8),
                      child: NeumorphicWidget(
                        sizeShadow: SizeShadow.SMALL,
                        child: Padding(
                          padding: const EdgeInsets.all(8.0),
                          child: Text("Bid"),
                        ),
                        borderRadius: 5,
                      ),
                    ),
                  )
                ],
              ),
            ),
          ),
          GestureDetector(
            onTap: () {
              widget.onBid(Capot(
                  position: widget.myPosition,
                  color: _cardColor,
                  belote: false));
            },
            child: NeumorphicWidget(
              sizeShadow: SizeShadow.SMALL,
              borderRadius: 5,
              child: Padding(
                padding: const EdgeInsets.all(8.0),
                child: Text("Capot"),
              ),
            ),
          )
        ],
      ),
    );
  }
}
