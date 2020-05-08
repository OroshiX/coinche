import 'package:FlutterCoinche/domain/dto/game_empty.dart';
import 'package:FlutterCoinche/state/games_bloc.dart';
import 'package:FlutterCoinche/ui/screen/all_games/one_game.dart';
import 'package:FlutterCoinche/ui/screen/stated_game_screen.dart';
import 'package:FlutterCoinche/ui/widget/neumorphic_container.dart';
import 'package:flutter/material.dart';

class InRoomGame extends StatelessWidget {
  final GameEmpty game;

  final GamesBloc gamesProvider;

  const InRoomGame({Key key, this.game, this.gamesProvider}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 14.0, horizontal: 16),
      child: NeumorphicWidget(
        onTap: () {
          gamesProvider.changeGame(game.id);
          Navigator.of(context).pushNamed(StatedGameScreen.routeName);
        },
        child: OneGame(
          game: game,
        ),
      ),
    );
  }
}
