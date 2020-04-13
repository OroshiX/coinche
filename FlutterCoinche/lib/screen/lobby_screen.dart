import 'package:FlutterCoinche/bloc/games_bloc.dart';
import 'package:FlutterCoinche/dto/game.dart';
import 'package:FlutterCoinche/screen/game_screen.dart';
import 'package:bloc_provider/bloc_provider.dart';
import 'package:flutter/material.dart';

class LobbyScreen extends StatelessWidget {
  static const routeName = "/lobby";

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text("Lobby"),
        actions: <Widget>[
          FlatButton(
              onPressed: () {
                Navigator.of(context).pushNamed(GameScreen.routeName);
              },
              child: Text("Play"))
        ],
      ),
      body: StreamBuilder<Game>(
          stream: BlocProvider.of<GamesBloc>(context).game,
          builder: (BuildContext context, AsyncSnapshot<Game> snapshot) {
            if (snapshot.hasError) {
              return Text("Error: ${snapshot.error}");
            }
            if (!snapshot.hasData) {
              return Center(
                child: Text("No data"),
              );
            }
            return Container(
              child: Text("Lobby of game ${snapshot.data.id}"),
            );
          }),
    );
  }
}
