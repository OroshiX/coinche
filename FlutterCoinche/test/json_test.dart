import 'dart:convert';

import 'package:coinche/domain/dto/game_empty.dart';
import 'package:flutter_test/flutter_test.dart';

void main() {
  test("Parses allGames.json", () {
    const jsonString =
        """[{"id":"NGfygSoelRxeF04SHcaU","nbJoined":4,"name":"AUTOMATEDme","nicknameCreator":"Jess","state":"PLAYING","inRoom":true},{"id":"hOnpWLvxKSEjD9ppz7vT","nbJoined":1,"name":"gameboy","nicknameCreator":"ieok","state":"JOINING","inRoom":false},{"id":"pcxbJ6Ehd27W7F90uihW","nbJoined":1,"name":"AAAutomated","nicknameCreator":"ieok","state":"JOINING","inRoom":false}]""";
    expect(
        (jsonDecode(jsonString) as List<dynamic>)
            .map((e) => GameEmpty.fromJson(e))
            .toList()
            .first
            .name,
        "AUTOMATEDme");
  });
}
