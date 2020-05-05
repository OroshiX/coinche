import 'package:FlutterCoinche/dto/table_state.dart';
import 'package:FlutterCoinche/widget/game_inherited.dart';
import 'package:flutter/material.dart';

class WaitingPlayersWidget extends StatelessWidget {
  const WaitingPlayersWidget({Key key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    final joining =
        GameInherited.of(context, aspectType: Aspects.STATE).game.state ==
            TableState.JOINING;
    return AnimatedOpacity(
      opacity: joining ? 1 : 0,
      duration: Duration(milliseconds: 400),
      child: Center(
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            Text("Players are joining this game..."),
            SizedBox(
              height: 10,
            ),
            CircularProgressIndicator(),
          ],
        ),
      ),
    );
  }
}
