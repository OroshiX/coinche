import 'package:FlutterCoinche/domain/dto/player_position.dart';
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
      case PlayerPosition.NORTH:
        return north;
      case PlayerPosition.SOUTH:
        return south;
      case PlayerPosition.EAST:
        return east;
      case PlayerPosition.WEST:
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
