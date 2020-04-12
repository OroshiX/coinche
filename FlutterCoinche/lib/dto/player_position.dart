import 'package:json_annotation/json_annotation.dart';

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
