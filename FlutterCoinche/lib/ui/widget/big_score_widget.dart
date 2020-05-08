import 'package:FlutterCoinche/domain/dto/game.dart';
import 'package:FlutterCoinche/domain/dto/table_state.dart';
import 'package:FlutterCoinche/domain/extensions/game_extensions.dart';
import 'package:FlutterCoinche/ui/resources/colors.dart';
import 'package:FlutterCoinche/ui/widget/neumorphic_container.dart';
import 'package:FlutterCoinche/ui/widget/neumorphic_no_state.dart';
import 'package:FlutterCoinche/ui/widget/only_score.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:states_rebuilder/states_rebuilder.dart';

class BigScoreWidget extends StatelessWidget {
  final Future<dynamic> Function(BuildContext context) quit;

  const BigScoreWidget({Key key, @required this.quit}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return StateBuilder<Game>(
      models: [RM.get<Game>()],
      tag: [Aspects.STATE, Aspects.SCORE],
      builder: (context, model) {
        final state = model.state.state;
        if (state != TableState.BETWEEN_GAMES && state != TableState.ENDED)
          return SizedBox();
        final score = model.state.score;
        final bool won = model.state.isWon();
        return LayoutBuilder(
          builder: (context, constraints) {
            return Stack(children: [
              IgnorePointer(
                child: Container(
                  width: constraints.maxWidth,
                  height: constraints.maxHeight,
                  color: colorShadow.withAlpha(125),
                ),
              ),
              Center(
                child: NeumorphicNoStateWidget(
                  child: Padding(
                    padding: const EdgeInsets.all(8.0),
                    child: Column(
                      mainAxisSize: MainAxisSize.min,
                      children: [
                        Text(
                          "Score",
                          style: TextStyle(color: colorTextDark, fontSize: 20),
                        ),
                        SizedBox(
                          height: 10,
                        ),
                        if (state == TableState.ENDED)
                          Text(
                            "You ${won ? "won! 😄" : "lost 😢\nI promise you'll get your revenge! 😈"}",
                            style: TextStyle(
                                color: colorTextDark, fontSize: won ? 20 : 18),
                          ),
                        if (state == TableState.ENDED)
                          SizedBox(
                            height: 10,
                          ),
                        OnlyScoreWidget(currentBid: null, minWidth: 140),
                        SizedBox(
                          height: 10,
                        ),
                        Row(
                          mainAxisSize: MainAxisSize.min,
                          children: [
                            NeumorphicWidget(
                              sizeShadow: SizeShadow.MEDIUM,
                              onTap: () async {
                                if (state == TableState.BETWEEN_GAMES) {
                                  var isQuit = (await quit(context)) ?? false;
                                  if (isQuit) Navigator.of(context).pop();
                                } else {
                                  Navigator.of(context).pop();
                                }
                              },
                              child: Padding(
                                padding: const EdgeInsets.all(8.0),
                                child: Icon(
                                  Icons.exit_to_app,
                                  color: colorTextDark,
                                ),
                              ),
                            )
                          ],
                        ),
                      ],
                    ),
                  ),
                ),
              ),
            ]);
          },
        );
      },
    );
  }
}
