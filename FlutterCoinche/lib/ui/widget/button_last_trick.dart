import 'package:FlutterCoinche/domain/dto/card.dart';
import 'package:FlutterCoinche/domain/dto/game.dart';
import 'package:FlutterCoinche/domain/dto/pos_table_to_colors.dart';
import 'package:FlutterCoinche/domain/dto/table_state.dart';
import 'package:FlutterCoinche/domain/extensions/game_extensions.dart';
import 'package:FlutterCoinche/domain/logic/calculus.dart';
import 'package:FlutterCoinche/ui/resources/colors.dart';
import 'package:FlutterCoinche/ui/widget/card_widget.dart';
import 'package:FlutterCoinche/ui/widget/dot_player.dart';
import 'package:FlutterCoinche/ui/widget/neumorphic_container.dart';
import 'package:flutter/material.dart';
import 'package:states_rebuilder/states_rebuilder.dart';

class ButtonLastTrick extends StatelessWidget {
  const ButtonLastTrick({Key key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return StateBuilder<Game>(
      models: [RM.get<Game>()],
      tag: [Aspects.LAST_TRICK, Aspects.STATE, Aspects.MY_POSITION],
      builder: (context, model) {
        final game = model.state;
        final state = game.state;
        final me = game.myPosition;
        final cardinalToPosTable = getCardinalToPosTable(me);
        final mapColor = RM
            .get<PosTableToColor>()
            .state
            .value
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
                                                  cardinalToPosTable[
                                                      e.position]],
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
      },
    );
  }
}
