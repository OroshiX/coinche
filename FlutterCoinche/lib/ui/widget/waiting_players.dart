import 'package:FlutterCoinche/domain/dto/game.dart';
import 'package:FlutterCoinche/domain/dto/table_state.dart';
import 'package:FlutterCoinche/domain/extensions/game_extensions.dart';
import 'package:flutter/material.dart';
import 'package:states_rebuilder/states_rebuilder.dart';

class WaitingPlayersWidget extends StatelessWidget {
  const WaitingPlayersWidget({Key key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return StateBuilder<Game>(
      models: [RM.get<Game>()],
      tag: [Aspects.STATE],
      builder: (context, model) {
        final joining = model.state.state == TableState.JOINING;
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
      },
    );
  }
}
