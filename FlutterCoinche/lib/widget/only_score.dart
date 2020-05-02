import 'package:FlutterCoinche/dto/bid.dart';
import 'package:FlutterCoinche/dto/score.dart';
import 'package:FlutterCoinche/resources/colors.dart';
import 'package:FlutterCoinche/widget/neumorphic_container.dart';
import 'package:FlutterCoinche/widget/neumorphic_no_state.dart';
import 'package:flutter/material.dart';

class OnlyScoreWidget extends StatelessWidget {
  final Score score;
  final Bid currentBid;

  const OnlyScoreWidget(
      {Key key, @required this.score, @required this.currentBid})
      : assert(currentBid == null ||
            currentBid is SimpleBid ||
            currentBid is Capot ||
            currentBid is General ||
            currentBid is Coinche),
        super(key: key);

  @override
  Widget build(BuildContext context) {
    const textSize = 14.0;
    return NeumorphicNoStateWidget(
      borderRadius: 10,
      sizeShadow: SizeShadow.SMALL,
      pressed: false,
      child: Padding(
        padding: const EdgeInsets.all(8),
        child: Column(
          children: [
            if (currentBid != null) currentBid.getReadableBidRow(textSize),
            if (currentBid != null)
              Padding(
                padding: const EdgeInsets.only(top:4.0, bottom: 4),
                child: Container(
                  color: colorText,
                  height: 1,
                ),
              ),

          ],
        ),
      ),
    );
  }
}
