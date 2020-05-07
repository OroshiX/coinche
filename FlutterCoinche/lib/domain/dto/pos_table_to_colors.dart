import 'package:flutter/material.dart';
import 'package:tuple/tuple.dart';

@immutable
class PosTableToColor {
  final Map<AxisDirection, Tuple2<Color, String>> value;

  PosTableToColor(this.value);
}
