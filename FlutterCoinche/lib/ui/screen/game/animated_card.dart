import 'package:flutter/material.dart';

class RotatedTransition extends AnimatedWidget {
  final Widget child;

  RotatedTransition(
      {Key key, @required this.child, @required Animation<double> animation})
      : super(key: key, listenable: animation);

  @override
  Widget build(BuildContext context) {
    final Animation<double> animation = listenable;
    return Transform.rotate(
      angle: animation.value,
      child: child,
    );
  }
}

class OffsetAndRotation {
  final Offset offset;
  final double rotation;

  const OffsetAndRotation(this.offset, this.rotation);

  const OffsetAndRotation.zero()
      : offset = Offset.zero,
        rotation = 0;

  OffsetAndRotation operator +(OffsetAndRotation other) {
    return OffsetAndRotation(
        this.offset + other.offset, this.rotation + other.rotation);
  }

  OffsetAndRotation operator *(OffsetAndRotation other) {
    return OffsetAndRotation(
        Offset(
            this.offset.dx * other.offset.dx, this.offset.dy * other.offset.dy),
        this.rotation + other.rotation);
  }

  OffsetAndRotation operator -(OffsetAndRotation other) {
    return OffsetAndRotation(
        this.offset - other.offset, this.rotation - other.rotation);
  }

  @override
  String toString() {
    return "offset: $offset, rotation: $rotation";
  }
}
