import 'package:json_annotation/json_annotation.dart';

part 'nicknames.g.dart';

@JsonSerializable(explicitToJson: true)
class Nicknames {
  @JsonKey(name: "NORTH")
  String north;
  @JsonKey(name: "SOUTH")
  String south;
  @JsonKey(name: "EAST")
  String east;
  @JsonKey(name: "WEST")
  String west;

  Nicknames({this.north, this.south, this.east, this.west});

  factory Nicknames.fromJson(Map<String, dynamic> json) =>
      _$NicknamesFromJson(json);

  Map<String, dynamic> toJson() => _$NicknamesToJson(this);
}
