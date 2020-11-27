import 'package:coinche/domain/dto/table_state.dart';
import 'package:coinche/state/game_model.dart';
import 'package:coinche/ui/widget/neumorphic_container.dart';
import 'package:coinche/ui/widget/neumorphic_no_state.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

class RecapWidget extends StatelessWidget {
  const RecapWidget({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return NeumorphicNoStateWidget(
      pressed: false,
      borderRadius: 10,
      sizeShadow: SizeShadow.medium,
      child: Padding(
        padding: const EdgeInsets.all(8.0),
        child: Column(
          children: <Widget>[
            Selector<GameModel, TableState>(
              selector: (ctx, gm) => gm.game.state,
              builder: (context, value, child) => Text(
                  "Phase: ${value.toString().split(".").last.toLowerCase()}"),
            ),
          ],
        ),
      ),
    );
  }
}
