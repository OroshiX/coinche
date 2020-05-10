import 'package:FlutterCoinche/domain/dto/game_empty.dart';
import 'package:FlutterCoinche/service/network/server_communication.dart';
import 'package:FlutterCoinche/state/games_bloc.dart';
import 'package:FlutterCoinche/ui/resources/colors.dart';
import 'package:FlutterCoinche/ui/resources/dimens.dart';
import 'package:FlutterCoinche/ui/screen/all_games/alert_new_game.dart';
import 'package:FlutterCoinche/ui/screen/all_games/list_games.dart';
import 'package:FlutterCoinche/ui/screen/game/managed_state_card.dart';
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
    _refreshData(gamesProvider);
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
                showDialog(
                  context: context,
                  builder: (context) {
                    return AlertDialog(
                      title: Text("Anim test"),
                      content: Container(
                          width: MediaQuery.of(context).size.width,
                          height: MediaQuery.of(context).size.height,
                          color: colorLightBlue,
                          child: Stack(children: [
                            ManagedStateCard(
                              cardHeight: 50 * golden,
                              cardWidth: 50,
                              axisDirection: AxisDirection.up,
                            )
                          ])),
                    );
                  },
                );
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

  Future<void> _refreshData(GamesBloc gamesProvider) =>
      ServerCommunication.allGames()
          .then((value) => gamesProvider.addAllMyGames(value));
}
