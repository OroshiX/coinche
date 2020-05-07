import 'package:FlutterCoinche/domain/dto/card.dart';
import 'package:FlutterCoinche/domain/dto/game.dart';
import 'package:FlutterCoinche/domain/dto/player_position.dart';
import 'package:FlutterCoinche/domain/dto/pos_table_to_colors.dart';
import 'package:FlutterCoinche/state/game_inherited.dart';
import 'package:FlutterCoinche/state/games_bloc.dart';
import 'package:FlutterCoinche/ui/resources/colors.dart';
import 'package:FlutterCoinche/ui/widget/table_widget.dart';
import 'package:bloc_provider/bloc_provider.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

class GameScreen extends StatefulWidget {
  static const routeName = "/game";

  @override
  _GameScreenState createState() => _GameScreenState();
}

class _GameScreenState extends State<GameScreen> {
  List<CardModel> myCards;
  GamesBloc gamesBloc;

  @override
  void initState() {
    super.initState();
    gamesBloc = BlocProvider.of<GamesBloc>(context);
    myCards = [];
  }

  Future _quit() {
    return showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: Text("Exit game?"),
        actions: [
          FlatButton(
              onPressed: () => Navigator.of(context).pop(false),
              child: Text("No, stay here")),
          FlatButton(
              onPressed: () {
                Navigator.of(context).pop(true);
                SystemChrome.restoreSystemUIOverlays();
              },
              child: Text("Yes, exit!"))
        ],
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    SystemChrome.setEnabledSystemUIOverlays([]);
    return Material(
      child: WillPopScope(
        onWillPop: () async {
          return (await _quit()) ?? false;
        },
        child: Container(
          color: colorLightBlue,
          child: FutureBuilder<PosTableToColor>(
            future: getPosTableToColors(),
            builder: (ctx, snapshotMap) => StreamBuilder<Game>(
                stream: gamesBloc.game,
                builder: (context, snapshot) {
                  if (snapshot.hasError) {
                    return Center(
                      child: Text("Error: ${snapshot.error}"),
                    );
                  }
                  if (!snapshot.hasData) {
                    return Center(
                      child: Text("No data"),
                    );
                  }
                  return GameInherited(
                    game: snapshot.data,
                    map: snapshotMap.data.value,
                    child: TableWidget(
                      quit: _quit,
                    ),
                  );
                }),
          ),
        ),
      ),
    );
  }
}
