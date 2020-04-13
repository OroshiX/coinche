import 'package:FlutterCoinche/bloc/games_bloc.dart';
import 'package:FlutterCoinche/dto/card.dart' as card;
import 'package:FlutterCoinche/dto/card.dart';
import 'package:FlutterCoinche/dto/game.dart';
import 'package:FlutterCoinche/provider/bloc_provider.dart';
import 'package:FlutterCoinche/resources/colors.dart';
import 'package:FlutterCoinche/screen/all_games_screen.dart';
import 'package:FlutterCoinche/widget/table_widget.dart';
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
    return Scaffold(
      appBar: AppBar(
        actions: <Widget>[
          FlatButton(
              onPressed: () {
                Navigator.of(context)
                    .pushReplacementNamed(AllGamesScreen.routeName);
              },
              child: Text("Games"))
        ],
        title: Text("Game"),
      ),
      body: StreamBuilder<Game>(
          stream: gamesBloc.gameBehavior,
          builder: (context, snapshot) {
            print("data has changed: $snapshot");
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
            return TableWidget(snapshot.data);
          }),
      backgroundColor: colorLightBlue,
      persistentFooterButtons: <Widget>[
         FlatButton(
            onPressed: () {
              setState(() {
                myCards.sort((card.Card c1, card.Card c2) {
                  if (c1.color.index < c2.color.index) return -1;
                  if (c1.color.index > c2.color.index) return 1;
                  return compareValue(c1.value, c2.value, false);
                });
              });
            },
            child: Text("Sort"))
      ],
    );
  }
}
