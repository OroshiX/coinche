import 'package:FlutterCoinche/domain/dto/player_position.dart';
import 'package:FlutterCoinche/domain/logic/calculus.dart';
import 'package:FlutterCoinche/ui/resources/colors.dart';
import 'package:FlutterCoinche/ui/inner_shadow.dart';
import 'package:FlutterCoinche/ui/widget/dot_player.dart';
import 'package:FlutterCoinche/state/game_inherited.dart';
import 'package:FlutterCoinche/ui/widget/neumorphic_container.dart';
import 'package:FlutterCoinche/ui/widget/neumorphic_no_state.dart';
import 'package:auto_size_text/auto_size_text.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';

class PlayerAvatar extends StatelessWidget {
  final AutoSizeGroup autoSizeGroup;
  final double width, height;
  final AxisDirection posTable;

  const PlayerAvatar(
      {Key key,
      @required this.autoSizeGroup,
      @required this.width,
      @required this.height,
      @required this.posTable})
      : super(key: key);

  @override
  Widget build(BuildContext context) {
    final PlayerPosition me =
        GameInherited.of(context, aspectType: Aspects.MY_POSITION)
            .game
            .myPosition;
    final map = getPosTableToCardinal(me);
    final bool myTurn =
        GameInherited.of(context, aspectType: Aspects.NEXT_PLAYER)
                .game
                .nextPlayer ==
            map[posTable];
    final nick = GameInherited.of(context, aspectType: Aspects.NICKNAMES)
        .game
        .nicknames
        .fromPosition(map[posTable]);
    final mapColorAvatar =
        GameInherited.of(context, aspectType: Aspects.COLORS).map;
    final color = mapColorAvatar[posTable].item1;
    final avatar = mapColorAvatar[posTable].item2;
    final portrait = MediaQuery.of(context).orientation == Orientation.portrait;
    return Container(
      width: portrait ? width : 130,
      height: portrait ? height : width,
      child: NeumorphicNoStateWidget(
        sizeShadow: SizeShadow.SMALL,
        borderRadius: 2,
        pressed: true,
        child: Padding(
            padding:
                const EdgeInsets.only(top: 4.0, bottom: 4, left: 2, right: 2),
            child: portrait
                ? Stack(children: [
                    Column(
                        mainAxisSize: MainAxisSize.min,
                        mainAxisAlignment: MainAxisAlignment.start,
                        crossAxisAlignment: CrossAxisAlignment.center,
                        children: [
                          _RoundAvatar(
                            color: color,
                            pictureSvg: avatar,
                          ),
                          SizedBox(
                            height: 3,
                          ),
                          _BoxName(
                            autoSizeGroup: autoSizeGroup,
                            nick: nick,
                            color: color,
                          )
                        ]),
                    Positioned(
                        right: 0,
                        top: 0,
                        child: AnimatedOpacity(
                          opacity: myTurn ? 1 : 0,
                          duration: Duration(milliseconds: 400),
                          child: DotPlayer(
                            color: color,
                            dotSize: 5,
                          ),
                        ))
                  ])
                : Stack(
                    children: [
                      Row(
                        mainAxisSize: MainAxisSize.min,
                        mainAxisAlignment: MainAxisAlignment.center,
                        crossAxisAlignment: CrossAxisAlignment.center,
                        children: [
                          _RoundAvatar(color: color, pictureSvg: avatar),
                          SizedBox(
                            width: 3,
                          ),
                          _BoxName(
                              nick: nick,
                              autoSizeGroup: autoSizeGroup,
                              color: color),
                        ],
                      ),
                      Positioned(
                          left: 0,
                          top: 0,
                          child: AnimatedOpacity(
                            opacity: myTurn ? 1 : 0,
                            duration: Duration(
                              milliseconds: 400,
                            ),
                            child: DotPlayer(
                              color: color,
                              dotSize: 5,
                            ),
                          ))
                    ],
                  )),
      ),
    );
  }
}

const double widthName = 65, heightName = 35;

class _BoxName extends StatelessWidget {
  final String nick;

  final AutoSizeGroup autoSizeGroup;

  final Color color;

  const _BoxName(
      {Key key,
      @required this.nick,
      @required this.autoSizeGroup,
      @required this.color})
      : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Container(
      width: widthName,
      height: heightName,
      decoration: BoxDecoration(
        border: Border.all(
          width: 4,
          style: BorderStyle.solid,
          color: color,
        ),
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
              borderRadius: BorderRadius.circular(6),
            ),
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
    );
  }
}

class _RoundAvatar extends StatelessWidget {
  final Color color;
  final String pictureSvg;

  const _RoundAvatar({Key key, @required this.color, @required this.pictureSvg})
      : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Container(
      width: 58,
      height: 58,
      decoration: BoxDecoration(color: color, shape: BoxShape.circle),
      padding: EdgeInsets.all(4),
      child: InnerShadow(
        offset: Offset(2, 2),
        blur: 3,
        color: Colors.black45,
        child: InnerShadow(
          offset: Offset(-2, -1),
          blur: 2,
          color: Colors.white,
          child: Container(
            decoration:
                BoxDecoration(color: colorLightBlue, shape: BoxShape.circle),
            child: ClipOval(
              child: SvgPicture.asset(
                pictureSvg,
                fit: BoxFit.contain,
              ),
            ),
          ),
        ),
      ),
    );
  }
}
