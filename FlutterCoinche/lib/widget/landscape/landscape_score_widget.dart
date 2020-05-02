import 'package:FlutterCoinche/dto/bid.dart';
import 'package:FlutterCoinche/dto/score.dart';
import 'package:FlutterCoinche/resources/colors.dart';
import 'package:FlutterCoinche/widget/neumorphic_container.dart';
import 'package:FlutterCoinche/widget/only_score.dart';
import 'package:flutter/material.dart';

class LandscapeScoreWidget extends StatelessWidget {
  final Function onTapExit, onTapMessages;
  final Score score;
  final Bid currentBid;

  LandscapeScoreWidget(
      {@required this.onTapExit,
      @required this.onTapMessages,
      @required this.score,
      @required this.currentBid});

  @override
  Widget build(BuildContext context) {
    return Container(
      color: Colors.red,
      child: IntrinsicWidth(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.start,
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            Padding(
              padding: const EdgeInsets.only(bottom: 8.0, top: 4),
              child: Row(
                mainAxisAlignment: MainAxisAlignment.spaceAround,
                crossAxisAlignment: CrossAxisAlignment.center,
                children: [
                  NeumorphicWidget(
                    onTap: onTapExit,
                    sizeShadow: SizeShadow.SMALL,
                    child: Padding(
                      padding: const EdgeInsets.all(8.0),
                      child: Icon(
                        Icons.exit_to_app,
                        color: colorTextDark,
                      ),
                    ),
                  ),
                  NeumorphicWidget(
                    onTap: onTapMessages,
                    sizeShadow: SizeShadow.SMALL,
                    child: Padding(
                      padding: const EdgeInsets.all(8.0),
                      child: Icon(
                        Icons.message,
                        color: colorTextDark,
                      ),
                    ),
                  )
                ],
              ),
            ),
            Padding(
              padding: const EdgeInsets.all(8.0),
              child: OnlyScoreWidget(
                score: score,
                currentBid: currentBid,
              ),
            )
          ],
        ),
      ),
    );
  }
}
