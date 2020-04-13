import 'package:FlutterCoinche/resources/colors.dart';
import 'package:FlutterCoinche/widget/neumorphic_container.dart';
import 'package:flutter/material.dart';

class InkwellNeumorphicButton extends StatelessWidget {
  final SizeShadow sizeShadow;

  final double borderRadius;

  const InkwellNeumorphicButton.round(
      {@required this.onTap, this.child, this.sizeShadow = SizeShadow.SMALL})
      : isRound = true,
        borderRadius = null;

  final void Function() onTap;
  final bool isRound;
  final Widget child;

  const InkwellNeumorphicButton.rrect(
      {@required this.onTap,
      this.child,
      this.sizeShadow = SizeShadow.SMALL,
      @required this.borderRadius})
      : isRound = false;

  const InkwellNeumorphicButton(
      {Key key,
      @required this.onTap,
      this.isRound = false,
      this.child,
      this.borderRadius,
      this.sizeShadow})
      : super(key: key);

  @override
  Widget build(BuildContext context) {
    final material = Material(
      color: Colors.transparent,
      child: InkWell(
        splashColor: colorShadow,
        onTap: onTap,
        child: child,
      ),
    );
    var clip;
    if (isRound) {
      clip = ClipOval(child: material);
    } else {
      clip = ClipRRect(
        borderRadius: BorderRadius.circular(borderRadius),
        child: material,
      );
    }
    return NeumorphicWidget(
      sizeShadow: sizeShadow,
      borderRadius: borderRadius != null ? borderRadius : 20,
      child: clip,
    );
  }
}
