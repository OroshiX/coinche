import 'package:FlutterCoinche/domain/dto/game_empty.dart';
import 'package:FlutterCoinche/service/network/server_communication.dart';
import 'package:FlutterCoinche/state/games_bloc.dart';
import 'package:FlutterCoinche/ui/screen/all_games/one_game.dart';
import 'package:FlutterCoinche/ui/screen/stated_game_screen.dart';
import 'package:FlutterCoinche/ui/widget/neumorphic_container.dart';
import 'package:flushbar/flushbar_helper.dart';
import 'package:flutter/material.dart';

class JoinGame extends StatelessWidget {
  final GameEmpty game;

  final GamesBloc gamesProvider;

  const JoinGame({Key key, this.game, this.gamesProvider}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 14.0, horizontal: 16),
      child: NeumorphicWidget(
        onTap: () {
          // join game
          ServerCommunication.joinGame(
            gameId: game.id,
          ).then((_) {
            gamesProvider.changeGame(game.id);
            Navigator.of(context).pushNamed(StatedGameScreen.routeName);
          }, onError: (error) {
            FlushbarHelper.createError(
                    message: "Cannot join game ${game.id}: $error",
                    duration: Duration(seconds: 4))
                .show(context);
          });
        },
        child: OneGame(
          game: game,
        ),
      ),
    );
  }
}
