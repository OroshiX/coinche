import 'package:coinche/domain/dto/game_empty.dart';
import 'package:coinche/state/game_model.dart';
import 'package:coinche/state/login_model.dart';
import 'package:coinche/ui/screen/all_games/one_game.dart';
import 'package:coinche/ui/screen/game/game_screen_provided.dart';
import 'package:coinche/ui/widget/neumorphic_container.dart';
import 'package:coinche/util/flush_util.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

class JoinGame extends StatelessWidget {
  final GameEmpty game;

  const JoinGame({Key key, this.game}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 14.0, horizontal: 16),
      child: NeumorphicWidget(
        onTap: () {
          // join game
          context.read<GameModel>().joinGame(
                gameId: game.id,
                userUid: context.read<LoginModel>().user.uid,
                onSuccess: () {
                  Navigator.of(context).pushNamed(GameScreenProvided.routeName);
                },
                onError: (message) {
                  FlushUtil.showError(
                      context, "Cannot join game ${game.id}: $message");
                },
              );
        },
        child: OneGame(
          game: game,
        ),
      ),
    );
  }
}
