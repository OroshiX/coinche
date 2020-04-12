import 'package:FlutterCoinche/dto/belote.dart';
import 'package:FlutterCoinche/dto/player_position.dart';
import 'package:json_annotation/json_annotation.dart';

part 'card.g.dart';

@JsonSerializable(explicitToJson: true)
class Card {
  CardValue value;
  CardColor color;
  bool playable;

  Card({this.value, this.color, this.playable});

  factory Card.fromJson(Map<String, dynamic> json) => _$CardFromJson(json);

  Map<String, dynamic> toJson() => _$CardToJson(this);
}

enum CardValue {
  @JsonValue(1)
  ACE,
  @JsonValue(13)
  KING,
  @JsonValue(12)
  QUEEN,
  @JsonValue(11)
  JACK,
  @JsonValue(10)
  TEN,
  @JsonValue(9)
  NINE,
  @JsonValue(8)
  EIGHT,
  @JsonValue(7)
  SEVEN
}

enum CardColor {
  @JsonValue("DIAMOND")
  DIAMOND,
  @JsonValue("SPADE")
  SPADE,
  @JsonValue("CLUB")
  CLUB,
  @JsonValue("HEART")
  HEART
}

@JsonSerializable(explicitToJson: true)
class CardPlayed {
  Card card;
  BeloteValue belote;
  PlayerPosition position;

  CardPlayed({this.card, this.belote, this.position});

  factory CardPlayed.fromJson(Map<String, dynamic> json) =>
      _$CardPlayedFromJson(json);

  Map<String, dynamic> toJson() => _$CardPlayedToJson(this);
}
