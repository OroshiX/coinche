import 'package:flutter/material.dart';
import 'package:json_annotation/json_annotation.dart';
import 'package:shared_preferences/shared_preferences.dart';

enum PlayerPosition {
  @JsonValue("NORTH")
  NORTH,
  @JsonValue("SOUTH")
  SOUTH,
  @JsonValue("EAST")
  EAST,
  @JsonValue("WEST")
  WEST
}

extension PlayExtension on PlayerPosition {
  String get prefColor => "color-${toString()}";

  Future<Color> getColor() async {
    SharedPreferences preferences = await SharedPreferences.getInstance();
    final int colorPref = preferences.getInt(prefColor) ?? -1;
    if (colorPref == -1) {
      var col = Colors.black;
      switch (this) {
        case PlayerPosition.NORTH:
          col = Colors.blue;
          break;
        case PlayerPosition.SOUTH:
          col = Colors.amber;
          break;
        case PlayerPosition.EAST:
          col = Colors.pink;
          break;
        case PlayerPosition.WEST:
          col = Colors.lightGreen;
          break;
      }
      preferences.setInt(prefColor, col.value);
      return col;
    }
    return Color(colorPref);
  }

  setColor(Color color) async {
    SharedPreferences preferences = await SharedPreferences.getInstance();
    preferences.setInt(prefColor, color.value);
  }
}
