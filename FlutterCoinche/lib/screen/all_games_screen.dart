import 'package:FlutterCoinche/bloc/games_bloc.dart';
import 'package:FlutterCoinche/dto/game_empty.dart';
import 'package:FlutterCoinche/resources/colors.dart';
import 'package:FlutterCoinche/rest/server_communication.dart';
import 'package:FlutterCoinche/screen/lobby_screen.dart';
import 'package:FlutterCoinche/ui/inner_shadow.dart';
import 'package:FlutterCoinche/widget/neu_round_inset.dart';
import 'package:FlutterCoinche/widget/neumorphic_container.dart';
import 'package:bloc_provider/bloc_provider.dart';
import 'package:flushbar/flushbar.dart';
import 'package:flushbar/flushbar_helper.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';

class AllGamesScreen extends StatelessWidget {
  static const routeName = "/allGames";
  final TextEditingController _controller = TextEditingController();

  @override
  Widget build(BuildContext context) {
    var formKey = GlobalKey<FormState>();
    var gamesProvider = BlocProvider.of<GamesBloc>(context);
    ServerCommunication.allGames()
        .then((value) => gamesProvider.addAllMyGames(value));
    return Scaffold(
      floatingActionButton: NeuRoundInset(
        onTap: () {
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
          color: colorTextDark,
        ),
      ),
      appBar: AppBar(
        title: Text("All games"),
        actions: <Widget>[
          IconButton(
              icon: Icon(Icons.refresh),
              onPressed: () {
                ServerCommunication.allGames().then((value) {
                  gamesProvider.addAllMyGames(value);
                });
              })
        ],
      ),
      body: SafeArea(
          child: Container(
        color: colorGradient1,
        child: StreamBuilder<List<GameEmpty>>(
          stream: gamesProvider.allGames,
          builder: (context, AsyncSnapshot<List<GameEmpty>> snapshot) {
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
                GameEmpty game = games[index];
                return Padding(
                  padding: const EdgeInsets.symmetric(
                      vertical: 14.0, horizontal: 16),
                  child: NeumorphicWidget(
                    onTap: () {
                      FlushbarHelper.createInformation(message: "todo");
                      if (!game.inRoom) {
                        // join game
                        ServerCommunication.joinGame(
                          gameId: game.id,
                        ).then((_) {
                          gamesProvider.changeGame(game.id);
                          Navigator.of(context)
                              .pushNamed(LobbyScreen.routeName);
                        }, onError: (error) {
                          FlushbarHelper.createError(
                                  message:
                                      "Cannot join game ${game.id}: $error",
                                  duration: Duration(seconds: 4))
                              .show(context);
                        });
                      } else {
                        gamesProvider.changeGame(game.id);
                        Navigator.of(context).pushNamed(LobbyScreen.routeName);
                      }
                    },
                    child: ListTile(
                      title: Text(
                        "Game: ${game.name}",
                        style: TextStyle(
                            color: colorTextDark,
                            fontSize: 18,
                            fontWeight: FontWeight.bold),
                      ),
                      leading: InnerShadow(
                        color: colorShadow,
                        blur: 2,
                        offset: Offset(2, 2),
                        child: InnerShadow(
                          blur: 2,
                          color: Colors.white,
                          offset: Offset(-2, -2),
                          child: Container(
                              padding: EdgeInsets.all(18),
                              decoration: BoxDecoration(
                                  shape: BoxShape.circle,
                                  color: colorGradient2,
//                                  borderRadius: BorderRadius.circular(50)
                              ),
                              child: Text(
                                game.state.toString().split(".").last,
                                style: TextStyle(color: colorTextDark),
                              )),
                        ),
                      ),
                      subtitle: Text("Created by ${game.nicknameCreator}"),
                      trailing: Row(
                        mainAxisSize: MainAxisSize.min,
                        crossAxisAlignment: CrossAxisAlignment.center,
                        mainAxisAlignment: MainAxisAlignment.end,
                        children: [
                          Text(
                            game.nbJoined.toString(),
                            style: TextStyle(color: colorText, fontSize: 18),
                          ),
                          SizedBox(
                            width: 2,
                          ),
                          Icon(game.nbJoined > 1 ? Icons.people : Icons.person),
                          SizedBox(
                            width: 20,
                          ),
                          Container(
                            width: 10,
                            height: 10,
                            decoration: BoxDecoration(
                                color:
                                    game.inRoom ? Colors.blue : Colors.blueGrey,
                                borderRadius: BorderRadius.circular(5)),
                          ),
                          SizedBox(
                            width: 10,
                          ),
                        ],
                      ),
                    ),
                  ),
                );
              },
            );
          },
        ),
      )),
    );
  }
}
