import 'package:FlutterCoinche/domain/dto/game_empty.dart';
import 'package:FlutterCoinche/service/network/fire_auth_service.dart';
import 'package:FlutterCoinche/service/network/server_communication.dart';
import 'package:FlutterCoinche/state/games_bloc.dart';
import 'package:FlutterCoinche/ui/resources/colors.dart';
import 'package:FlutterCoinche/ui/screen/all_games/alert_new_game.dart';
import 'package:FlutterCoinche/ui/screen/all_games/list_games.dart';
import 'package:FlutterCoinche/ui/screen/login_screen.dart';
import 'package:FlutterCoinche/ui/screen/testing_offline_game.dart';
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
    _refreshData(gamesProvider, context);
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
              icon: Icon(Icons.cake),
              onPressed: () {
                Navigator.of(context).pushNamed(TestingOfflineGame.routeName);
              }),
          IconButton(
              icon: Icon(Icons.exit_to_app),
              onPressed: () {
                getFireAuthService().signOut(context).then(
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

            return ListGames(
              gamesProvider: gamesProvider,
              games: snapshot.data,
              onRefresh: _refreshData,
            );
          },
        ),
      )),
    );
  }

  Future<void> _refreshData(GamesBloc gamesProvider, BuildContext context) =>
      ServerCommunication.allGames().then(
          (value) => gamesProvider.addAllMyGames(value),
          onError: (error) =>
              FlushbarHelper.createError(message: "Error getting games: $error")
                  .show(context));
}
