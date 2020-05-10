import 'dart:math';

import 'package:FlutterCoinche/domain/dto/card.dart';
import 'package:FlutterCoinche/ui/screen/game/animated_card.dart';
import 'package:FlutterCoinche/ui/widget/card_widget.dart';
import 'package:FlutterCoinche/ui/widget/translated_transition.dart';
import 'package:flutter/material.dart';

class MoveCard extends StatefulWidget {
  final double cardWidth, cardHeight;
  final CardModel card;
  final String offsetAndRotationName;

  const MoveCard(
      {Key key,
      @required this.cardWidth,
      @required this.cardHeight,
      @required this.card,
      @required this.offsetAndRotationName})
      : assert(offsetAndRotationName != null, "name offsetAndRotation is null"),
        super(key: key);

  @override
  MoveCardState createState() => MoveCardState();
}

class MoveCardState extends State<MoveCard>
    with SingleTickerProviderStateMixin {
//  Animation<OffsetAndRotation> _animation;
  Animation<double> rotation;
  Animation<Offset> offset;
  AnimationController controller;
  static int buildNb = 0;
  int id;
  CardModel cardModel;

  @override
  void initState() {
    super.initState();
    _initAnim(OffsetAndRotation.zero());
    cardModel = widget.card;
  }

  void _initAnim(OffsetAndRotation offsetAndRotation) {
    print("init anim to $offsetAndRotation");
    controller =
        AnimationController(vsync: this, duration: const Duration(seconds: 2));
    rotation = Tween(
            begin: offsetAndRotation.rotation, end: offsetAndRotation.rotation)
        .animate(controller);
    offset = Tween<Offset>(
            begin: offsetAndRotation.offset, end: offsetAndRotation.offset)
        .animate(controller);
    controller.reset();
    controller.forward();
  }

  void setAnim(OffsetAndRotation offsetAndRotation) {
    print("setting anim to $offsetAndRotation");
    rotation =
        Tween<double>(begin: rotation.value, end: offsetAndRotation.rotation)
            .animate(controller);
    offset = Tween<Offset>(begin: offset.value, end: offsetAndRotation.offset)
        .animate(controller);
    controller.reset();
    controller.forward();
  }

  void setCard(CardModel card) {
    setState(() {
      cardModel = card;
    });
  }

  @override
  Widget build(BuildContext context) {
    id = Random().nextInt(100);
    print("MoveCard build ${++buildNb}");
    return TranslatedTransition(
      animation: offset,
      child: RotatedTransition(
        animation: rotation,
        child: Center(
          child: cardModel != null
              ? CardWidget(
                  card: cardModel,
                  width: widget.cardWidth,
                  height: widget.cardHeight)
              : Placeholder(
                  fallbackWidth: 20,
                  fallbackHeight: 30,
                  color: Colors.black,
                ),
        ),
      ),
    );
  }

  @override
  void dispose() {
    controller.dispose();
    super.dispose();
  }
}
