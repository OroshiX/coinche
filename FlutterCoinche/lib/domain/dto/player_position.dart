import 'package:coinche/domain/dto/pos_table_to_colors.dart';
import 'package:flutter/material.dart';
import 'package:json_annotation/json_annotation.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:tuple/tuple.dart';

enum PlayerPosition {
  @JsonValue("NORTH")
  north,
  @JsonValue("SOUTH")
  south,
  @JsonValue("EAST")
  east,
  @JsonValue("WEST")
  west
}

extension TablePosition on AxisDirection {
  String get prefColor => "color-${toString()}";

  String get avatar => "avatar-${toString()}";

  Color _getColor(SharedPreferences preferences) {
    final colorPref = preferences.getInt(prefColor) ?? -1;
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

  Future<void> setColor(Color color) async {
    SharedPreferences preferences = await SharedPreferences.getInstance();
    await preferences.setInt(prefColor, color.value);
  }

  String _getAvatar(SharedPreferences preferences) {
    String? file = preferences.getString(avatar);
    // ignore: unnecessary_null_comparison
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

  Future<void> setAvatar(String file) async {
    SharedPreferences preferences = await SharedPreferences.getInstance();
    await preferences.setString(avatar, file);
  }

  String simpleName() {
    return toString().split(".").last.toLowerCase();
  }
}

Future<PosTableToColor> getPosTableToColors() async {
  var preferences = await SharedPreferences.getInstance();
  var res = <AxisDirection, Tuple2<Color, String>>{};
  for (var element in AxisDirection.values) {
    res[element] =
        Tuple2(element._getColor(preferences), element._getAvatar(preferences));
  }
  return PosTableToColor(res);
}
