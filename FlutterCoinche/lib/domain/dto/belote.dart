import 'package:json_annotation/json_annotation.dart';

enum BeloteValue {
  @JsonValue("BELOTE")
  belote,
  @JsonValue("REBELOTE")
  rebelote,
  @JsonValue(null)
  none
}
