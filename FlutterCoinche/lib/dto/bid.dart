import 'package:FlutterCoinche/dto/card.dart';
import 'package:FlutterCoinche/dto/player_position.dart';
import 'package:FlutterCoinche/resources/colors.dart';
import 'package:flutter/cupertino.dart';
import 'package:json_annotation/json_annotation.dart';

part 'bid.g.dart';

@JsonSerializable(explicitToJson: true)
class Bid {
  @JsonKey(required: true)
  String type;

  @JsonKey(name: "position")
  PlayerPosition position;

  Bid({this.position, @required this.type});

  factory Bid.fromJson(Map<String, dynamic> json) {
    print("trying to change bid $json to Bid");
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
}
