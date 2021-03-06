import 'package:coinche/domain/dto/table_state.dart';
import 'package:coinche/state/game_model.dart';
import 'package:coinche/theme/dimens.dart';
import 'package:coinche/ui/widget/bidding_bar/bidding_bar.dart';
import 'package:coinche/util/flush_util.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:tuple/tuple.dart';

class AnimatedBiddingBar extends StatelessWidget {
  final Size screenSize;

  final double widthContainerName;

  const AnimatedBiddingBar(
      {Key? key, required this.screenSize, required this.widthContainerName})
      : super(key: key);

  @override
  Widget build(BuildContext context) {
    final key = GlobalKey<BiddingBarState>();

    return Selector<GameModel, Tuple2<bool, String?>>(
      // bidding? , success, error
      selector: (ctx, gm) {
        // if (gm.game.state == TableState.PLAYING) {
        //   key.currentState.minPoints = 80;
        //   key.currentState.points = 80;
        // }
        return Tuple2(
            gm.game.state == TableState.bidding, gm.error);
      },
      builder: (context, value, child) {
        final error = value.item2;
        final bool isBidding = value.item1;
        if (error != null) {
          WidgetsBinding.instance?.addPostFrameCallback((timeStamp) {
            FlushUtil.showError(context, "Error placing bid: $error");
            context.read<GameModel>().clearError();
          });
        }
        return AnimatedPositioned(
          bottom: getBottomOfBiddingBar(screenSize),
          left: isBidding
              ? screenSize.width / 2 - widthContainerName * 2
              : -screenSize.width,
          duration: Duration(milliseconds: 500),
          child: AnimatedOpacity(
            duration: Duration(milliseconds: 500),
            opacity: isBidding ? 1 : 0,
            child: BiddingBarProvided(
              key: key,
            ),
          ),
        );
      },
    );
  }
}
