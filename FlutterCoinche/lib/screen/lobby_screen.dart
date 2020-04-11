import 'package:FlutterCoinche/screen/argument/game_arguments.dart';
import 'package:FlutterCoinche/screen/game_screen.dart';
import 'package:cloud_firestore/cloud_firestore.dart';
import 'package:flutter/material.dart';

class LobbyScreen extends StatelessWidget {
  static const routeName = "/lobby";

  @override
  Widget build(BuildContext context) {
    final GameArguments args = ModalRoute.of(context).settings.arguments;
    var game = args.game;
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
      body: StreamBuilder<DocumentSnapshot>(
          stream: Firestore.instance
              .collection('playersSets')
              .document(game.id)
              .collection('players')
              .document('uid')
              .snapshots(),
          builder:
              (BuildContext context, AsyncSnapshot<DocumentSnapshot> snapshot) {
            if (snapshot.hasError) {
              return Text("Error: ${snapshot.error}");
            }
            if (!snapshot.hasData) {
              return Center(
                child: Text("No data"),
              );
            }
            return Container(
              child: Text("Lobby of game ${snapshot.data.data}"),
            );
          }),
    );
  }
}
