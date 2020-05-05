import 'package:FlutterCoinche/dto/card.dart';
import 'package:FlutterCoinche/dto/player_position.dart';
import 'package:FlutterCoinche/resources/colors.dart';
import 'package:FlutterCoinche/widget/dot_player.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:json_annotation/json_annotation.dart';
import 'package:quiver/core.dart';

part 'bid.g.dart';

@JsonSerializable(explicitToJson: true)
class Bid {
  @JsonKey(required: true)
  String type;

  @JsonKey(name: "position")
  PlayerPosition position;

  Bid({this.position, @required this.type});

  factory Bid.fromJson(Map<String, dynamic> json) {
    var type = json['type'];
    switch (type) {
      case "SimpleBid":
        return SimpleBid.fromJson(json);
      case "General":
        return General.fromJson(json);
      case "Coinche":
        return Coinche.fromJson(json);
      case "Pass":
        return Pass.fromJson(json);
      case "Capot":
        return Capot.fromJson(json);
    }
    return null;
  }

  Map<String, dynamic> toJson() {
    print("we are in toJson of SUPER");
    return _$BidToJson(this);
  }

  CardColor cardColor() {
    if (this is SimpleBid) {
      return (this as SimpleBid).color;
    }
    if (this is General) {
      return (this as General).color;
    }
    if (this is Capot) {
      return (this as Capot).color;
    }
    if (this is Coinche) {
      return (this as Coinche).annonce.cardColor();
    }
    return null;
  }

  String readableValueBid() {
    if (this is SimpleBid) {
      return (this as SimpleBid).points.toString();
    }
    if (this is General) {
      return "General${(this as General).belote ? " beloté-ed" : ""}";
    }
    if (this is Capot) {
      return "Capot${(this as Capot).belote ? " beloté-ed" : ""}";
    }
    if (this is Coinche) {
      return "${(this as Coinche).annonce.readableValueBid()}";
    }
    return "";
  }

  PlayerPosition getTaker() {
    if (this is Coinche) {
      return (this as Coinche).annonce.position;
    }
    if (this is Pass) {
      return null;
    }
    return position;
  }

  Row getReadableBidRow(double fontSize, double dotSize,
      Map<PlayerPosition, Color> cardinalToPosTable,
      {bool displayBy = true}) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.center,
      mainAxisSize: displayBy ? MainAxisSize.max : MainAxisSize.min,
      children: [
        Text(
          readableValueBid(),
          style: TextStyle(fontSize: fontSize, color: colorTextDark),
        ),
        Image.asset(
          getAssetImageFromColor(cardColor()),
          width: fontSize,
        ),
        if (this is Coinche)
          Text(
            " (x${(this as Coinche).surcoinche ? 4 : 2})",
            style: TextStyle(
              fontSize: fontSize,
              color: colorTextDark,
            ),
          ),
        if (displayBy)
          Text(
            " by ",
            style: TextStyle(
              fontSize: fontSize,
              color: colorTextDark,
            ),
          ),
        if (displayBy)
          DotPlayer(
            dotSize: dotSize,
            color: cardinalToPosTable[getTaker()],
          ),
      ],
    );
  }

  @override
  bool operator ==(other) {
    if (identical(this, other)) return true;
    if (runtimeType != other.runtimeType || other is! Bid) return false;
    var bid = other as Bid;
    if (bid.type != this.type) return false;
    if (this is SimpleBid) return this as SimpleBid == bid;
    if (this is General) return this as General == bid;
    if (this is Capot) return this as Capot == bid;
    if (this is Coinche) return this as Coinche == bid;
    if (this is Pass) return this as Pass == bid;
    return false;
  }

  @override
  int get hashCode {
    if (this is SimpleBid) return (this as SimpleBid).hashCode;
    if (this is General) return (this as General).hashCode;
    if (this is Capot) return (this as Capot).hashCode;
    if (this is Coinche) return (this as Coinche).hashCode;
    if (this is Pass) return (this as Pass).hashCode;
    return hash2(position, type);
  }
}

