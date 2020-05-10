import 'dart:math';

import 'package:FlutterCoinche/domain/dto/card.dart';
import 'package:FlutterCoinche/ui/widget/card_widget.dart';
import 'package:animator/animator.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:states_rebuilder/states_rebuilder.dart';

class MovingCard extends StatelessWidget {
  final double cardWidth, cardHeight;
  final CardModel card;
  final String offsetName, rotateName;
  static const String TWEEN_TRANSLATE = "translateAnim",
      TWEEN_ROTATE = "rotateAnim";

  const MovingCard(
      {Key key,
      @required this.cardWidth,
      @required this.cardHeight,
      @required this.card,
      @required this.offsetName,
      @required this.rotateName})
      : super(key: key);

  @override
  Widget build(BuildContext context) {
    Tween<Offset> tween = Tween(begin: Offset.zero, end: Offset.zero);
    Tween<double> rotateTween = Tween(begin: 0, end: 0);
    final animatorKey = AnimatorKey(
        initialMapValue: {TWEEN_TRANSLATE: tween, TWEEN_ROTATE: rotateTween});
    void Function(AnimatorState<dynamic>) _next;
    int id = Random().nextInt(100);
    return StateBuilder<Offset>(
      models: [
        RM.get<Offset>(name: offsetName),
        RM.get<double>(name: rotateName),
      ],
      initState: (context, model) {
        Offset offsetForced = model.value;
        double angleForced = RM.get<double>(name: rotateName).state;
        tween = Tween(begin: offsetForced, end: offsetForced);
        rotateTween = Tween(begin: angleForced, end: angleForced);
        print("[movingCard - $id] init for id");
      },
//      afterInitialBuild: (context, model) => animatorKey.triggerAnimation(),
      onSetState: (context, model) {
        final currTranslate = animatorKey.getAnimation<Offset>(TWEEN_TRANSLATE);
        final currRotate = animatorKey.getAnimation<double>(TWEEN_ROTATE);
        final angle = RM.get<double>(name: rotateName);
        print(
            "[movingCard - $id] onSetState: newAngle: ${angle.value}, newOffset: ${model.value}");
        animatorKey.refreshAnimation(
          tweenMap: {
            TWEEN_TRANSLATE:
                Tween<Offset>(begin: currTranslate.value, end: model.value),
            TWEEN_ROTATE:
                Tween<double>(begin: currRotate.value, end: angle.value),
          },
        );
        _next = (s) {};
      },
      builder: (context, model) {
        print("[movingCard - $id] builder");
        return Animator(
          resetAnimationOnRebuild: true,
          endAnimationListener: _next,
          animatorKey: animatorKey,
          triggerOnInit: true,
          tweenMap: {
            TWEEN_TRANSLATE: tween,
            TWEEN_ROTATE: rotateTween,
          },
          cycles: 1,
          duration: Duration(seconds: 2),
          builder: (context, animatorState, child) {
            final offset = animatorState.getValue<Offset>(TWEEN_TRANSLATE);
            final rotate = animatorState.getValue<double>(TWEEN_ROTATE);
            return Transform.translate(
                offset: offset,
                child: Transform.rotate(angle: rotate, child: child));
          },
          child: Center(
              child: card != null
                  ? CardWidget(card: card, width: cardWidth, height: cardHeight)
                  : SizedBox()),
        );
      },
    );
  }
}
