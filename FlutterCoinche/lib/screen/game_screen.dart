import 'package:FlutterCoinche/bloc/games_bloc.dart';
import 'package:FlutterCoinche/dto/game.dart';
import 'package:FlutterCoinche/provider/bloc_provider.dart';
import 'package:FlutterCoinche/screen/all_games_screen.dart';
import 'package:flutter/material.dart';

class GameScreen extends StatelessWidget {
  static const routeName = "/game";

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
            stream: BlocProvider.of<GamesBloc>(context).gameBehavior,
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
              return Container(
                child: Text(snapshot.data.id),
              );
            }));
  }
}
