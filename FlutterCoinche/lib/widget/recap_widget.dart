import 'package:FlutterCoinche/dto/bid.dart';
import 'package:FlutterCoinche/dto/player_position.dart';
import 'package:FlutterCoinche/dto/table_state.dart';
import 'package:FlutterCoinche/widget/neumorphic_container.dart';
import 'package:flutter/material.dart';

class RecapWidget extends StatelessWidget {
  final TableState state;
  final PlayerPosition whoseTurn;

  final Bid bid;

  const RecapWidget(
      {Key key, @required this.state, this.bid, @required this.whoseTurn})
      : super(key: key);

  @override
  Widget build(BuildContext context) {
    return NeumorphicWidget(
      borderRadius: 10,
      sizeShadow: SizeShadow.MEDIUM,
      child: Padding(
        padding: const EdgeInsets.all(8.0),
        child: Column(
          children: <Widget>[
            Text("Phase: ${state.toString().split(".").last.toLowerCase()}"),
            if (state == TableState.PLAYING) Text("${bid.toString()}"),
            if (state == TableState.PLAYING || state == TableState.BIDDING)
              Text(
                  "${whoseTurn.toString().split(".").last}'s turn to ${state == TableState.PLAYING ? "play" : state == TableState.BIDDING ? "bid" : ""}")
          ],
        ),
      ),
    );
  }
}
