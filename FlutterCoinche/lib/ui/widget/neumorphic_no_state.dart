import 'package:coinche/theme/colors.dart';
import 'package:coinche/ui/widget/neumorphic_container.dart';
import 'package:flutter/material.dart';

const double kBlur1 = 2,
    kSpread1 = -1.25,
    kOffset1 = -1.25,
    kBlur2 = 1.25,
    kOffset2 = 1.75,
    kSpread2 = -0.25;

BoxDecoration buildNeumorphicDecoration({
  double borderRadius = 20,
  SizeShadow sizeShadow = SizeShadow.medium,
  bool pressed = false,
}) {
  var blur1 = kBlur1,
      spread1 = kSpread1,
      offset1 = kOffset1,
      blur2 = kBlur2,
      offset2 = kOffset2,
      spread2 = kSpread2;
  switch (sizeShadow) {
    case SizeShadow.medium:
      blur1 *= 2;
      spread1 *= 2;
      offset1 *= 2;
      blur2 *= 2;
      offset2 *= 2;
      spread2 *= 2;
      break;
    case SizeShadow.large:
      blur1 *= 4;
      spread1 *= 4;
      offset1 *= 4;
      blur2 *= 4;
      offset2 *= 4;
      spread2 *= 4;
      break;
    default:
      break;
  }
  final boxShadowLight = BoxShadow(
    color: Colors.white,
    blurRadius: blur1,
    spreadRadius: spread1,
    offset: Offset(offset1, offset1),
  );
  return BoxDecoration(
      shape: BoxShape.rectangle,
      gradient: LinearGradient(
          begin: Alignment.topCenter,
          end: Alignment.bottomCenter,
          colors: [
            pressed ? kColorGradient2 : kColorGradient1,
            kColorGradientMiddle,
            kColorGradientMiddle,
            pressed ? kColorGradient1 : kColorGradient2,
          ]),
      borderRadius: BorderRadius.circular(borderRadius),
      boxShadow: pressed
          ? [
              boxShadowLight,
              BoxShadow(
                  color: Color.lerp(kColorShadow, Colors.white, 0.4)!,
                  blurRadius: blur2 / 4,
                  spreadRadius: spread2 / 4,
                  offset: Offset(offset2 / 8, offset2 / 8))
            ]
          : [
              boxShadowLight,
              BoxShadow(
                  color: kColorShadow,
                  blurRadius: blur2,
                  offset: Offset(offset2, offset2),
                  spreadRadius: spread2)
            ]);
}

class NeumorphicNoStateWidget extends StatelessWidget {
  final Widget child;
  final double borderRadius;
  final SizeShadow sizeShadow;
  final bool pressed;

  const NeumorphicNoStateWidget(
      {Key? key,
      this.borderRadius = 20,
      this.sizeShadow = SizeShadow.large,
      this.pressed = false,
      required this.child})
      : super(key: key);

  @override
  Widget build(BuildContext context) {
    return AnimatedContainer(
      curve: Curves.easeOutCirc,
      decoration: buildNeumorphicDecoration(
          sizeShadow: sizeShadow, pressed: pressed, borderRadius: borderRadius),
      duration: Duration(milliseconds: 150),
      child: child,
    );
  }
}
