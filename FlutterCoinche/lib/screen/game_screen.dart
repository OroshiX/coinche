import 'package:FlutterCoinche/bloc/games_bloc.dart';
import 'package:FlutterCoinche/dto/card.dart';
import 'package:FlutterCoinche/dto/game.dart';
import 'package:FlutterCoinche/resources/colors.dart';
import 'package:FlutterCoinche/widget/game_inherited.dart';
import 'package:FlutterCoinche/widget/table_widget.dart';
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
          child: StreamBuilder<Game>(
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
                print("data has changed: ${snapshot.data}");
                return GameInherited(
                    game: snapshot.data,
                    child: TableWidget(
                      quit: _quit,
                    ));
              }),
        ),
      ),
    );
  }
}
