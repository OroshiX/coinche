import 'dart:math';

import 'package:FlutterCoinche/dto/card.dart' as cardModel;
import 'package:FlutterCoinche/resources/colors.dart';
import 'package:FlutterCoinche/widget/neumorphic_container.dart';
import 'package:flutter/material.dart';

class CardWidget extends StatelessWidget {
  final cardModel.Card card;
  final double marginValue = 5;

  CardWidget(this.card);

  @override
  Widget build(BuildContext context) {
    final image = "images/${cardModel.getAssetImageFromColor(card.color)}";
    final letter = cardModel.getLetterFromValue(card.value);
    return NeumorphicWidget(
      onTap: null,
      child: Material(
        color: Colors.transparent,
        child: Stack(
          children: [
            Center(
                child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: <Widget>[
                Image.asset(
                  image,
                  fit: BoxFit.contain,
                  width: 50,
                ),
                if (card.playable != null && card.playable)
                  Icon(Icons.check)
              ],
            )),
            Positioned(
                top: marginValue,
                left: marginValue,
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.center,
                  mainAxisAlignment: MainAxisAlignment.start,
                  mainAxisSize: MainAxisSize.min,
                  children: <Widget>[
                    Text(
                      letter,
                      style: TextStyle(
                        color: colorText,
                        fontSize: 20,
                      ),
                    ),
                    Image.asset(
                      image,
                      width: 12,
                      fit: BoxFit.contain,
                    )
                  ],
                )),
            Positioned(
                right: marginValue,
                bottom: marginValue,
                child: Transform.rotate(
                  angle: pi,
                  child: Column(children: [
                    Text(
                      letter,
                      style: TextStyle(
                          color: colorText,
                          fontSize: 20),
                    ),
                    Image.asset(
                      image,
                      width: 12,
                      fit: BoxFit.contain,
                    )
                  ]),
                ))
          ],
        ),
      ),
    );
  }
}
