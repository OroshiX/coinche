import 'package:FlutterCoinche/business/calculus.dart';
import 'package:FlutterCoinche/dto/player_position.dart';
import 'package:FlutterCoinche/resources/colors.dart';
import 'package:FlutterCoinche/ui/inner_shadow.dart';
import 'package:FlutterCoinche/widget/dot_player.dart';
import 'package:FlutterCoinche/widget/game_inherited.dart';
import 'package:FlutterCoinche/widget/neumorphic_container.dart';
import 'package:FlutterCoinche/widget/neumorphic_no_state.dart';
import 'package:auto_size_text/auto_size_text.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:tuple/tuple.dart';

class PlayerAvatar extends StatelessWidget {
  final PlayerPosition position;
  final String nick;
  final AutoSizeGroup autoSizeGroup;

  const PlayerAvatar(
      {Key key,
      this.position,
      @required this.nick,
      @required this.autoSizeGroup})
      : super(key: key);

  @override
  Widget build(BuildContext context) {
    final bool myTurn =
        GameInherited.of(context, aspectType: Aspects.NEXT_PLAYER)
                .game
                .nextPlayer ==
            position;
    var posTable = getCardinalToPosTable(
        GameInherited.of(context, aspectType: Aspects.MY_POSITION)
            .game
            .myPosition)[position];
    return FutureBuilder<Tuple2<Color, String>>(
        initialData: Tuple2(Colors.blue, "images/chicken.svg"),
        future: posTable.getColorAndAvatar(),
        builder: (context, snapshot) => NeumorphicNoStateWidget(
              sizeShadow: SizeShadow.SMALL,
              borderRadius: 2,
              pressed: true,
              child: Padding(
                padding: const EdgeInsets.only(
                    top: 4.0, bottom: 4, left: 2, right: 2),
                child: Stack(children: [
                  Column(
                      mainAxisSize: MainAxisSize.min,
                      mainAxisAlignment: MainAxisAlignment.start,
                      crossAxisAlignment: CrossAxisAlignment.center,
                      children: [
                        Container(
                          width: 58,
                          height: 58,
                          decoration: BoxDecoration(
                              color: snapshot.data.item1,
                              shape: BoxShape.circle),
                          padding: EdgeInsets.all(4),
                          child: ClipOval(
                            child: SvgPicture.asset(
                              snapshot.data.item2,
//                              width: 50,
//                              height: 50,
                              fit: BoxFit.contain,
                            ),
                          ),
                        ),
                        SizedBox(
                          height: 3,
                        ),
                        Container(
                          width: 65,
                          height: 35,
                          decoration: BoxDecoration(
                            border: Border.all(
                                width: 4,
                                style: BorderStyle.solid,
                                color: snapshot.data.item1),
                            borderRadius: BorderRadius.circular(10),
                          ),
                          child: InnerShadow(
                            offset: Offset(2, 3),
                            color: colorShadow,
                            blur: 2,
                            child: InnerShadow(
                              offset: Offset(-2, -1),
                              color: Colors.white,
                              blur: 2,
                              child: Container(
                                padding: const EdgeInsets.all(4),
                                decoration: BoxDecoration(
                                    color: colorLightBlue,
                                    borderRadius: BorderRadius.circular(6)),
                                child: Center(
                                  child: AutoSizeText(
                                    nick,
                                    maxLines: 2,
                                    style: TextStyle(color: colorTextDark),
                                    group: autoSizeGroup,
                                    minFontSize: 8,
                                    maxFontSize: 20,
                                    overflow: TextOverflow.ellipsis,
                                    textAlign: TextAlign.center,
                                  ),
                                ),
                              ),
                            ),
                          ),
                        )
                      ]),
                  Positioned(
                      right: 0,
                      top: 0,
                      child: AnimatedOpacity(
                        opacity: myTurn ? 1 : 0,
                        duration: Duration(milliseconds: 400),
                        child: DotPlayer(
                          color: snapshot.data.item1,
                          dotSize: 5,
                        ),
                      ))
                ]),
              ),
            ));
  }
}