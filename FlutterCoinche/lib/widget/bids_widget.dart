import 'package:FlutterCoinche/business/calculus.dart';
import 'package:FlutterCoinche/dto/player_position.dart';
import 'package:FlutterCoinche/dto/table_state.dart';
import 'package:FlutterCoinche/resources/colors.dart';
import 'package:FlutterCoinche/widget/game_inherited.dart';
import 'package:bubble/bubble.dart';
import 'package:flutter/material.dart';

class BidsWidget extends StatelessWidget {
  final AxisDirection posTable;

  const BidsWidget({Key key, @required this.posTable}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    final me = GameInherited.of(context, aspectType: Aspects.MY_POSITION)
        .game
        .myPosition;
    final PlayerPosition cardinalPosition = getPosTableToCardinal(me)[posTable];
    final theBids = GameInherited.of(context, aspectType: Aspects.BIDS)
        .game
        .bids?.where((element) => element.position == cardinalPosition) ?? [];
    final state =
        GameInherited.of(context, aspectType: Aspects.STATE).game.state;
    if (state != TableState.BIDDING) return SizedBox();

    Alignment alignment;
    BubbleNip bubbleNip;
    switch (posTable) {
      case AxisDirection.up:
        alignment = Alignment.topCenter;
        bubbleNip = BubbleNip.no;
        break;
      case AxisDirection.right:
        alignment = Alignment.centerRight;
        bubbleNip = BubbleNip.rightTop;
        break;
      case AxisDirection.down:
        alignment = Alignment.bottomCenter;
        bubbleNip = BubbleNip.no;
        break;
      case AxisDirection.left:
        alignment = Alignment.centerLeft;
        bubbleNip = BubbleNip.leftTop;
        break;
    }
    return Bubble(
      color: colorLightBlue,
      child: Text(theBids.map((e) => e.toString()).join("\n")),
      alignment: alignment,
      nip: bubbleNip,
      elevation: 4,
    );
  }
}
