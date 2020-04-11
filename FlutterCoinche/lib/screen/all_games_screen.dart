import 'package:FlutterCoinche/dto/game.dart';
import 'package:FlutterCoinche/rest/server_communication.dart';
import 'package:FlutterCoinche/screen/argument/game_arguments.dart';
import 'package:FlutterCoinche/screen/lobby_screen.dart';
import 'package:flushbar/flushbar.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';

class AllGamesScreen extends StatelessWidget {
  static const routeName = "/allGames";
  final TextEditingController _controller = TextEditingController();

  @override
  Widget build(BuildContext context) {
    var formKey = GlobalKey<FormState>();

    return Scaffold(
      floatingActionButton: FloatingActionButton(
        onPressed: () {
          showDialog(
            context: context,
            builder: (context) => AlertDialog(
              content: Form(
                  key: formKey,
                  child: TextFormField(
                    controller: _controller,
                    validator: (value) => value.isNotEmpty
                        ? null
                        : "Please submit a name for the game",
                  )),
              actions: <Widget>[
                FlatButton(
                    onPressed: () {
                      if (formKey.currentState.validate()) {
                        ServerCommunication.createGame(_controller.text).then(
                            (value) {
                          Navigator.of(context).pushReplacementNamed(
                              LobbyScreen.routeName,
                              arguments: GameArguments(game: value));
                          return Flushbar(
                            message: "YES! (now go to game)",
                          ).show(context);
                        },
                            onError: (error) => Flushbar(
                                  message:
                                      "Oh no! Please check your connection",
                                ).show(context));
                      }
                    },
                    child: Text("OK"))
              ],
            ),
          );
          ServerCommunication.createGame("My awesome Game")
              .then((value) => print("We got game = ${value.toJson()}"));
        },
        child: Icon(
          Icons.add,
          color: Colors.white,
        ),
      ),
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
