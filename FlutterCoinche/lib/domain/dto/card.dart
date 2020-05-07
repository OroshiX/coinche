import 'package:FlutterCoinche/domain/dto/belote.dart';
import 'package:FlutterCoinche/domain/dto/player_position.dart';
import 'package:equatable/equatable.dart';
import 'package:json_annotation/json_annotation.dart';

part 'card.g.dart';

@JsonSerializable(explicitToJson: true)
class CardModel extends Equatable {
  final CardValue value;
  final CardColor color;
  final bool playable;

  CardModel({this.value, this.color, this.playable});

  factory CardModel.fromJson(Map<String, dynamic> json) =>
      _$CardModelFromJson(json);

  Map<String, dynamic> toJson() => _$CardModelToJson(this);

  @override
  String toString() {
    return "${value.toString().split(".").last}"
        " of ${color.toString().split(".").last}"
        "${playable != null && playable ? " (playable)" : ""}";
  }

  @override
  List<Object> get props => [value, color, playable];
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

int getDominanceColor(CardValue cardValue) {
  switch (cardValue) {
    case CardValue.ACE:
      return 7;
    case CardValue.TEN:
      return 6;
    case CardValue.KING:
      return 5;
    case CardValue.QUEEN:
      return 4;
    case CardValue.JACK:
      return 3;
    case CardValue.NINE:
      return 2;
    case CardValue.EIGHT:
      return 1;
    case CardValue.SEVEN:
      return 0;
  }
  return -1;
}

int getDominanceTrump(CardValue cardValue) {
  switch (cardValue) {
    case CardValue.JACK:
      return 7;
    case CardValue.NINE:
      return 6;
    case CardValue.ACE:
      return 5;
    case CardValue.TEN:
      return 4;
    case CardValue.KING:
      return 3;
    case CardValue.QUEEN:
      return 2;
    case CardValue.EIGHT:
      return 1;
    case CardValue.SEVEN:
      return 0;
  }
  return -1;
}

int compareValue(CardValue value, CardValue value2, bool isTrump) {
  if (isTrump) return getDominanceTrump(value) - getDominanceTrump(value2);
  return getDominanceColor(value) - getDominanceColor(value2);
}

enum CardColor {
  @JsonValue("SPADE")
  SPADE,
  @JsonValue("HEART")
  HEART,
  @JsonValue("CLUB")
  CLUB,
  @JsonValue("DIAMOND")
  DIAMOND,
}

@JsonSerializable(explicitToJson: true)
class CardPlayed extends Equatable {
  final CardModel card;
  final BeloteValue belote;
  final PlayerPosition position;

  CardPlayed({this.card, this.belote, this.position});

  factory CardPlayed.fromJson(Map<String, dynamic> json) =>
      _$CardPlayedFromJson(json);

  Map<String, dynamic> toJson() => _$CardPlayedToJson(this);

  @override
  List<Object> get props => [card, belote, position];
}

String getAssetImageFromColor(CardColor e) {
  var file = "";
  switch (e) {
    case CardColor.DIAMOND:
      file = "diamond.png";
      break;
    case CardColor.SPADE:
      file = "spade.png";
      break;
    case CardColor.CLUB:
      file = "club.png";
      break;
    case CardColor.HEART:
      file = "heart.png";
      break;
  }
  return "images/$file";
}

String getLetterFromValue(CardValue v) {
  switch (v) {
    case CardValue.ACE:
      return "A";
    case CardValue.KING:
      return "K";
    case CardValue.QUEEN:
      return 'Q';
    case CardValue.JACK:
      return "J";
    case CardValue.TEN:
      return "10";
    case CardValue.NINE:
      return "9";
    case CardValue.EIGHT:
      return "8";
    case CardValue.SEVEN:
      return "7";
  }
  return "";
}