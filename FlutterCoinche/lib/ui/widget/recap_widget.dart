import 'package:FlutterCoinche/state/game_inherited.dart';
import 'package:FlutterCoinche/ui/widget/neumorphic_container.dart';
import 'package:FlutterCoinche/ui/widget/neumorphic_no_state.dart';
import 'package:flutter/material.dart';

class RecapWidget extends StatelessWidget {

  const RecapWidget({Key key})
      : super(key: key);

  @override
  Widget build(BuildContext context) {
    final state =
        GameInherited.of(context, aspectType: Aspects.STATE).game.state;
    return NeumorphicNoStateWidget(
      pressed: false,
      borderRadius: 10,
      sizeShadow: SizeShadow.MEDIUM,
      child: Padding(
        padding: const EdgeInsets.all(8.0),
        child: Column(
          children: <Widget>[
            Text("Phase: ${state.toString().split(".").last.toLowerCase()}"),
          ],
        ),
      ),
    );
  }
}
