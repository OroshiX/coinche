import 'package:FlutterCoinche/service/network/fire_auth_service.dart';
import 'package:FlutterCoinche/state/game_model.dart';
import 'package:FlutterCoinche/ui/resources/colors.dart';
import 'package:FlutterCoinche/ui/screen/all_games/alert_new_game.dart';
import 'package:FlutterCoinche/ui/screen/all_games/list_games.dart';
import 'package:FlutterCoinche/ui/screen/login_screen.dart';
import 'package:FlutterCoinche/ui/screen/testing_offline_game.dart';
import 'package:FlutterCoinche/ui/widget/neu_round_inset.dart';
import 'package:flushbar/flushbar_helper.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

class AllGamesScreen extends StatelessWidget {
  static const routeName = "/allGames";

  @override
  Widget build(BuildContext context) {
    _refreshData(context);
    return Selector<GameModel, String>(
      selector: (ctx, gameModel) => gameModel.error,
      builder: (context, value, child) {
        if (value != null) {
          String error = value;
          WidgetsBinding.instance.addPostFrameCallback((timeStamp) {
            FlushbarHelper.createError(message: "Error getting games: $error")
                .show(context);
            context.read<GameModel>().clearError();
          });
        }
        return Scaffold(
          floatingActionButton: NeuRoundInset(
            onTap: () {
              showDialog(
                context: context,
                builder: (context) => DialogNewGame(),
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
                    Navigator.of(context)
                        .pushNamed(TestingOfflineGame.routeName);
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
                  child: Consumer<GameModel>(
                    builder: (context, value, child) {
                      if (value.allGames.isEmpty) {
                        return Center(
                          child: Text("No games"),
                        );
                      }
                      return ListGames(
                        games: value.allGames,
                        onRefresh: _refreshData,
                      );
                    },
                  ))),
        );
      },
    );
  }

  Future<void> _refreshData(BuildContext context) {
    return context.read<GameModel>().refreshGames();
  }
}
