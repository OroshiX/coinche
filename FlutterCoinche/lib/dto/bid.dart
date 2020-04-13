import 'package:FlutterCoinche/dto/card.dart';
import 'package:FlutterCoinche/dto/player_position.dart';
import 'package:json_annotation/json_annotation.dart';

part 'bid.g.dart';

@JsonSerializable(explicitToJson: true)
class Bid {
  @JsonKey(required: true)
  String type;

//  @JsonKey(required: false)
//  int points;
//  @JsonKey(required: false)
//  CardColor color;
//  @JsonKey(required: false)
//  bool belote;
//  @JsonKey(required: false)
//  Bid annonce;
//  @JsonKey(required: false)
//  bool surcoinche;

  @JsonKey(name: "position")
  PlayerPosition position;

  Bid({this.position});

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
//    switch(type) {
//      case "SimpleBid":
//        return SimpleBid.toJson(this);
//      case "General":
//        return General.toJson(this);
//      case "Coinche":
//        return Coinche.toJson(this);
//      case "Pass":
//        return Pass.toJson(this);
//      case "Capot":
//        return Capot.toJson(this);
//    }
    return _$BidToJson(this);
  }
}

@JsonSerializable()
class SimpleBid extends Bid {
  int points;
  CardColor color;

  SimpleBid({this.points, this.color, PlayerPosition position})
      : super(position: position);

  factory SimpleBid.fromJson(Map<String, dynamic> json) =>
      _$SimpleBidFromJson(json);

  Map<String, dynamic> toJson() => _$SimpleBidToJson(this);

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
      : super(position: position);

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
      : super(position: position);

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
      : super(position: position);

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
  Pass({PlayerPosition position}) : super(position: position);

  factory Pass.fromJson(Map<String, dynamic> json) => _$PassFromJson(json);

  Map<String, dynamic> toJson() => _$PassToJson(this);

  @override
  String toString() {
    return "Pass";
  }
}