@JsonSerializable()
class SimpleBid extends Bid {
  int points;
  CardColor color;

  SimpleBid({this.points, this.color, PlayerPosition position})
      : super(position: position, type: "SimpleBid");

  factory SimpleBid.fromJson(Map<String, dynamic> json) =>
      _$SimpleBidFromJson(json);

  Map<String, dynamic> toJson() {
    print("We are in toJson of SimpleBid");
    return _$SimpleBidToJson(this);
  }

  @override
  String toString() {
    return "$points ${color.toString().split(".").last}";
  }

  @override
  bool operator ==(other) =>
      identical(this, other) ||
      (other is SimpleBid &&
          runtimeType == other.runtimeType &&
          position == other.position &&
          points == other.points &&
          type == other.type &&
          color == other.color);

  @override
  int get hashCode => hash4(points, position, color, type);
}

@JsonSerializable()
class General extends Bid {
  CardColor color;
  bool belote;

  General({this.color, this.belote, PlayerPosition position})
      : super(position: position, type: "General");

  factory General.fromJson(Map<String, dynamic> json) =>
      _$GeneralFromJson(json);

  Map<String, dynamic> toJson() => _$GeneralToJson(this);

  @override
  String toString() {
    return "Generale ${belote ? "belote " : ""}${color.toString().split(".").last}";
  }

  @override
  bool operator ==(other) =>
      identical(this, other) ||
      (other is General &&
          runtimeType == other.runtimeType &&
          position == other.position &&
          color == other.color &&
          type == other.type &&
          belote == other.belote);

  @override
  int get hashCode => hash4(position, color, type, belote);
}

@JsonSerializable()
class Capot extends Bid {
  CardColor color;
  bool belote;

  Capot({this.color, this.belote, PlayerPosition position})
      : super(position: position, type: "Capot");

  factory Capot.fromJson(Map<String, dynamic> json) => _$CapotFromJson(json);

  Map<String, dynamic> toJson() => _$CapotToJson(this);

  @override
  String toString() {
    return "Capot ${belote ? "belote " : ""}${color.toString().split(".").last}";
  }

  @override
  bool operator ==(other) =>
      identical(this, other) ||
      (other is Capot &&
          runtimeType == other.runtimeType &&
          position == other.position &&
          color == other.color &&
          type == other.type &&
          belote == other.belote);

  @override
  int get hashCode => hash4(position, color, type, belote);
}

@JsonSerializable()
class Coinche extends Bid {
  Bid annonce;
  bool surcoinche;

  Coinche({this.annonce, this.surcoinche, PlayerPosition position})
      : super(position: position, type: "Coinche");

  factory Coinche.fromJson(Map<String, dynamic> json) =>
      _$CoincheFromJson(json);

  Map<String, dynamic> toJson() => _$CoincheToJson(this);

  @override
  String toString() {
    return "${surcoinche ? "sur" : ""}coinche";
  }

  @override
  bool operator ==(other) =>
      identical(this, other) ||
      (other is Coinche &&
          runtimeType == other.runtimeType &&
          position == other.position &&
          annonce == other.annonce &&
          type == other.type &&
          surcoinche == other.surcoinche);

  @override
  int get hashCode => hash4(position, annonce, type, surcoinche);
}

@JsonSerializable()
class Pass extends Bid {
  Pass({PlayerPosition position}) : super(position: position, type: "Pass");

  factory Pass.fromJson(Map<String, dynamic> json) => _$PassFromJson(json);

  Map<String, dynamic> toJson() => _$PassToJson(this);

  @override
  String toString() {
    return "Pass";
  }

  @override
  bool operator ==(other) =>
      identical(this, other) ||
      (other is Pass &&
          runtimeType == other.runtimeType &&
          type == other.type &&
          position == other.position);

  @override
  int get hashCode => hash2(type, position);
}
