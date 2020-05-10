import 'package:FlutterCoinche/domain/dto/bid.dart';
import 'package:FlutterCoinche/domain/dto/card.dart';
import 'package:FlutterCoinche/domain/dto/game.dart';
import 'package:FlutterCoinche/domain/dto/nicknames.dart';
import 'package:FlutterCoinche/domain/dto/player_position.dart';
import 'package:FlutterCoinche/domain/dto/pos_table_to_colors.dart';
import 'package:FlutterCoinche/domain/dto/table_state.dart';
import 'package:FlutterCoinche/domain/extensions/cards_extension.dart';
import 'package:FlutterCoinche/domain/extensions/game_extensions.dart';
import 'package:FlutterCoinche/ui/resources/colors.dart';
import 'package:FlutterCoinche/ui/screen/game/table_widget.dart';
import 'package:flutter/material.dart';
import 'package:states_rebuilder/states_rebuilder.dart';
import 'package:tuple/tuple.dart';

class TestingOfflineGame extends StatelessWidget {
  static const String routeName = "/offlineTest";

  @override
  Widget build(BuildContext context) {
    return Material(
      child: Container(
          width: MediaQuery.of(context).size.width,
          height: MediaQuery.of(context).size.height,
          color: colorLightBlue,
          child: Injector(
            inject: [
              Inject(() => Game()),
              Inject.future(() => getPosTableToColors(),
                  initialValue: PosTableToColor({
                    AxisDirection.down:
                        Tuple2(Colors.red, "images/vampire.svg"),
                    AxisDirection.right:
                        Tuple2(Colors.redAccent, "images/vampire.svg"),
                    AxisDirection.up: Tuple2(Colors.pink, "images/vampire.svg"),
                    AxisDirection.left:
                        Tuple2(Colors.pinkAccent, "images/vampire.svg"),
                  })),
            ],
            builder: (ctx) {
              final rmGame = RM.get<Game>();
              final rmPos = RM.get<PosTableToColor>();
              return Stack(children: [
                StateBuilder<Game>(
                  observe: () => RM.create(_init()),
                  builder: (_, initModel) => WhenRebuilder<Game>(
                    models: [rmPos],
//                    onSetState: (_,),
                    onData: (data) => TableWidget(
                        quit: (context) => showDialog(context: context)),
                    onWaiting: () => CircularProgressIndicator(),
                    onIdle: () => Text("idle: $rmGame, $rmPos"),
                    onError: (error) => Text("error: $error"),
                  ),
                ),
                Positioned(
                  bottom: 0,
                  child: Column(children: [
                    RaisedButton(
                      onPressed: () {
                        rmGame.setValue(() => _init());
                      },
                      child: Text("Init"),
                    ),
                    FlatButton(
                        onPressed: () {
                          _addACardFor(rmGame);
                        },
                        child: Text("Add a card on table")),
                  ]),
                ),
              ]);
            },
          )),
    );
  }

  _addACardFor(ReactiveModel<Game> rmGame) {
    Game g = rmGame.value.copy(withOnTable: rmGame.value.onTable.toList());
    if (g.onTable.length == 4) {
      g.onTable.clear();
    } else if (g.onTable.isEmpty) {
      g.onTable.add(getRandomCardTable(PlayerPosition.NORTH));
    } else {
      g.onTable.add(getRandomCardTable(_next(g.onTable.last.position)));
    }

    rmGame.setValue(
      () => g,
      filterTags: [Aspects.ON_TABLE],
      onSetState: (context) => print("setState in testing"),
    );
  }

  PlayerPosition _next(PlayerPosition playerPosition) {
    switch (playerPosition) {
      case PlayerPosition.NORTH:
        return PlayerPosition.EAST;
      case PlayerPosition.SOUTH:
        return PlayerPosition.WEST;
      case PlayerPosition.EAST:
        return PlayerPosition.SOUTH;
      case PlayerPosition.WEST:
        return PlayerPosition.NORTH;
    }
    return null;
  }

  Game _init() {
    return Game(
        onTable: [],
        nicknames:
            Nicknames(north: "me", east: "toto", south: "south", west: "west"),
        cards: [randomCard(), randomCard()],
        id: "toto",
        state: TableState.PLAYING,
        myPosition: PlayerPosition.NORTH,
        currentBid: SimpleBid(
            points: 80, color: CardColor.CLUB, position: PlayerPosition.WEST));
  }
}
