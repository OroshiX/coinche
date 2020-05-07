import 'package:flutter/material.dart';

class DotPlayer extends StatelessWidget {
  final double dotSize;
  final Color color;

  const DotPlayer({Key key, this.dotSize, this.color}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Container(
      height: dotSize,
      width: dotSize,
      decoration: BoxDecoration(color: color, shape: BoxShape.circle),
    );
  }
}
