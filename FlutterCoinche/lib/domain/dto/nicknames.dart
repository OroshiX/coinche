//@dart=2.9
import 'package:coinche/domain/dto/player_position.dart';
import 'package:equatable/equatable.dart';
import 'package:json_annotation/json_annotation.dart';

part 'nicknames.g.dart';

@JsonSerializable(explicitToJson: true)
class Nicknames extends Equatable {
  @JsonKey(name: "NORTH")
  final String north;
  @JsonKey(name: "SOUTH")
  final String south;
  @JsonKey(name: "EAST")
  final String east;
  @JsonKey(name: "WEST")
  final String west;

  const Nicknames({
    this.north = "",
    this.south = "",
    this.east = "",
    this.west = "",
  });

  factory Nicknames.fromJson(Map<String, dynamic> json) =>
      _$NicknamesFromJson(json);

  Map<String, dynamic> toJson() => _$NicknamesToJson(this);

  String fromPosition(PlayerPosition playerPosition) {
    switch (playerPosition) {
      case PlayerPosition.north:
        return north;
      case PlayerPosition.south:
        return south;
      case PlayerPosition.east:
        return east;
      case PlayerPosition.west:
        return west;
    }
    return "<empty>";
  }

  @override
  List<Object> get props => [north, south, east, west];

  @override
  String toString() {
    return "north: $north, east: $east, south: $south, west: $west";
  }
}
