import 'package:FlutterCoinche/domain/dto/bid.dart';
import 'package:FlutterCoinche/domain/dto/card.dart';
import 'package:FlutterCoinche/domain/dto/game.dart';
import 'package:FlutterCoinche/domain/dto/player_position.dart';
import 'package:FlutterCoinche/domain/dto/pos_table_to_colors.dart';
import 'package:FlutterCoinche/domain/extensions/game_extensions.dart';
import 'package:FlutterCoinche/state/games_bloc.dart';
import 'package:FlutterCoinche/ui/resources/colors.dart';
import 'package:FlutterCoinche/ui/widget/table_widget.dart';
import 'package:bloc_provider/bloc_provider.dart';
import 'package:flushbar/flushbar_helper.dart';
import 'package:flutter/cupertino.dart';
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
        child: Container(
          color: colorLightBlue,
          child: _top(context),
        ),
      ),
    );
  }

  Widget _top(BuildContext context) {
    return Injector(
      inject: [
        Inject(() => Game()),
        Inject.future(() => getPosTableToColors())
      ],
      builder: (_) => StateBuilder<Game>(
        observe: () => RM
            .stream(BlocProvider.of<GamesBloc>(context).game, name: "gameFire")
//            .asNew("mySeed")
//              ..setState((s) => s.sortCards())
        ,
        builder: (_, modelFire) {
          final currentModel = RM.get<Game>();
          final pos = RM.get<PosTableToColor>();
          return WhenRebuilder<Game>(
              models: [modelFire, pos],
              onIdle: () => _buildEmpty(),
              onWaiting: () => _buildWaiting(),
              onError: (e) => _buildEmpty(),
              onSetState: (_, rmFire) {
                if (rmFire.hasData && rmFire.state != null) {
                  var different = rmFire.state.different(currentModel.state);
                  currentModel.setValue(() => rmFire.state,
                      filterTags: different);
                }
              },
              onData: (_) {
                return TableWidget(quit: _quit);
//                  return _build(model.getReactive());
              });
        },
      ),
    );
    /*
    return Injector(
      inject: [
        Inject.stream(() => BlocProvider.of<GamesBloc>(context).game,
            name: "game")
      ],
      builder: (BuildContext buildContext) {
        return WhenRebuilder<Game>(
            observe: () => RM.get<Game>(name: "game"),
            onIdle: () => _buildEmpty(),
            onWaiting: () => _buildWaiting(),
            onError: (e) => _buildEmpty(),
            onData: (gameFire) {
//              var reactiveModel = RM.create(gameFire).asNew();
              return StateBuilder<Game>(
                  onSetState: (context, model) {
                    model.setValue(() => gameFire);
                  },
                  observe: () => RM.create(gameFire),
//                  observeMany: [
//                    () => reactiveModel,
//                    () => RM.future(getPosTableToColors()),
//                  ],
                  builder:
                      (BuildContext context, ReactiveModel<Game> gameModel) {
                    if (gameModel.state != null) return _build(gameModel);
                    return _buildEmpty();
                  });
            });
      },
    );
    */
  }

  Widget _build(ReactiveModel<Game> gameModel) {
    return Scaffold(
        appBar: AppBar(
          title: Text("${gameModel.state.id}"),
          actions: [
            FlatButton(
                onPressed: () {
                  gameModel.setState(
                    (g) => g.bids.add(SimpleBid(
                        points: 90,
                        color: CardColor.HEART,
                        position: PlayerPosition.EAST)),
                    filterTags: [Aspects.BIDS],
                    catchError: true,
                    onSetState: (context) {},
                  );
                },
                child: Text("add a bid"))
          ],
        ),
        floatingActionButton: FloatingActionButton(
            child: Icon(Icons.sort_by_alpha),
            onPressed: () => gameModel.setState((Game g) => g.sortCards())),
        body: TestWidget());
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

  Widget _buildWaiting() {
    return Center(
      child: CircularProgressIndicator(),
    );
  }

  Widget _buildEmpty() {
    return Center(
      child: Text("Empty"),
    );
  }
}

class TestWidget extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    final ReactiveModel<Game> game = RM.get<Game>();
    return StateBuilder(
      models: [game],
      tag: Aspects.BIDS,
      onSetState: (context, model) {
        FlushbarHelper.createInformation(message: "Hi, new Bids!")
            .show(context);
      },
      builder: (context, model) => Padding(
        padding: const EdgeInsets.all(20.0),
        child: Center(child: Text("${game.value}")),
      ),
    );
  }
}
