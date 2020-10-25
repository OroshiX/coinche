import 'package:coinche/domain/dto/table_state.dart';
import 'package:coinche/state/game_model.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

class WaitingPlayersWidget extends StatelessWidget {
  const WaitingPlayersWidget({Key key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Selector<GameModel, TableState>(
      selector: (context, gameModel) => gameModel.game.state,
      builder: (context, state, child) {
        final joining = state == TableState.JOINING;
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
