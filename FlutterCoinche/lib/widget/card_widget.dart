import 'package:FlutterCoinche/dto/card.dart' as cardModel;
import 'package:FlutterCoinche/widget/neumorphic_container.dart';
import 'package:flutter/material.dart';

class CardWidget extends StatelessWidget {
  final cardModel.Card card;

  CardWidget(this.card);

  @override
  Widget build(BuildContext context) {
    return NeumorphicWidget(
      child: Center(
          child: Material(
              color: Colors.transparent,
              child: Text(
                card.toString(),
                style: TextStyle(
                    color: Color.fromRGBO(136, 151, 176, 1),
                    fontWeight: FontWeight.bold,
                ),
                textAlign: TextAlign.center,
              ))),
    );
  }
}
