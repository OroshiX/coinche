import 'package:coinche/domain/dto/table_state.dart';
import 'package:coinche/state/game_model.dart';
import 'package:coinche/ui/resources/dimens.dart';
import 'package:coinche/ui/widget/bidding_bar/bidding_bar.dart';
import 'package:flushbar/flushbar_helper.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:tuple/tuple.dart';

class AnimatedBiddingBar extends StatelessWidget {
  final Size screenSize;

  final double widthContainerName;

  const AnimatedBiddingBar(
      {Key key, @required this.screenSize, @required this.widthContainerName})
      : super(key: key);

  @override
  Widget build(BuildContext context) {
    final key = GlobalKey<BiddingBarState>();

    return Selector<GameModel, Tuple3<bool, String, String>>(
      // bidding? , success, error
      selector: (ctx, gm) {
        // if (gm.game.state == TableState.PLAYING) {
        //   key.currentState.minPoints = 80;
        //   key.currentState.points = 80;
        // }
        return Tuple3(
            gm.game.state == TableState.BIDDING, gm.success, gm.error);
      },
      builder: (context, value, child) {
        final String success = value.item2;
        final String error = value.item3;
        final bool isBidding = value.item1;
        if (error != null) {
          WidgetsBinding.instance.addPostFrameCallback((timeStamp) {
            FlushbarHelper.createError(message: "Error placing bid: $error")
                .show(context);
            context.read<GameModel>().clearError();
          });
        } else if (success != null) {
          WidgetsBinding.instance.addPostFrameCallback((timeStamp) {
            FlushbarHelper.createSuccess(message: "bid $success placed")
                .show(context);
            context.read<GameModel>().clearSuccess();
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
