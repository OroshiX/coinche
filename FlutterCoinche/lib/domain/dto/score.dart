import 'package:equatable/equatable.dart';
import 'package:json_annotation/json_annotation.dart';

part 'score.g.dart';

@JsonSerializable(explicitToJson: true)
class Score extends Equatable{
  final int northSouth;
  final int eastWest;

  Score({this.northSouth, this.eastWest});

  factory Score.fromJson(Map<String, dynamic> json) => _$ScoreFromJson(json);

  Map<String, dynamic> toJson() => _$ScoreToJson(this);

  @override
  List<Object> get props => [northSouth, eastWest];
}
