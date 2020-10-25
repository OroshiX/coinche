import 'package:coinche/domain/dto/table_state.dart';
import 'package:json_annotation/json_annotation.dart';

part 'game_empty.g.dart';

/// To Compile it for the first time, use "flutter pub run build_runner build"
/// after having pulled the dependencies with "flutter pub get"

@JsonSerializable(explicitToJson: true)
class GameEmpty {
  String id;
  int nbJoined;
  String name;
  String nicknameCreator;
  bool inRoom;
  @JsonKey(ignore: true)
  bool hasBots;
  static const automatedString = "AUTOMATED";

  TableState state;

  GameEmpty(
      {this.id,
      this.nbJoined,
      this.name,
      this.nicknameCreator,
      this.inRoom,
      this.state = TableState.JOINING})
      : hasBots = name.contains(automatedString);

  factory GameEmpty.fromJson(Map<String, dynamic> json) =>
      _$GameEmptyFromJson(json);

  Map<String, dynamic> toJson() => _$GameEmptyToJson(this);

  @override
  String toString() {
    return "Game [$id] $name by $nicknameCreator, $nbJoined people in room. You are ${inRoom != null && inRoom ? "" : "not "}in the room";
  }
}
