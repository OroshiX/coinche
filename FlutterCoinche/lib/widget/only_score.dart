import 'package:FlutterCoinche/business/calculus.dart';
import 'package:FlutterCoinche/dto/bid.dart';
import 'package:FlutterCoinche/dto/player_position.dart'
    show PlayerPosition, TablePosition;
import 'package:FlutterCoinche/dto/score.dart';
import 'package:FlutterCoinche/resources/colors.dart';
import 'package:FlutterCoinche/widget/dot_player.dart';
import 'package:FlutterCoinche/widget/game_inherited.dart';
import 'package:FlutterCoinche/widget/neumorphic_container.dart';
import 'package:FlutterCoinche/widget/neumorphic_no_state.dart';
import 'package:flutter/material.dart';

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
    final Score score =
        GameInherited.of(context, aspectType: Aspects.SCORE).game.score;
    final map = getCardinalToPosTable(
        GameInherited.of(context, aspectType: Aspects.MY_POSITION)
            .game
            .myPosition);
    const textSize = 14.0;
    const dotSize = 7.0;
    return NeumorphicNoStateWidget(
      borderRadius: 10,
      sizeShadow: SizeShadow.SMALL,
      pressed: false,
      child: Padding(
        padding: const EdgeInsets.all(8),
        child: IntrinsicWidth(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.spaceAround,
            children: [
              SizedBox(
                width: minWidth,
              ),
              if (currentBid != null)
                currentBid.getReadableBidRow(textSize, dotSize, map),
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
                    FutureBuilder<Color>(
                      future: map[PlayerPosition.NORTH].getColor(),
                      initialData: Colors.blue,
                      builder: (context, snapshot) => DotPlayer(
                        dotSize: dotSize,
                        color: snapshot.data,
                      ),
                    ),
                    FutureBuilder<Color>(
                      future: map[PlayerPosition.SOUTH].getColor(),
                      initialData: Colors.blue,
                      builder: (context, snapshot) => DotPlayer(
                        dotSize: dotSize,
                        color: snapshot.data,
                      ),
                    ),
                    Text(
                      score.northSouth.toString(),
                      textAlign: TextAlign.right,
                      style:
                          TextStyle(color: colorTextDark, fontSize: textSize),
                    ),
                  ]),
                  TableRow(children: [
                    FutureBuilder<Color>(
                      future: map[PlayerPosition.EAST].getColor(),
                      initialData: Colors.blue,
                      builder: (context, snapshot) => DotPlayer(
                        dotSize: dotSize,
                        color: snapshot.data,
                      ),
                    ),
                    FutureBuilder(
                      future: map[PlayerPosition.WEST].getColor(),
                      initialData: Colors.blue,
                      builder: (context, snapshot) => DotPlayer(
                        dotSize: dotSize,
                        color: snapshot.data,
                      ),
                    ),
                    Text(
                      score.eastWest.toString(),
                      textAlign: TextAlign.right,
                      style:
                          TextStyle(color: colorTextDark, fontSize: textSize),
                    ),
                  ])
                ],
              )
            ],
          ),
        ),
      ),
    );
  }
}
