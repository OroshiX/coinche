import 'package:json_annotation/json_annotation.dart';

part 'game.g.dart';

/// To Compile it for the first time, use "flutter pub run build_runner build"
/// after having pulled the dependencies with "flutter pub get"

@JsonSerializable()
class Game {
  String id;
  int nbJoined;
  String name;
  String nicknameCreator;
  bool inRoom;

  Game(this.id, this.nbJoined, this.name, this.nicknameCreator, this.inRoom);

  factory Game.fromJson(Map<String, dynamic> json) => _$GameFromJson(json);

  Map<String, dynamic> toJson() => _$GameToJson(this);
}
