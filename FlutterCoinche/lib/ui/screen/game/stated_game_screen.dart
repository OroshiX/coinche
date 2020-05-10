import 'package:FlutterCoinche/domain/dto/game.dart';
import 'package:FlutterCoinche/domain/dto/player_position.dart';
import 'package:FlutterCoinche/domain/dto/pos_table_to_colors.dart';
import 'package:FlutterCoinche/domain/extensions/cards_extension.dart';
import 'package:FlutterCoinche/domain/extensions/game_extensions.dart';
import 'package:FlutterCoinche/state/games_bloc.dart';
import 'package:FlutterCoinche/ui/resources/colors.dart';
import 'package:FlutterCoinche/ui/screen/game/table_widget.dart';
import 'package:bloc_provider/bloc_provider.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:states_rebuilder/states_rebuilder.dart';

class StatedGameScreen extends StatelessWidget {
  static const routeName = "/statedGame";

  @override
  Widget build(BuildContext context) {
    SystemChrome.setEnabledSystemUIOverlays([]);
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
        observe: () => RM.stream(BlocProvider.of<GamesBloc>(context).game,
            name: "gameFire"),
        builder: (_, modelFire) {
          final currentModel = RM.get<Game>();
          return WhenRebuilder<Game>(
              models: [modelFire, RM.get<PosTableToColor>()],
              onIdle: () => _buildEmpty(),
              onWaiting: () => _buildWaiting(),
              onError: (e) => _buildEmpty(),
              onSetState: (_, rmFire) {
                if (rmFire.hasData && rmFire.state != null) {
                  var different = rmFire.state.different(currentModel.state);
                  final fireGame = modelFire.value;
                  if (different.isNotEmpty) {
                    if(different.contains(Aspects.ON_TABLE)) {
                      print("NEW onTable: ${fireGame.onTable}");
                    }
                    currentModel.setValue(
                        () => fireGame.copy(
                            withCards: fireGame.cards.toList()
                              ..sortCards(fireGame.state, fireGame.currentBid)),
                        filterTags: different);
                  }
                }
              },
              onData: (_) {
                return TableWidget(quit: _quit);
              });
        },
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
