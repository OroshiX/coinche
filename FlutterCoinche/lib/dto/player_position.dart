import 'package:flutter/material.dart';
import 'package:json_annotation/json_annotation.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:tuple/tuple.dart';

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

extension TablePosition on AxisDirection {
  String get prefColor => "color-${toString()}";

  String get avatar => "avatar-${toString()}";

  Future<Color> getColor() async {
    SharedPreferences preferences = await SharedPreferences.getInstance();
    return _getColor(preferences);
  }

  Color _getColor(SharedPreferences preferences) {
    final int colorPref = preferences.getInt(prefColor) ?? -1;
    if (colorPref == -1) {
      var col = Colors.black;
      switch (this) {
        case AxisDirection.up:
          col = Colors.blue;
          break;
        case AxisDirection.right:
          col = Colors.pink;
          break;
        case AxisDirection.down:
          col = Colors.amber;
          break;
        case AxisDirection.left:
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

  Future<String> getAvatarFile() async {
    SharedPreferences preferences = await SharedPreferences.getInstance();
    return _getAvatar(preferences);
  }

  String _getAvatar(SharedPreferences preferences) {
    var file = preferences.getString(avatar) ?? null;
    if (file == null) {
      final prefix = "images";
      file = "$prefix/chicken.svg";
      switch (this) {
        case AxisDirection.up:
          file = "$prefix/vampire.svg";
          break;
        case AxisDirection.right:
          file = "$prefix/face.svg";
          break;
        case AxisDirection.down:
          file = "$prefix/penguin.svg";
          break;
        case AxisDirection.left:
          file = "$prefix/chicken.svg";
          break;
      }
    }
    return file;
  }

  Future<Tuple2<Color, String>> getColorAndAvatar() async {
    SharedPreferences preferences = await SharedPreferences.getInstance();
    return Tuple2(_getColor(preferences), _getAvatar(preferences));
  }
}
