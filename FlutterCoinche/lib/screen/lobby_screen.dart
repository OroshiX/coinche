import 'package:FlutterCoinche/screen/game_screen.dart';
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
      body: Container(
        child: Text("Lobby"),
      ),
    );
  }
}
