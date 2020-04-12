import 'package:FlutterCoinche/dto/card.dart' as cardModel;
import 'package:flutter/material.dart';

class CardWidget extends StatelessWidget {
  final cardModel.Card card;

  CardWidget(this.card);

  @override
  Widget build(BuildContext context) {
    return Container(
      decoration: BoxDecoration(
          shape: BoxShape.rectangle,
          borderRadius: BorderRadius.circular(20),
          color: Color.fromRGBO(193, 214, 233, 1),
          boxShadow: [
            BoxShadow(
                color: Colors.white,
                blurRadius: 8,
                spreadRadius: -5,
                offset: Offset(-2.5, -2.5)),
            BoxShadow(
                spreadRadius: -1,
                blurRadius: 5,
                offset: Offset(3.5, 3.5),
                color: Color.fromRGBO(146, 182, 216, 1))
          ]),
//          width: constraints.maxWidth,
//          height: constraints.maxHeight,
      child: Center(
          child: Material(
              color: Colors.transparent,
              child: Text(
                card.toString(),
                textAlign: TextAlign.center,
              ))),
    );
  }
}
