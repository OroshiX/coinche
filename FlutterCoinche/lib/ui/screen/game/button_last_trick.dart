import 'package:FlutterCoinche/domain/dto/card.dart';
import 'package:FlutterCoinche/domain/dto/player_position.dart';
import 'package:FlutterCoinche/domain/dto/pos_table_to_colors.dart';
import 'package:FlutterCoinche/domain/dto/table_state.dart';
import 'package:FlutterCoinche/domain/logic/calculus.dart';
import 'package:FlutterCoinche/state/game_model.dart';
import 'package:FlutterCoinche/ui/resources/colors.dart';
import 'package:FlutterCoinche/ui/widget/card_widget.dart';
import 'package:FlutterCoinche/ui/widget/dot_player.dart';
import 'package:FlutterCoinche/ui/widget/neumorphic_container.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:tuple/tuple.dart';

class ButtonLastTrick extends StatelessWidget {
  const ButtonLastTrick({Key key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Selector<
        GameModel,
        Tuple4<PlayerPosition, List<CardPlayed>, TableState,
            Map<PlayerPosition, AxisDirection>>>(
      selector: (ctx, gm) => Tuple4(
        gm.game.winnerLastTrick,
        gm.game.lastTrick,
        gm.game.state,
        getCardinalToPosTable(gm.game.myPosition),
      ),
      builder: (context, value, child) {
        var mapColor = context
            .read<PosTableToColor>()
            .value
            .map((key, value) => MapEntry(key, value.item1));

        final winner = value.item1;
        final List<CardPlayed> lastTrick = value.item2;
        return Visibility(
          visible: value.item3 == TableState.PLAYING && winner != null,
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
                                color: mapColor[value.item4[winner]],
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
                                                  value.item4[e.position]],
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
