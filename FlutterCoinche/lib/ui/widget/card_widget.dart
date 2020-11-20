import 'dart:math';

import 'package:coinche/domain/dto/card.dart';
import 'package:coinche/ui/resources/colors.dart';
import 'package:coinche/ui/resources/dimens.dart';
import 'package:coinche/ui/widget/neumorphic_container.dart';
import 'package:coinche/ui/widget/neumorphic_no_state.dart';
import 'package:flutter/material.dart';

class CardWidget extends StatelessWidget {
  final CardModel? card;
  final bool displayPlayable;
  final double width, height;

  CardWidget(
      {required this.card,
      this.displayPlayable = true,
      required this.width,
      required this.height});

  @override
  Widget build(BuildContext context) {
    final big = isBigCardWidth(cardWidth: width);
    return NeumorphicNoStateWidget(
      sizeShadow: big ? SizeShadow.large : SizeShadow.medium,
      borderRadius: big ? 20 : 10,
      pressed: false,
      child: CardContent(
        width: width,
        height: height,
        card: card,
        displayPlayable: displayPlayable,
      ),
    );
  }
}

class CardContent extends StatelessWidget {
  final CardModel? card;
  final bool displayPlayable;
  final double width, height;

  const CardContent(
      {Key? key,
      required this.card,
      this.displayPlayable = true,
      required this.width,
      required this.height})
      : super(key: key);

  @override
  Widget build(BuildContext context) {
    final image = getAssetImageFromColor(card?.color);
    final letter = getLetterFromValue(card?.value);
    final marginValue = getPaddingInCard(cardWidth: width);

    return Container(
      width: width,
      height: height,
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
                  width: getSizeSuitIcon(cardWidth: width),
                ),
                if (card?.playable != null &&
                    displayPlayable &&
                    (card?.playable ?? false))
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
                        fontSize: getTextSizeCard(cardWidth: width),
                      ),
                    ),
                    Image.asset(
                      image,
                      width: getSizeSuitMiniIcon(cardWidth: width),
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
                          fontSize: getTextSizeCard(cardWidth: width)),
                    ),
                    Image.asset(
                      image,
                      width: getSizeSuitMiniIcon(cardWidth: width),
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
