import 'package:FlutterCoinche/domain/dto/bid.dart';
import 'package:FlutterCoinche/domain/dto/game.dart';
import 'package:FlutterCoinche/domain/dto/table_state.dart';
import 'package:FlutterCoinche/domain/extensions/game_extensions.dart';
import 'package:FlutterCoinche/ui/resources/colors.dart';
import 'package:FlutterCoinche/ui/widget/neumorphic_container.dart';
import 'package:FlutterCoinche/ui/widget/only_score.dart';
import 'package:flutter/material.dart';
import 'package:states_rebuilder/states_rebuilder.dart';

class PortraitScoreWidget extends StatelessWidget {
  final Function onTapExit, onTapMessages;

  const PortraitScoreWidget({
    @required this.onTapExit,
    @required this.onTapMessages,
  });

  @override
  Widget build(BuildContext context) {
    const iconSize = 20.0;
    const paddingButton = 8.0;
    const distanceButtons = 10.0;
    const leftRightButtons = 4.0;
    return StateBuilder<Game>(
      models: [RM.get<Game>()],
      tag: [Aspects.STATE, Aspects.CURRENT_BID],
      builder: (context, model) {
        if (model.state == null || model.hasError) return Text("error");
        final state = model.state.state;
        final Bid currentBidGame = model.state.currentBid;
        final Bid currentBid =
            state != TableState.PLAYING ? null : currentBidGame;
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
                      width: distanceButtons,
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
                padding: const EdgeInsets.only(left: 4.0, right: 4),
                child: OnlyScoreWidget(
                  currentBid: currentBid,
                  minWidth: 0,
                ),
              )
            ],
          ),
        );
      },
    );
  }
}
