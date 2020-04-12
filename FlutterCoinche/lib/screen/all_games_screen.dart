import 'package:FlutterCoinche/bloc/games_bloc.dart';
import 'package:FlutterCoinche/dto/game_empty.dart';
import 'package:FlutterCoinche/provider/bloc_provider.dart';
import 'package:FlutterCoinche/rest/server_communication.dart';
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
    var gamesProvider = BlocProvider.of<GamesBloc>(context);
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
                          gamesProvider.changeGame(value.id);
                          Navigator.of(context)
                              .pushNamed(LobbyScreen.routeName);
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
        },
        child: Icon(
          Icons.add,
          color: Colors.white,
        ),
      ),
      appBar: AppBar(
        title: Text("All games"),
      ),
      body: SafeArea(
          child: FutureBuilder<List<GameEmpty>>(
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
          return ListView.separated(
            separatorBuilder: (context, index) => Divider(),
            itemCount: games.length,
            itemBuilder: (context, index) {
              GameEmpty game = games[index];
              return GestureDetector(
                onTap: () {
                  gamesProvider.changeGame(game.id);
                  Navigator.of(context).pushNamed(LobbyScreen.routeName);
                },
                child: ListTile(
                  title: Text("Game: ${game.name}"),
                  subtitle: Text("Nb joined: ${game.nbJoined}"),
                ),
              );
            },
          );
        },
      )),
    );
  }
}
