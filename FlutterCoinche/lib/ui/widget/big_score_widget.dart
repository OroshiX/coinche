import 'package:coinche/domain/dto/table_state.dart';
import 'package:coinche/domain/extensions/game_extensions.dart';
import 'package:coinche/state/game_model.dart';
import 'package:coinche/theme/colors.dart';
import 'package:coinche/ui/widget/neumorphic_container.dart';
import 'package:coinche/ui/widget/neumorphic_no_state.dart';
import 'package:coinche/ui/widget/only_score.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:tuple/tuple.dart';

class BigScoreWidget extends StatelessWidget {
  final Future<dynamic> Function(BuildContext context) quit;

  const BigScoreWidget({Key? key, required this.quit}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Selector<GameModel, Tuple2<TableState, bool>>(
      selector: (ctx, gm) => Tuple2(gm.game.state, gm.game.isWon()),
      builder: (context, model, child) {
        final state = model.item1;
        if (state != TableState.betweenGames && state != TableState.ended) {
          return SizedBox();
        }
        final bool won = model.item2;
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
                        if (state == TableState.ended)
                          Text(
                            "You ${won ? "won! 😄" : "lost 😢\nI promise you'll get your revenge! 😈"}",
                            style: TextStyle(
                                color: colorTextDark, fontSize: won ? 20 : 18),
                          ),
                        if (state == TableState.ended)
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
                              sizeShadow: SizeShadow.medium,
                              onTap: () async {
                                if (state == TableState.betweenGames) {
                                  var isQuit = (await quit(context)) ?? false;
                                  if (isQuit) Navigator.of(context)?.pop();
                                } else {
                                  Navigator.of(context)?.pop();
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
