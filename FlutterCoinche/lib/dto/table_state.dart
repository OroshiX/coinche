import 'package:json_annotation/json_annotation.dart';

enum TableState {
  @JsonValue("JOINING")
  JOINING,
  @JsonValue("DISTRIBUTING")
  DISTRIBUTING,
  @JsonValue("BIDDING")
  BIDDING,
  @JsonValue("PLAYING")
  PLAYING,
  @JsonValue("BETWEEN_GAMES")
  BETWEEN_GAMES,
  @JsonValue("ENDED")
  ENDED
}
