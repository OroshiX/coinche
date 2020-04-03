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
        child: Center(
          child: Container(
            color: Colors.green,
            child: Text("Success"),
          ),
        ),
      ),
    );
  }
}
