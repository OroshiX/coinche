import 'package:FlutterCoinche/business/calculus.dart';
import 'package:FlutterCoinche/dto/card.dart';
import 'package:FlutterCoinche/dto/table_state.dart';
import 'package:FlutterCoinche/resources/colors.dart';
import 'package:FlutterCoinche/widget/card_widget.dart';
import 'package:FlutterCoinche/widget/dot_player.dart';
import 'package:FlutterCoinche/widget/game_inherited.dart';
import 'package:FlutterCoinche/widget/neumorphic_container.dart';
import 'package:flutter/material.dart';

class ButtonLastTrick extends StatelessWidget {
  const ButtonLastTrick({Key key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    final game = GameInherited.of(context, aspectType: Aspects.LAST_TRICK).game;
    final state =
        GameInherited.of(context, aspectType: Aspects.STATE).game.state;
    final me = GameInherited.of(context, aspectType: Aspects.MY_POSITION)
        .game
        .myPosition;
    final cardinalToPosTable = getCardinalToPosTable(me);
    final mapColor = GameInherited.of(context, aspectType: Aspects.COLORS)
        .map
        .map((key, value) => MapEntry(key, value.item1));
    final List<CardPlayed> lastTrick = game.lastTrick;
    final winner = game.winnerLastTrick;
    return Visibility(
      visible: state == TableState.PLAYING && winner != null,
      child: Padding(
        padding: const EdgeInsets.only(bottom: 8.0),
        child: NeumorphicWidget(
            onTap: () {
              showDialog(
                context: context,
                builder: (context) {
                  return Column(
                      mainAxisSize: MainAxisSize.min,
                      mainAxisAlignment: MainAxisAlignment.center,
                      crossAxisAlignment: CrossAxisAlignment.center,
                      children: [
                        Padding(
                          padding: const EdgeInsets.all(8.0),
                          child: DotPlayer(
                            dotSize: 20,
                            color: mapColor[cardinalToPosTable[winner]],
                          ),
                        ),
                        Row(
                          mainAxisSize: MainAxisSize.min,
                          mainAxisAlignment: MainAxisAlignment.center,
                          crossAxisAlignment: CrossAxisAlignment.center,
                          children: lastTrick
                              .map((e) => Padding(
                                    padding: const EdgeInsets.all(8.0),
                                    child: Container(
                                        decoration: BoxDecoration(
                                          borderRadius:
                                              BorderRadius.circular(10),
                                          color: mapColor[
                                              cardinalToPosTable[e.position]],
                                        ),
                                        padding: EdgeInsets.all(8),
                                        child: CardWidget(
                                            card: CardModel(
                                                color: e.card.color,
                                                value: e.card.value,
                                                playable: null),
                                            width: 40,
                                            height: 60)),
                                  ))
                              .toList(),
                        ),
                      ]);
                },
              );
            },
            child: Container(
                padding: EdgeInsets.symmetric(horizontal: 10, vertical: 10),
                child: Text(
                  "Last Trick",
                  style: TextStyle(color: colorTextDark),
                )),
            sizeShadow: SizeShadow.SMALL,
            borderRadius: 10,
            interactable: true),
      ),
    );
  }
}
