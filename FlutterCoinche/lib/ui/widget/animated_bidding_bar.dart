import 'package:FlutterCoinche/domain/dto/bid.dart';
import 'package:FlutterCoinche/domain/dto/table_state.dart';
import 'package:FlutterCoinche/service/network/server_communication.dart';
import 'package:FlutterCoinche/ui/resources/dimens.dart';
import 'package:FlutterCoinche/ui/widget/bidding_bar.dart';
import 'package:FlutterCoinche/state/game_inherited.dart';
import 'package:flushbar/flushbar_helper.dart';
import 'package:flutter/material.dart';

class AnimatedBiddingBar extends StatelessWidget {
  final Size screenSize;

  final double widthContainerName;

  const AnimatedBiddingBar(
      {Key key, @required this.screenSize, @required this.widthContainerName})
      : super(key: key);

  @override
  Widget build(BuildContext context) {
    final state =
        GameInherited.of(context, aspectType: Aspects.STATE).game.state;
    final id = GameInherited.of(context, aspectType: Aspects.ID).game.id;

    return AnimatedPositioned(
      bottom: getBottomOfBiddingBar(screenSize),
      left: state == TableState.BIDDING
          ? screenSize.width / 2 - widthContainerName * 2
          : -screenSize.width,
      duration: Duration(milliseconds: 500),
      child: AnimatedOpacity(
        duration: Duration(milliseconds: 500),
        opacity: state == TableState.BIDDING ? 1 : 0,
        child: BiddingBar(
//                height: heightBiddingBar,
          onBid: (Bid bid) {
            ServerCommunication.bid(bid, id).then((success) {
              print("success");
              FlushbarHelper.createSuccess(message: "bid $bid placed")
                  .show(context);
            }, onError: (error) {
              print("Error: $error");
              FlushbarHelper.createError(message: "Error placing bid: $error")
                  .show(context);
            });
          },
//                screenWidth: screenSize.width,
        ),
      ),
    );
  }
}
