import 'package:coinche/domain/dto/bid.dart';
import 'package:coinche/domain/dto/table_state.dart';
import 'package:coinche/state/game_model.dart';
import 'package:coinche/ui/resources/colors.dart';
import 'package:coinche/ui/widget/neumorphic_container.dart';
import 'package:coinche/ui/widget/only_score.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

class PortraitScoreWidget extends StatelessWidget {
  final void Function() onTapExit, onTapMessages;

  const PortraitScoreWidget({
    required this.onTapExit,
    required this.onTapMessages,
  });

  @override
  Widget build(BuildContext context) {
    const iconSize = 20.0;
    const paddingButton = 8.0;
    const distanceButtons = 10.0;
    const leftRightButtons = 4.0;

    return IntrinsicWidth(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.start,
        mainAxisSize: MainAxisSize.min,
        crossAxisAlignment: CrossAxisAlignment.stretch,
        children: [
          Padding(
            padding: const EdgeInsets.only(
                bottom: 8.0,
                top: 4,
                left: leftRightButtons,
                right: leftRightButtons),
            child: Row(
              mainAxisAlignment: MainAxisAlignment.spaceAround,
              crossAxisAlignment: CrossAxisAlignment.center,
              children: [
                NeumorphicWidget(
                  onTap: onTapExit,
                  sizeShadow: SizeShadow.small,
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
                  width: distanceButtons,
                ),
                NeumorphicWidget(
                  onTap: onTapMessages,
                  sizeShadow: SizeShadow.small,
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
            padding: const EdgeInsets.only(left: 4.0, right: 4),
            child: Selector<GameModel, Bid?>(
              selector: (ctx, gm) {
                final state = gm.game.state;
                final Bid currentBidGame = gm.game.currentBid;
                final currentBid =
                    state != TableState.playing ? null : currentBidGame;
                return currentBid;
              },
              builder: (context, value, child) => OnlyScoreWidget(
                currentBid: value,
                minWidth: 0,
              ),
            ),
          )
        ],
      ),
    );
  }
}
