//@dart=2.9
import 'package:coinche/domain/dto/belote.dart';
import 'package:coinche/domain/dto/player_position.dart';
import 'package:equatable/equatable.dart';
import 'package:flutter/foundation.dart';
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
  ace,
  @JsonValue(13)
  king,
  @JsonValue(12)
  queen,
  @JsonValue(11)
  jack,
  @JsonValue(10)
  ten,
  @JsonValue(9)
  nine,
  @JsonValue(8)
  eight,
  @JsonValue(7)
  seven
}

extension CardValueExtension on CardValue {
  int getDominanceColor() {
    switch (this) {
      case CardValue.ace:
        return 7;
      case CardValue.ten:
        return 6;
      case CardValue.king:
        return 5;
      case CardValue.queen:
        return 4;
      case CardValue.jack:
        return 3;
      case CardValue.nine:
        return 2;
      case CardValue.eight:
        return 1;
      case CardValue.seven:
        return 0;
    }
    return -1;
  }

  int getDominanceTrump() {
    switch (this) {
      case CardValue.jack:
        return 7;
      case CardValue.nine:
        return 6;
      case CardValue.ace:
        return 5;
      case CardValue.ten:
        return 4;
      case CardValue.king:
        return 3;
      case CardValue.queen:
        return 2;
      case CardValue.eight:
        return 1;
      case CardValue.seven:
        return 0;
    }
    return -1;
  }
}

int compareValue(CardValue value, CardValue value2, {@required bool isTrump}) {
  if (isTrump) return value.getDominanceTrump() - value2.getDominanceTrump();
  return value.getDominanceColor() - value2.getDominanceColor();
}

enum CardColor {
  @JsonValue("SPADE")
  spade,
  @JsonValue("HEART")
  heart,
  @JsonValue("CLUB")
  club,
  @JsonValue("DIAMOND")
  diamond,
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

  @override
  String toString() {
    return "card: $card, by $position";
  }
}
