import 'package:json_annotation/json_annotation.dart';

part 'login.g.dart';

@JsonSerializable(explicitToJson: true)
class Login {
  final String nickName;
  final bool isLoggedIn;

  final String uid;

  const Login({this.nickName, this.isLoggedIn, this.uid});

  factory Login.fromJson(Map<String, dynamic> json) => _$LoginFromJson(json);

  Map<String, dynamic> toJson() => _$LoginToJson(this);
}
