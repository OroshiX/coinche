import 'package:FlutterCoinche/domain/dto/game_empty.dart';
import 'package:FlutterCoinche/service/network/server_communication.dart';
import 'package:FlutterCoinche/state/games_bloc.dart';
import 'package:FlutterCoinche/ui/resources/colors.dart';
import 'package:FlutterCoinche/ui/screen/all_games/alert_new_game.dart';
import 'package:FlutterCoinche/ui/screen/all_games/in_room_game.dart';
import 'package:FlutterCoinche/ui/screen/all_games/join_game.dart';
import 'package:FlutterCoinche/ui/screen/login_screen.dart';
import 'package:FlutterCoinche/ui/widget/neu_round_inset.dart';
import 'package:bloc_provider/bloc_provider.dart';
import 'package:flushbar/flushbar_helper.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';

class AllGamesScreen extends StatelessWidget {
  static const routeName = "/allGames";

  @override
  Widget build(BuildContext context) {
    var gamesProvider = BlocProvider.of<GamesBloc>(context);
    ServerCommunication.allGames()
        .then((value) => gamesProvider.addAllMyGames(value));
    return Scaffold(
      floatingActionButton: NeuRoundInset(
        onTap: () {
          showDialog(
            context: context,
            builder: (context) => DialogNewGame(gamesProvider: gamesProvider),
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
              }),
          IconButton(
              icon: Icon(Icons.exit_to_app),
              onPressed: () {
                ServerCommunication.logout().then(
                    (value) => Navigator.of(context)
                        .pushReplacementNamed(LoginScreen.routeName),
                    onError: (error) => FlushbarHelper.createError(
                        message: "Couldn't logout: $error"));
              }),
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
            final inRoomGames =
                games.where((element) => element.inRoom).toList();
            final toJoin = games.where((element) => !element.inRoom).toList();
            return ListView.builder(
              itemCount: games.length + 2,
              itemBuilder: (context, index) {
                if (index == 0) {
                  return ListTile(
                    title: Text(
                      "In game",
                      style: TextStyle(color: colorTextDark, fontSize: 20),
                    ),
                  );
                }
                if (index == inRoomGames.length + 1) {
                  return ListTile(
                    title: Text(
                      "Join",
                      style: TextStyle(
                        color: colorTextDark,
                        fontSize: 20,
                      ),
                    ),
                  );
                }
                if (index <= inRoomGames.length) {
                  GameEmpty gameInRoom = inRoomGames[index - 1];
                  return InRoomGame(
                    game: gameInRoom,
                    gamesProvider: gamesProvider,
                  );
                }
                GameEmpty game = toJoin[index - inRoomGames.length - 2];
                return JoinGame(
                  gamesProvider: gamesProvider,
                  game: game,
                );
              },
            );
          },
        ),
      )),
    );
  }
}
