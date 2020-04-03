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
        body: Container(
          child: Text("Bidding"),
        ));
  }
}
