import 'package:FlutterCoinche/bloc/games_bloc.dart';
import 'package:FlutterCoinche/dto/card.dart' as card;
import 'package:FlutterCoinche/dto/game.dart';
import 'package:FlutterCoinche/resources/colors.dart';
import 'package:FlutterCoinche/screen/all_games_screen.dart';
import 'package:FlutterCoinche/widget/table_widget.dart';
import 'package:bloc_provider/bloc_provider.dart';
import 'package:flutter/material.dart';

class GameScreen extends StatefulWidget {
  static const routeName = "/game";

  @override
  _GameScreenState createState() => _GameScreenState();
}

class _GameScreenState extends State<GameScreen> {
  List<card.Card> myCards;
  GamesBloc gamesBloc;

  @override
  void initState() {
    super.initState();
    gamesBloc = BlocProvider.of<GamesBloc>(context);
    myCards = [];
  }

  @override
  Widget build(BuildContext context) {
    return WillPopScope(
      onWillPop: () async {
        return (await showDialog(
              context: context,
              builder: (context) => AlertDialog(
                title: Text("Exit game?"),
                actions: [
                  FlatButton(
                      onPressed: () => Navigator.of(context).pop(false),
                      child: Text("No, stay here")),
                  FlatButton(
                      onPressed: () => Navigator.of(context).pop(true),
                      child: Text("Yes, exit!"))
                ],
              ),
            )) ??
            false;
      },
      child: Scaffold(
        appBar: AppBar(
          actions: <Widget>[
            FlatButton(
                onPressed: () {
                  Navigator.of(context)
                      .pushReplacementNamed(AllGamesScreen.routeName);
                },
                child: Text("Games"))
          ],
          title: StreamBuilder<Game>(
              stream: gamesBloc.game,
              builder: (context, snapshot) {
                if (snapshot.hasError || !snapshot.hasData) return Text("Game");
                return Text("${snapshot.data.id}");
              }),
        ),
        body: StreamBuilder<Game>(
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
              return TableWidget(snapshot.data);
            }),
        backgroundColor: colorLightBlue,
      ),
    );
  }
}
