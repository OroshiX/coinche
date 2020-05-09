import 'dart:math';

import 'package:FlutterCoinche/domain/dto/card.dart';
import 'package:FlutterCoinche/ui/widget/card_widget.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';

class MovingCards extends StatefulWidget {
  @override
  _MovingCardsState createState() => _MovingCardsState();
}

class _MovingCardsState extends State<MovingCards>
    with SingleTickerProviderStateMixin {
  AnimationController _animationController;
  Animation<double> anim;
  bool running = false;

  @override
  void initState() {
    super.initState();
    _animationController =
        AnimationController(vsync: this, duration: Duration(seconds: 3))
          ..addStatusListener((status) {
            print("Status: $status");
            setState(() {
              switch (status) {
                case AnimationStatus.dismissed:
                  running = false;
                  break;
                case AnimationStatus.forward:
                  running = true;
                  break;
                case AnimationStatus.reverse:
                  running = true;
                  break;
                case AnimationStatus.completed:
                  running = true;
                  break;
              }
            });
          });
    anim = Tween(begin: -100.0, end: 200.0).animate(_animationController);
  }

  @override
  Widget build(BuildContext context) {
    return Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          AnimatedBuilder(
            builder: (context, child) {
              return Transform.translate(
                offset: Offset(anim.value, 0),
                child: RotationTransition(
                  turns: _animationController,
                  child: CardWidget(
                      card: CardModel(color: CardColor.DIAMOND, value: CardValue.SEVEN),
                      width: 30,
                      height: 50),
                ),
              );
            },
            animation: _animationController,
          ),
          FlatButton(
              onPressed: () {
                if (running) {
                  _animationController.stop(canceled: false);
                  _animationController.reset();
                } else {
                  _animationController.forward();
                }
              },
              child: Text(running ? "Stop" : "Start")),
          FlatButton(
              onPressed: () {
                _animationController.animateTo(pi);
                _animationController.fling();
              },
              child: Text("To / fling")),
        ],
      ),
    );
  }
}
