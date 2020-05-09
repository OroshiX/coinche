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
    final animatorKey = AnimatorKey();
    Tween<Offset> tween;
    Tween<double> rotateTween;
    void Function(AnimatorState<dynamic>) _next;
    return StateBuilder<Offset>(
      models: [
        RM.get<Offset>(name: offsetName),
        RM.get<double>(name: rotateName),
      ],
      initState: (context, model) {
        tween = Tween(begin: Offset(0, 0), end: model.value);
        rotateTween = Tween(begin: 0, end: 2 * pi);
      },
      onSetState: (context, model) {
        final currTranslate = animatorKey.getAnimation<Offset>(TWEEN_TRANSLATE);
        final currRotate = animatorKey.getAnimation<double>(TWEEN_ROTATE);
        final angle = RM.get<double>(name: rotateName);
        animatorKey.refreshAnimation(
          tweenMap: {
            TWEEN_TRANSLATE:
                Tween<Offset>(begin: currTranslate.value, end: model.value),
            TWEEN_ROTATE:
                Tween<double>(begin: currRotate.value, end: angle.value),
          },
        );
        _next = (s){} ;
        animatorKey.triggerAnimation(restart: false);
      },
      builder: (context, model) => Animator(
        endAnimationListener: _next,
        animatorKey: animatorKey,
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
        child: Center(child: CardWidget(card: card, width: cardWidth, height: cardHeight)),
      ),
    );
  }
}
