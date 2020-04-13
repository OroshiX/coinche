import 'package:FlutterCoinche/dto/bid.dart';
import 'package:FlutterCoinche/dto/card.dart' as cardModel;
import 'package:FlutterCoinche/dto/player_position.dart';
import 'package:FlutterCoinche/widget/inkwell_round_icon_button.dart';
import 'package:FlutterCoinche/widget/neumorphic_container.dart';
import 'package:flutter/material.dart';

class BiddingBar extends StatefulWidget {
  final double screenWidth;
  final int minBidPoints;
  final void Function(Bid bid) onBid;
  final double height;
  final PlayerPosition myPosition;
  final bool enabled;

  const BiddingBar(
      {Key key,
      @required this.screenWidth,
      this.onBid,
      this.minBidPoints = 80,
      @required this.myPosition,
      this.height,
      this.enabled})
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
    points = widget.minBidPoints < 160 ? widget.minBidPoints : 160;
    _cardColor = cardModel.CardColor.HEART;
  }

  @override
  Widget build(BuildContext context) {
    return Container(
      color: Colors.white,
      width: widget.screenWidth,
      height: widget.height != null ? widget.height : 70,
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
                  Padding(padding: const EdgeInsets.only(left: 8)),
                  Padding(
                    padding: const EdgeInsets.symmetric(horizontal: 8),
                    child: InkwellNeumorphicButton.round(
                      onTap: () {
                        if (points > widget.minBidPoints) {
                          setState(() {
                            points -= 10;
                          });
                        }
                      },
                      child: Padding(
                        padding: const EdgeInsets.all(2),
                        child: Icon(Icons.remove),
                      ),
                    ),
                  ),
                  Text(points.toString()),
                  Padding(
                    padding: const EdgeInsets.symmetric(horizontal: 8),
                    child: InkwellNeumorphicButton.round(
                      child: Padding(
                        padding: const EdgeInsets.all(2.0),
                        child: Icon(Icons.add),
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
                  InkwellNeumorphicButton.rrect(
                    onTap: () {
                      widget.onBid(SimpleBid(
                          points: points,
                          color: _cardColor,
                          position: widget.myPosition));
                    },
                    borderRadius: 5,
                    enabled: widget.enabled,
                    child: Padding(
                      padding: const EdgeInsets.all(8),
                      child: Text("Bid"),
                    ),
                  ),
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
