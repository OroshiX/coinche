import 'package:FlutterCoinche/domain/dto/bid.dart';
import 'package:FlutterCoinche/domain/dto/player_position.dart'
    show PlayerPosition;
import 'package:FlutterCoinche/domain/dto/pos_table_to_colors.dart';
import 'package:FlutterCoinche/domain/dto/score.dart';
import 'package:FlutterCoinche/domain/logic/calculus.dart';
import 'package:FlutterCoinche/state/game_model.dart';
import 'package:FlutterCoinche/ui/resources/colors.dart';
import 'package:FlutterCoinche/ui/widget/dot_player.dart';
import 'package:FlutterCoinche/ui/widget/neumorphic_container.dart';
import 'package:FlutterCoinche/ui/widget/neumorphic_no_state.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:states_rebuilder/states_rebuilder.dart';
import 'package:tuple/tuple.dart';

class OnlyScoreWidget extends StatelessWidget {
  final Bid currentBid;

  final double minWidth;

  const OnlyScoreWidget(
      {Key key, @required this.currentBid, @required this.minWidth})
      : assert(currentBid == null ||
            currentBid is SimpleBid ||
            currentBid is Capot ||
            currentBid is General ||
            currentBid is Coinche),
        super(key: key);

  @override
  Widget build(BuildContext context) {
    final Map<AxisDirection, Color> mapColor = Injector.get<PosTableToColor>()
        .value
        .map((key, value) => MapEntry(key, value.item1));
    const textSize = 14.0;
    const dotSize = 7.0;
    return NeumorphicNoStateWidget(
      borderRadius: 10,
      sizeShadow: SizeShadow.SMALL,
      pressed: false,
      child: Padding(
        padding: const EdgeInsets.all(8),
        child: IntrinsicWidth(
          child: Selector<GameModel, Tuple2<Score, Map<PlayerPosition, Color>>>(
            selector: (ctx, gm) => Tuple2(
                gm.game.score,
                getCardinalToPosTable(gm.game.myPosition)
                    .map((key, value) => MapEntry(key, mapColor[value]))),
            builder: (context, value, child) {
              final Score score = value.item1;
              final playerPosToColor = value.item2;
              return Column(
                mainAxisAlignment: MainAxisAlignment.spaceAround,
                children: [
                  SizedBox(
                    width: minWidth,
                  ),
                  if (currentBid != null)
                    currentBid.getReadableBidRow(textSize,
                        dotSize: dotSize, cardinalToPosTable: playerPosToColor),
                  if (currentBid != null)
                    Padding(
                      padding: const EdgeInsets.only(top: 4.0, bottom: 4),
                      child: Container(
                        color: colorText,
                        height: 1,
                      ),
                    ),
                  Table(
                    defaultVerticalAlignment: TableCellVerticalAlignment.middle,
                    columnWidths: {2: FractionColumnWidth(0.55)},
                    children: [
                      TableRow(children: [
                        DotPlayer(
                          dotSize: dotSize,
                          color: playerPosToColor[PlayerPosition.NORTH],
                        ),
                        DotPlayer(
                          dotSize: dotSize,
                          color: playerPosToColor[PlayerPosition.SOUTH],
                        ),
                        Text(
                          score?.northSouth?.toString() ?? "",
                          textAlign: TextAlign.right,
                          style: TextStyle(
                              color: colorTextDark, fontSize: textSize),
                        ),
                      ]),
                      TableRow(children: [
                        DotPlayer(
                          dotSize: dotSize,
                          color: playerPosToColor[PlayerPosition.EAST],
                        ),
                        DotPlayer(
                          dotSize: dotSize,
                          color: playerPosToColor[PlayerPosition.WEST],
                        ),
                        Text(
                          score?.eastWest?.toString() ?? "",
                          textAlign: TextAlign.right,
                          style: TextStyle(
                              color: colorTextDark, fontSize: textSize),
                        ),
                      ])
                    ],
                  )
                ],
              );
            },
          ),
        ),
      ),
    );
  }
}
