import 'package:FlutterCoinche/domain/dto/game.dart';
import 'package:FlutterCoinche/domain/extensions/game_extensions.dart';
import 'package:FlutterCoinche/ui/widget/neumorphic_container.dart';
import 'package:FlutterCoinche/ui/widget/neumorphic_no_state.dart';
import 'package:flutter/material.dart';
import 'package:states_rebuilder/states_rebuilder.dart';

class RecapWidget extends StatelessWidget {
  const RecapWidget({Key key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return StateBuilder<Game>(
      models: [RM.get<Game>()],
      tag: [Aspects.STATE],
      builder: (context, model) {
        final state = model.state.state;

        return NeumorphicNoStateWidget(
          pressed: false,
          borderRadius: 10,
          sizeShadow: SizeShadow.MEDIUM,
          child: Padding(
            padding: const EdgeInsets.all(8.0),
            child: Column(
              children: <Widget>[
                Text(
                    "Phase: ${state.toString().split(".").last.toLowerCase()}"),
              ],
            ),
          ),
        );
      },
    );
  }
}
