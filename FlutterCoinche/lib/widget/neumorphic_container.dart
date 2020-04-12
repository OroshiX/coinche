import 'package:flutter/material.dart';

class NeumorphicWidget extends StatelessWidget {
  final Widget child;
  final double borderRadius;
  final SizeShadow sizeShadow;

  NeumorphicWidget(
      {this.child, this.borderRadius = 20, this.sizeShadow = SizeShadow.LARGE});

  @override
  Widget build(BuildContext context) {
    double blur1 = 2,
        spread1 = -1.25,
        offset1 = -1.25,
        blur2 = 1.25,
        offset2 = 1.75,
        spread2 = -0.25;
    switch (sizeShadow) {
      case SizeShadow.MEDIUM:
        blur1 *= 2;
        spread1 *= 2;
        offset1 *= 2;
        blur2 *= 2;
        offset2 *= 2;
        spread2 *= 2;
        break;
      case SizeShadow.LARGE:
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
    return Container(
      decoration: BoxDecoration(
          shape: BoxShape.rectangle,
          gradient: LinearGradient(
              begin: Alignment.topCenter,
              end: Alignment.bottomCenter,
              colors: [
                Color.fromRGBO(230, 239, 245, 1),
                Color.fromRGBO(204, 223, 237, 1),
              ]),
          borderRadius: BorderRadius.circular(borderRadius),
          boxShadow: [
            BoxShadow(
              color: Colors.white,
              blurRadius: blur1,
              spreadRadius: spread1,
              offset: Offset(offset1, offset1),
            ),
            BoxShadow(
                color: Color.fromRGBO(146, 182, 216, 1),
                blurRadius: blur2,
                offset: Offset(offset2, offset2),
                spreadRadius: spread2)
          ]),
      child: child,
    );
  }
}

enum SizeShadow { SMALL, MEDIUM, LARGE }
