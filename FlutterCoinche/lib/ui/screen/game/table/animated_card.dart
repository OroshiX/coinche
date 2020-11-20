import 'package:flutter/material.dart';

class RotatedTransition extends AnimatedWidget {
  final Widget child;
  final Animation<double> animation;
  RotatedTransition({Key? key, required this.child, required this.animation})
      : super(key: key, listenable: animation);

  @override
  Widget build(BuildContext context) {
    return Transform.rotate(
      angle: animation.value,
      child: child,
    );
  }
}

class OffsetAndRotation {
  final Offset offset;
  final double rotation;
  final int timestamp;
  const OffsetAndRotation(this.offset, this.rotation, this.timestamp);

  const OffsetAndRotation.zero(this.timestamp)
      : offset = Offset.zero,
        rotation = 0;

  @override
  String toString() {
    return "offset: $offset, rotation: $rotation, requested at ${timestamp % 1000}";
  }
}
