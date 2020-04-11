import 'package:FlutterCoinche/dto/game.dart';
import 'package:FlutterCoinche/rest/server_communication.dart';
import 'package:FlutterCoinche/screen/lobby_screen.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';

class AllGamesScreen extends StatelessWidget {
  static const routeName = "/allGames";

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text("All games"),
        actions: <Widget>[
          FlatButton(
              onPressed: () {
                Navigator.of(context)
                    .pushReplacementNamed(LobbyScreen.routeName);
              },
              child: Text("Lobby"))
        ],
      ),
      body: SafeArea(
          child: FutureBuilder<List<Game>>(
        future: ServerCommunication.allGames(),
        builder: (context, snapshot) {
          if (snapshot.hasError) {
            return Center(
              child: Text("Error: ${snapshot.error}"),
            );
          }
          if (!snapshot.hasData || snapshot.data.isEmpty) {
            return Center(
              child: Text("No data"),
            );
          }
          var games = snapshot.data;
          return ListView.builder(
            itemCount: games.length,
            itemBuilder: (context, index) {
              Game game = games[index];
              return ListTile(
                title: Text("Game: ${game.name}"),
                subtitle: Text("Nb joined: ${game.nbJoined}"),
              );
            },
          );
        },
      )),
    );
  }
}
