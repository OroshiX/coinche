import 'package:FlutterCoinche/dto/bid.dart';
import 'package:FlutterCoinche/dto/table_state.dart';
import 'package:FlutterCoinche/resources/colors.dart';
import 'package:FlutterCoinche/widget/game_inherited.dart';
import 'package:FlutterCoinche/widget/neumorphic_container.dart';
import 'package:FlutterCoinche/widget/only_score.dart';
import 'package:flutter/material.dart';

class LandscapeScoreWidget extends StatelessWidget {
  final Function onTapExit, onTapMessages;

  const LandscapeScoreWidget(
      {@required this.onTapExit, @required this.onTapMessages});

  @override
  Widget build(BuildContext context) {
    final state =
        GameInherited.of(context, aspectType: Aspects.STATE).game.state;
    final Bid currentBidGame =
        GameInherited.of(context, aspectType: Aspects.CURRENT_BID)
            .game
            .currentBid;
    final Bid currentBid = state != TableState.PLAYING ? null : currentBidGame;

    const iconSize = 20.0;
    const paddingButton = 8.0;
    const distanceButtons = 10.0;
    const topBottomButtons = 4.0;
    const minWidth =
        paddingButton * 2 + iconSize * 2 + distanceButtons + topBottomButtons * 2;
    return IntrinsicHeight(
      child: Row(
        mainAxisAlignment: MainAxisAlignment.start,
        crossAxisAlignment: CrossAxisAlignment.stretch,
        mainAxisSize: MainAxisSize.min,
        children: [
          Padding(
            padding: const EdgeInsets.only(
                bottom: topBottomButtons,
                top: topBottomButtons,
                left: 4,
                right: 4),
            child: Column(
              mainAxisAlignment: MainAxisAlignment.spaceAround,
              crossAxisAlignment: CrossAxisAlignment.center,
              children: [
                NeumorphicWidget(
                  onTap: onTapExit,
                  sizeShadow: SizeShadow.SMALL,
                  child: Padding(
                    padding: const EdgeInsets.all(paddingButton),
                    child: Icon(
                      Icons.exit_to_app,
                      size: iconSize,
                      color: colorTextDark,
                    ),
                  ),
                ),
                SizedBox(
                  height: distanceButtons,
                ),
                NeumorphicWidget(
                  onTap: onTapMessages,
                  sizeShadow: SizeShadow.SMALL,
                  child: Padding(
                    padding: const EdgeInsets.all(paddingButton),
                    child: Icon(
                      Icons.message,
                      size: iconSize,
                      color: colorTextDark,
                    ),
                  ),
                )
              ],
            ),
          ),
          Padding(
            padding:
                const EdgeInsets.only(left: 4.0, right: 4, top: 4, bottom: 4),
            child: OnlyScoreWidget(
              currentBid: currentBid,
              minWidth: minWidth,
            ),
          )
        ],
      ),
    );
  }
}
