import 'package:json_annotation/json_annotation.dart';

enum BeloteValue {
  @JsonValue("BELOTE")
  BELOTE,
  @JsonValue("REBELOTE")
  REBELOTE,
  @JsonValue(null)
  NONE
}
