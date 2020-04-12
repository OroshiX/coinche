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
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: <Widget>[
                  Image.asset(
                    "images/${cardModel.getAssetImageFromColor(card.color)}",
                    fit: BoxFit.contain,
                    width: 50,
                  ),
                  Text(
                    card.value.toString().split(".").last,
                    style: TextStyle(
                      color: Color.fromRGBO(136, 151, 176, 1),
                      fontWeight: FontWeight.bold,
                    ),
                    textAlign: TextAlign.center,
                  ),
                  if (card.playable != null && card.playable) Icon(Icons.check)
                ],
              ))),
    );
  }
}
