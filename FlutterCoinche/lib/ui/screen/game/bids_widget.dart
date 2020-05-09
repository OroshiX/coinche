import 'package:FlutterCoinche/domain/dto/bid.dart';
import 'package:FlutterCoinche/domain/dto/game.dart';
import 'package:FlutterCoinche/domain/dto/player_position.dart';
import 'package:FlutterCoinche/domain/dto/table_state.dart';
import 'package:FlutterCoinche/domain/extensions/game_extensions.dart';
import 'package:FlutterCoinche/domain/logic/calculus.dart';
import 'package:FlutterCoinche/ui/resources/colors.dart';
import 'package:bubble/bubble.dart';
import 'package:flutter/cupertino.dart';
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
    final GlobalKey<AnimatedListState> key = GlobalKey();
    final List<Bid> currentBids = [];
    return StateBuilder<Game>(
      tag: [Aspects.MY_POSITION, fromPositionBid(posTable), Aspects.STATE],
      models: [RM.get<Game>()],
      onSetState: (context, model) {
        final newBids = model.state.bidsOfPosition(posTable);
        final changeBids =
            GameExtensions.changeBid(currentBids, newBids, posTable);
        if (changeBids == null) return;
        if (changeBids.typeChange == TypeChange.INSERT) {
          for (var i = 0; i < changeBids.nbChanges; i++) {
            final indexInsert = currentBids.length;
            currentBids.add(newBids[indexInsert]);
            key.currentState.insertItem(indexInsert);
          }
        } else {
          // remove N bids
          for (var i = 0; i < changeBids.nbChanges; i++) {
            final indexRemove = currentBids.length - 1;
            currentBids.removeAt(indexRemove);
            key.currentState.removeItem(
                indexRemove,
                (context, animation) => Container(
                      width: 30,
                      height: 30,
                      color: Colors.blue,
                    ));
          }
        }
      },
      builder: (context, model) {
        final me = model.state.myPosition;
        final PlayerPosition cardinalPosition =
            getPosTableToCardinal(me)[posTable];
        currentBids.clear();
        currentBids.addAll(model.state.bids
                ?.where((element) => element.position == cardinalPosition) ??
            []);
        final state = model.state.state;
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
//            dx = -widthAvatar;
            dy = -heightAvatar;
            break;
          case AxisDirection.down:
            alignment = Alignment.bottomRight;
            bubbleNip = BubbleNip.no;
            dy = -heightAvatar;
            break;
          case AxisDirection.left:
            alignment = Alignment.centerLeft;
            bubbleNip = BubbleNip.leftTop;
//            dx = widthAvatar;
            dy = -heightAvatar;
            break;
        }
        return Transform.translate(
          offset: Offset(dx, dy),
          child: Align(
            child: ConstrainedBox(
              constraints: BoxConstraints(maxWidth: 170, maxHeight: 80),
              child: AnimatedOpacity(
                opacity: (state != TableState.BIDDING || currentBids.isEmpty)
                    ? 0
                    : 1,
                duration: Duration(milliseconds: 200),
                child: Container(
                  decoration: BoxDecoration(color: colorLightBlue, boxShadow: [
                    BoxShadow(
                        offset: Offset(2, 2),
                        color: colorShadow,
                        spreadRadius: 2,
                        blurRadius: 4),
                    BoxShadow(
                        offset: Offset(-2, -2),
                        color: Colors.white,
                        spreadRadius: 2,
                        blurRadius: 3),
                  ]),
                  child: AnimatedList(
                    scrollDirection: Axis.vertical,
                    key: key,
                    initialItemCount: currentBids.length,
                    itemBuilder: (context, index, animation) {
                      return SlideTransition(
                        position: animation.drive(
                            Tween(begin: Offset(-1, -1), end: Offset(0, 0))),
                        child: Padding(
                          padding: const EdgeInsets.only(bottom: 2.0),
                          child: Container(
                            padding: const EdgeInsets.all(2),
                            color: Colors.lightGreen,
                            child: LimitedBox(
                              maxWidth: 80,
                              maxHeight: 40,
                              child: currentBids
                                  .toList()[index]
                                  .getReadableBidRow(16, displayBy: false),
                            ),
                          ),
                        ),
                      );
                    },
                  ),
                ),
              ),
            ),
            alignment: alignment,
          ),
        );
      },
    );
  }
}
