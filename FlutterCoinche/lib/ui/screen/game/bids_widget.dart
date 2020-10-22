import 'package:FlutterCoinche/domain/dto/bid.dart';
import 'package:FlutterCoinche/domain/dto/table_state.dart';
import 'package:FlutterCoinche/domain/extensions/game_extensions.dart';
import 'package:FlutterCoinche/state/game_model.dart';
import 'package:FlutterCoinche/ui/resources/colors.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:tuple/tuple.dart';

class BidsAnimated extends StatefulWidget {
  final TableState tableState;
  final AxisDirection posTable;
  final List<Bid> bidsOfPosition;
  final double heightAvatar, widthAvatar;
  const BidsAnimated(
      {Key key,
      this.tableState,
      this.posTable,
      this.bidsOfPosition,
      this.heightAvatar,
      this.widthAvatar})
      : super(key: key);

  @override
  _BidsAnimatedState createState() => _BidsAnimatedState();
}

class _BidsAnimatedState extends State<BidsAnimated> {
  final GlobalKey<AnimatedListState> _key = GlobalKey();
  final List<Bid> currentBids = [];

  @override
  void didUpdateWidget(covariant BidsAnimated oldWidget) {
    super.didUpdateWidget(oldWidget);

    final newBids = widget.bidsOfPosition;
    final changeBids =
        GameExtensions.changeBid(currentBids, newBids, widget.posTable);
    if (changeBids == null) return;
    if (changeBids.typeChange == TypeChange.INSERT) {
      for (var i = 0; i < changeBids.nbChanges; i++) {
        final indexInsert = currentBids.length;
        currentBids.add(newBids[indexInsert]);
        _key.currentState.insertItem(indexInsert);
      }
    } else {
      // remove N bids
      for (var i = 0; i < changeBids.nbChanges; i++) {
        final indexRemove = currentBids.length - 1;
        currentBids.removeAt(indexRemove);
        _key.currentState.removeItem(
            indexRemove,
            (context, animation) => Container(
                  width: 30,
                  height: 30,
                  color: Colors.blue,
                ));
      }
    }

    currentBids.clear();
    currentBids.addAll(widget.bidsOfPosition ?? []);
  }

  final double width = 170, height = 80;

  @override
  Widget build(BuildContext context) {
    final TableState state = widget.tableState;
    Alignment alignment;
    double dx = 0, dy = 0;
    switch (widget.posTable) {
      case AxisDirection.up:
        alignment = Alignment.topCenter;
        dx = (width + widget.widthAvatar) / 2;
        dy = 2;
        break;
      case AxisDirection.right:
        alignment = Alignment.centerRight;
        dy = (-widget.heightAvatar - height) / 2;
        break;
      case AxisDirection.down:
        alignment = Alignment.bottomRight;
        dy = (-widget.heightAvatar - height) / 2;
        break;
      case AxisDirection.left:
        alignment = Alignment.centerLeft;
        dy = (-widget.heightAvatar - height) / 2;
        break;
    }
    return Transform.translate(
      offset: Offset(dx, dy),
      child: Align(
        child: ConstrainedBox(
          constraints: BoxConstraints(maxWidth: width, maxHeight: height),
          child: AnimatedOpacity(
            opacity:
                (state != TableState.BIDDING || currentBids.isEmpty) ? 0 : 1,
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
                key: _key,
                initialItemCount: currentBids.length,
                itemBuilder: (context, index, animation) {
                  return SlideTransition(
                    position: animation
                        .drive(Tween(begin: Offset(-1, -1), end: Offset(0, 0))),
                    child: Padding(
                      padding: const EdgeInsets.only(bottom: 2.0),
                      child: Container(
                        padding: const EdgeInsets.all(2),
                        color: index % 2 == 0 ? colorGradient2 : colorGradient1,
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
  }
}

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
    return Selector<GameModel, Tuple2<TableState, List<Bid>>>(
      selector: (ctx, gameModel) =>
          Tuple2(gameModel.game.state, gameModel.game.bidsOfPosition(posTable)),
      builder: (context, tableStateBids, child) {
        return BidsAnimated(
            tableState: tableStateBids.item1,
            posTable: posTable,
            bidsOfPosition: tableStateBids.item2,
            widthAvatar: widthAvatar,
            heightAvatar: heightAvatar);
      },
    );
  }
}
