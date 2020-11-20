import 'package:json_annotation/json_annotation.dart';

enum TableState {
  @JsonValue("JOINING")
  joining,
  @JsonValue("DISTRIBUTING")
  distributing,
  @JsonValue("BIDDING")
  bidding,
  @JsonValue("PLAYING")
  playing,
  @JsonValue("BETWEEN_GAMES")
  betweenGames,
  @JsonValue("ENDED")
  ended
}
