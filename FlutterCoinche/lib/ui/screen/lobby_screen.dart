import 'package:FlutterCoinche/state/games_bloc.dart';
import 'package:FlutterCoinche/domain/dto/game.dart';
import 'package:FlutterCoinche/domain/dto/table_state.dart';
import 'package:FlutterCoinche/ui/screen/stated_game_screen.dart';
import 'package:bloc_provider/bloc_provider.dart';
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
                Navigator.of(context)
                    .pushReplacementNamed(StatedGameScreen.routeName);
              },
              child: Text("Play"))
        ],
      ),
      body: StreamBuilder<Game>(
          stream: BlocProvider.of<GamesBloc>(context).game,
          builder: (BuildContext context, AsyncSnapshot<Game> snapshot) {
            if (snapshot.hasError) {
              return Text("Error: ${snapshot.error}");
            }
            if (!snapshot.hasData) {
              return Center(
                child: Text("No data"),
              );
            }
            if (snapshot.data.state != TableState.JOINING) {
              WidgetsBinding.instance.addPostFrameCallback((_) {
                Navigator.of(context)
                    .pushReplacementNamed(StatedGameScreen.routeName);
              });
              return SizedBox();
            }
            return Container(
              child: Center(
                  child: Column(
                      mainAxisSize: MainAxisSize.min,
                      crossAxisAlignment: CrossAxisAlignment.center,
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                    CircularProgressIndicator(
                      strokeWidth: 5,
                    ),
                    Text(
                        "Waiting for additional players... ${snapshot.data.id}"),
                  ])),
            );
          }),
    );
  }
}
