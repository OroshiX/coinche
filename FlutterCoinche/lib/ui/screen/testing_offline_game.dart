import 'dart:math';

import 'package:coinche/domain/dto/bid.dart';
import 'package:coinche/domain/dto/card.dart';
import 'package:coinche/domain/dto/game.dart';
import 'package:coinche/domain/dto/nicknames.dart';
import 'package:coinche/domain/dto/player_position.dart';
import 'package:coinche/domain/dto/pos_table_to_colors.dart';
import 'package:coinche/domain/dto/table_state.dart';
import 'package:coinche/domain/extensions/cards_extension.dart';
import 'package:coinche/state/game_model.dart';
import 'package:coinche/theme/colors.dart';
import 'package:coinche/ui/screen/game/table/table_widget.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:tuple/tuple.dart';

class TestingOfflineGame extends StatelessWidget {
  static const String routeName = "/offlineTest";

  @override
  Widget build(BuildContext context) {
    return Material(
      child: Container(
          width: MediaQuery.of(context).size.width,
          height: MediaQuery.of(context).size.height,
          color: kColorBeauBlue,
          child: MultiProvider(
              providers: [
                ChangeNotifierProvider(
                  create: (context) => GameModel(),
                  lazy: false,
                ),
                FutureProvider(
                    create: (context) => getPosTableToColors(),
                    initialData: PosTableToColor({
                      AxisDirection.down:
                          Tuple2(Colors.red, "images/vampire.svg"),
                      AxisDirection.right:
                          Tuple2(Colors.redAccent, "images/vampire.svg"),
                      AxisDirection.up:
                          Tuple2(Colors.pink, "images/vampire.svg"),
                      AxisDirection.left:
                          Tuple2(Colors.pinkAccent, "images/vampire.svg"),
                    })),
              ],
              child: Stack(children: [
                ChangeNotifierProvider(
                  create: (context) => GameModel(),
                  child: Consumer<PosTableToColor>(
                    builder: (context, value, child) => TableWidget(
                      quit: (context) => showDialog(context: context),
                    ),
                  ),
                ),
                Positioned(
                  bottom: 0,
                  child: Column(children: [
                    RaisedButton(
                      onPressed: () {
                        context.read<GameModel>().game = _init();
                      },
                      child: Text("Init"),
                    ),
                    FlatButton(
                        onPressed: () {
                          _addACardFor(context.read<GameModel>());
                        },
                        child: Text("Add a card on table")),
                  ]),
                ),
              ]))),
    );
  }

  void _addACardFor(GameModel rmGame) {
    var winner = PlayerPosition.values[Random().nextInt(4)];
    Game g = rmGame.game.copy(
        withOnTable: rmGame.game.onTable.toList(),
        withLastTrick: rmGame.game.onTable.toList(),
        withWinnerLastTrick: winner);
    var changedWinner = false;
    if (g.onTable.length == 4) {
      g.onTable.clear();
      changedWinner = true;
      g.onTable.add(getRandomCardTable(_next(winner)));
    } else if (g.onTable.isEmpty) {
      g.onTable.add(getRandomCardTable(PlayerPosition.north));
    } else {
      g.onTable.add(getRandomCardTable(_next(g.onTable.last.position)));
    }
    rmGame.game = g;
  }

  PlayerPosition _next(PlayerPosition playerPosition) {
    switch (playerPosition) {
      case PlayerPosition.north:
        return PlayerPosition.east;
      case PlayerPosition.south:
        return PlayerPosition.west;
      case PlayerPosition.east:
        return PlayerPosition.south;
      case PlayerPosition.west:
        return PlayerPosition.north;
    }
  }

  Game _init() {
    return Game(
        onTable: [
          getRandomCardTable(PlayerPosition
              .values[Random().nextInt(PlayerPosition.values.length)])
        ],
        nicknames:
            Nicknames(north: "me", east: "toto", south: "south", west: "west"),
        cards: [randomCard(), randomCard()],
        id: "toto",
        state: TableState.playing,
        myPosition: PlayerPosition.north,
        currentBid: SimpleBid(
            points: 80, color: CardColor.club, position: PlayerPosition.west));
  }
}
