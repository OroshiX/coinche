import 'dart:convert';

import 'package:FlutterCoinche/.env.dart';
import 'package:FlutterCoinche/dto/card.dart';
import 'package:FlutterCoinche/dto/game_empty.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:requests/requests.dart';

class ServerCommunication {
  static final String _baseUrl = environment["baseUrl"];

  static Future<bool> sendToken(IdTokenResult idTokenResult) async {
    var url = '$_baseUrl/loginToken';
    print("connecting to Server $url");

    var response = await Requests.post(url,
        body: idTokenResult.token, bodyEncoding: RequestBodyEncoding.PlainText);
    if (response.hasError) {
      print("problem sending token: ${response.statusCode}");
      return false;
    }
    print("success sending token");
    return true;
  }

  static Future<List<GameEmpty>> allGames() async {
    var url = '$_baseUrl/lobby/allGames';
    print("connecting to Server $url");

    var r = await Requests.get(url, timeoutSeconds: 60);
    if (r.hasError) {
      throw "Error getting allGames: ${r.content()}";
    }
    List<dynamic> dynList = jsonDecode(r.content());

    List<GameEmpty> games = dynList.map((i) => GameEmpty.fromJson(i)).toList();
    print(games);
    return games;
  }

  static Future<bool> logout() async {
    var url = "$_baseUrl/logout";
    var response = await Requests.post(url);
    if (response.statusCode == 200) {
      return true;
    }
    return false;
  }

  static Future<GameEmpty> createGame(String gameName) async {
    var url = "$_baseUrl/lobby/createGame";
    print("connect to $url");
    var r = await Requests.post(url,
        body: gameName,
        timeoutSeconds: 60,
        bodyEncoding: RequestBodyEncoding.PlainText);
    if (r.hasError) {
      throw "Error creating game: ${r.content()}";
    }
    var game = GameEmpty.fromJson(jsonDecode(r.content()));
    return game;
  }

  static playCard(Card data, String gameId) async {
    var url = "$_baseUrl/games/$gameId/playCard";
    print("connect to $url");
    var r = await Requests.post(url,
        body: data, timeoutSeconds: 60, bodyEncoding: RequestBodyEncoding.JSON);
    if (r.hasError) {
      throw "Error playing card: ${r.content()}";
    }
  }
}
