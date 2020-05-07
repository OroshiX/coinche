import 'package:FlutterCoinche/domain/dto/game.dart';
import 'package:FlutterCoinche/domain/dto/player_position.dart';
import 'package:FlutterCoinche/domain/dto/pos_table_to_colors.dart';
import 'package:FlutterCoinche/domain/dto/table_state.dart';
import 'package:FlutterCoinche/domain/extensions/game_extensions.dart';
import 'package:FlutterCoinche/domain/logic/calculus.dart';
import 'package:FlutterCoinche/ui/resources/colors.dart';
import 'package:bubble/bubble.dart';
import 'package:flutter/material.dart';
import 'package:states_rebuilder/states_rebuilder.dart';

class BidsWidget extends StatelessWidget {
  final AxisDirection posTable;
  final double widthAvatar, heightAvatar;

  const BidsWidget(
      {Key key,
      @required this.posTable,
      @required this.widthAvatar,
      @required this.heightAvatar})
      : super(key: key);

  @override
  Widget build(BuildContext context) {
    return StateBuilder<Game>(
      tag: [Aspects.MY_POSITION, Aspects.BIDS, Aspects.STATE],
      models: [RM.get<Game>()],
      builder: (context, model) {
        final me = model.state.myPosition;
        final PlayerPosition cardinalPosition =
            getPosTableToCardinal(me)[posTable];
        final theBids = model.state.bids
                ?.where((element) => element.position == cardinalPosition) ??
            [];
        final state = model.state.state;
        final mapToColor = Injector.get<PosTableToColor>().value;
        if (state != TableState.BIDDING || theBids.isEmpty) return SizedBox();
        Alignment alignment;
        BubbleNip bubbleNip;
        double dx = 0, dy = 0;
        switch (posTable) {
          case AxisDirection.up:
            alignment = Alignment.topCenter;
            bubbleNip = BubbleNip.leftTop;
            dx = widthAvatar;
            dy = 2;
            break;
          case AxisDirection.right:
            alignment = Alignment.centerRight;
            bubbleNip = BubbleNip.rightTop;
            dx = -widthAvatar;
            break;
          case AxisDirection.down:
            alignment = Alignment.bottomRight;
            bubbleNip = BubbleNip.no;
            dy = -heightAvatar;
            break;
          case AxisDirection.left:
            alignment = Alignment.centerLeft;
            bubbleNip = BubbleNip.leftTop;
            dx = widthAvatar;
            break;
        }

        Map<PlayerPosition, Color> cardinalToPosTable =
            getCardinalToPosTable(me)
                .map((key, value) => MapEntry(key, mapToColor[value].item1));
        return Transform.translate(
          offset: Offset(dx, dy),
          child: Bubble(
            color: colorLightBlue,
            child: Column(
              mainAxisSize: MainAxisSize.min,
              children: theBids
                  .map((e) => e.getReadableBidRow(12, 7, cardinalToPosTable,
                      displayBy: false))
                  .toList(),
            ),
            alignment: alignment,
            nip: bubbleNip,
            elevation: 4,
          ),
        );
      },
    );
  }
}
