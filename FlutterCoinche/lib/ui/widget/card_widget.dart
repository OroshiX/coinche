import 'dart:math';

import 'package:coinche/domain/dto/card.dart';
import 'package:coinche/theme/colors.dart';
import 'package:coinche/theme/dimens.dart';
import 'package:coinche/ui/widget/card_layout.dart';
import 'package:coinche/ui/widget/neumorphic_container.dart';
import 'package:coinche/ui/widget/neumorphic_no_state.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';

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
    var card = this.card;
    if (card == null) return SizedBox();
    final image = card.color.getAssetImageFromColor();
    final bigImage = card.getAssetImageFromColorAndValue();
    final letter = card.value.getLetterFromValue();
    final marginValue = getPaddingInCard(cardWidth: width);
    final xs = card.value.getXs();
    final ys = card.value.getYs();
    final rotations = card.value.getRotations();
    final sizeSuit = card.getSizeSuit() * width;
    final size = xs.length;
    assert(size == ys.length, "not the same size!");
    assert(size == rotations.length, "not the same size!!");
    return Container(
      width: width,
      height: height,
      child: Material(
        color: Colors.transparent,
        child: Stack(
          children: [
            for (var i in List.generate(size, (index) => index))
              Positioned(
                  left: xs[i] * width - sizeSuit / 2,
                  top: ys[i] * height - sizeSuit / 2,
                  child: RotatedBox(
                    quarterTurns: rotations[i] ? 0 : 2,
                    child: bigImage.endsWith(".svg")
                        ? SvgPicture.asset(
                            bigImage,
                            fit: BoxFit.contain,
                            width: sizeSuit,
                          )
                        : Image.asset(
                            bigImage,
                            fit: BoxFit.contain,
                            width: sizeSuit,
                          ),
                  )),
            if (card.playable != null &&
                displayPlayable &&
                (card.playable ?? false))
              Center(
                child: Icon(Icons.check),
              ),
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
                        color: kColorText,
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
                          color: kColorText,
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
