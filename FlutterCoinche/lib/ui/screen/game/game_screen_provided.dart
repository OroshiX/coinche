import 'package:coinche/domain/dto/player_position.dart';
import 'package:coinche/domain/dto/pos_table_to_colors.dart';
import 'package:coinche/ui/resources/colors.dart';
import 'package:coinche/ui/screen/game/table/table_widget.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:provider/provider.dart';
import 'package:tuple/tuple.dart';

class GameScreenProvided extends StatelessWidget {
  static const routeName = "/gameScreenProvided";

  @override
  Widget build(BuildContext context) {
    SystemChrome.setEnabledSystemUIOverlays([]);
    return Material(
      child: WillPopScope(
        onWillPop: () async {
          return (await _quit(context)) ?? false;
        },
        child: Container(
          color: colorLightBlue,
          child: FutureProvider<PosTableToColor>(
            builder: (context, child) => _top(context),
            lazy: false,
            initialData: PosTableToColor({
              AxisDirection.down: Tuple2(Colors.red, "images/vampire.svg"),
              AxisDirection.right: Tuple2(Colors.redAccent, "images/vampire.svg"),
              AxisDirection.up: Tuple2(Colors.pink, "images/vampire.svg"),
              AxisDirection.left: Tuple2(Colors.pinkAccent, "images/vampire.svg"),
            }),
            create: (BuildContext context) => getPosTableToColors(),
          ),
        ),
      ),
    );
  }

  Future _quit(BuildContext context) {
    return showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: Text("Exit game?"),
        actions: [
          FlatButton(
              onPressed: () => Navigator.of(context).pop(false),
              child: Text("No, stay here")),
          FlatButton(
              onPressed: () {
                Navigator.of(context).pop(true);
                SystemChrome.restoreSystemUIOverlays();
              },
              child: Text("Yes, exit!"))
        ],
      ),
    );
  }

  Widget _top(BuildContext context) {
    return TableWidget(quit: _quit);
  }
}
