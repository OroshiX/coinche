import 'package:FlutterCoinche/bloc/games_bloc.dart';
import 'package:FlutterCoinche/dto/bid.dart';
import 'package:FlutterCoinche/dto/card.dart';
import 'package:FlutterCoinche/dto/game.dart';
import 'package:FlutterCoinche/dto/player_position.dart';
import 'package:FlutterCoinche/widget/game_inherited.dart';
import 'package:bloc_provider/bloc_provider.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:states_rebuilder/states_rebuilder.dart';

class StatedGameScreen extends StatelessWidget {
  static const routeName = "/statedGame";

  @override
  Widget build(BuildContext context) {
    return Material(
      child: WillPopScope(
        onWillPop: () async {
          return (await _quit(context)) ?? false;
        },
        child: Injector(
          inject: [
            Inject.stream(() => BlocProvider.of<GamesBloc>(context).game,
                name: "game"),
            Inject(() => Game()),
            Inject.future(() => getPosTableToColors())
          ],
          initState: () {
            // To be executed in the initState of StatefulWidget
          },
          builder: (BuildContext context) {
            final ReactiveModel<Game> gameFire =
                Injector.getAsReactive<Game>(name: "game");
            final ReactiveModel<Game> game = Injector.getAsReactive<Game>();

            return StateBuilder(
              models: [gameFire],
              onSetState: (_, model) {
                game.setValue(() => gameFire.value);
              },
              builder: (_, __) {
                return Scaffold(
                    appBar: AppBar(
                      title: Text("${game.value.id}"),
                      actions: [
                        FlatButton(
                            onPressed: () {
                              game.setState(
                                (game) => game.bids.add(SimpleBid(
                                    points: 90,
                                    color: CardColor.HEART,
                                    position: PlayerPosition.EAST)),
                                filterTags: [Aspects.BIDS],
                                catchError: true,
                                onSetState: (context) {
                                  if (game.hasError) {
                                    showDialog(
                                      context: context,
                                      builder: (context) => AlertDialog(
                                        title: Icon(Icons.error),
                                        content: Text("${game.error}"),
                                      ),
                                    );
                                  }
                                },
                              );
                            },
                            child: Text("add a bid"))
                      ],
                    ),
                    floatingActionButton: FloatingActionButton(
                        child: Icon(Icons.sort_by_alpha),
                        onPressed: () =>
                            game.setState((Game g) => g.sortCards())),
                    body: TestWidget());
              },
            );
          },
        ),
      ),
    );
  }

  Future _quit(BuildContext context) {
    return showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: Text("Exit game?"),
        actions: [
          FlatButton(
              onPressed: () => Navigator.of(context).pop(false),
              child: Text("No, stay here")),
          FlatButton(
              onPressed: () {
                Navigator.of(context).pop(true);
                SystemChrome.restoreSystemUIOverlays();
              },
              child: Text("Yes, exit!"))
        ],
      ),
    );
  }
}

class TestWidget extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    final ReactiveModel<Game> game =
        Injector.getAsReactive<Game>(context: context);
    return StateBuilder(
      models: [game],
      tag: Aspects.BIDS,
      builder: (context, model) => Padding(
        padding: const EdgeInsets.all(20.0),
        child: Center(child: Text("${game.value}")),
      ),
    );
  }
}
