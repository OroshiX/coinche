import 'package:FlutterCoinche/dto/table_state.dart';
import 'package:FlutterCoinche/widget/game_inherited.dart';
import 'package:FlutterCoinche/widget/neumorphic_container.dart';
import 'package:FlutterCoinche/widget/neumorphic_no_state.dart';
import 'package:flutter/material.dart';

class RecapWidget extends StatelessWidget {

  const RecapWidget({Key key})
      : super(key: key);

  @override
  Widget build(BuildContext context) {
    final state =
        GameInherited.of(context, aspectType: Aspects.STATE).game.state;
    final whoseTurn = GameInherited.of(context, aspectType: Aspects.NEXT_PLAYER)
        .game
        .nextPlayer;
    return NeumorphicNoStateWidget(
      pressed: false,
      borderRadius: 10,
      sizeShadow: SizeShadow.MEDIUM,
      child: Padding(
        padding: const EdgeInsets.all(8.0),
        child: Column(
          children: <Widget>[
            Text("Phase: ${state.toString().split(".").last.toLowerCase()}"),
            if (state == TableState.PLAYING || state == TableState.BIDDING)
              Text(
                  "${whoseTurn.toString().split(".").last}'s turn to ${state == TableState.PLAYING ? "play" : state == TableState.BIDDING ? "bid" : ""}"),
          ],
        ),
      ),
    );
  }
}
