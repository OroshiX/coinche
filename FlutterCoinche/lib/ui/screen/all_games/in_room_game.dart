import 'package:FlutterCoinche/domain/dto/game_empty.dart';
import 'package:FlutterCoinche/state/game_model.dart';
import 'package:FlutterCoinche/ui/screen/all_games/one_game.dart';
import 'package:FlutterCoinche/ui/screen/game/game_screen_provided.dart';
import 'package:FlutterCoinche/ui/widget/neumorphic_container.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

class InRoomGame extends StatelessWidget {
  final GameEmpty game;

  const InRoomGame({Key key, this.game}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 14.0, horizontal: 16),
      child: NeumorphicWidget(
        onTap: () {
          context.read<GameModel>().changeGame(game.id);
          Navigator.of(context).pushNamed(GameScreenProvided.routeName);
        },
        child: OneGame(
          game: game,
        ),
      ),
    );
  }
}
