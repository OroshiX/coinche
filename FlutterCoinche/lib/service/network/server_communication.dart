import 'dart:async';
import 'dart:convert';

import 'package:coinche/.env.dart';
import 'package:coinche/domain/dto/bid.dart';
import 'package:coinche/domain/dto/card.dart';
import 'package:coinche/domain/dto/game_empty.dart';
import 'package:coinche/domain/dto/login.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/foundation.dart';
import 'package:requests/requests.dart';
import 'dart:developer' as dev;

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

  static Future<bool> playCard(CardModel data, String gameId) async {
    var url = "$_baseUrl/games/$gameId/playCard";
    print("connect to $url");
    var r = await Requests.post(url,
        body: data, timeoutSeconds: 60, bodyEncoding: RequestBodyEncoding.JSON);
    if (r.hasError) {
      throw r.json();
    }
    return true;
  }

  static Future<void> joinGame({
    String gameId,
    String nickname,
    @required void Function() onSuccess,
    @required OnError onError,
  }) async {
    var url = "$_baseUrl/lobby/joinGame";
    var r = await Requests.post(url,
        queryParameters: {
          "gameId": gameId,
          if (nickname != null) "nickname": nickname
        },
        timeoutSeconds: 60);
    if (r.hasError) {
      onError(r.content());
    }
  }

  static Future<bool> bid(Bid bid, String gameId) async {
    var url = "$_baseUrl/games/$gameId/announceBid";
    print("connect to $url");
    var r = await Requests.post(url,
        body: bid.toJson(),
        timeoutSeconds: 60,
        bodyEncoding: RequestBodyEncoding.JSON);
    if (r.hasError) {
      throw r.json();
    }
    return true;
  }

  static Future<void> isLoggedIn(
      {@required void Function(Login login) onSuccess,
      @required OnError onError}) async {
    var url = "$_baseUrl/lobby/isLoggedIn";

    var r = await _get(url, onError: onError);
    if (_dealHasError(r, onError: onError)) return;
    _convertToJson(
      r,
      transform: (e) => Login.fromJson(e),
      onSuccess: onSuccess,
      onError: onError,
    );
  }

  // region helpers
  static Future<Response> _post(String url,
      {Map<String, dynamic> body, @required OnError onErrorFunction}) async {
    if (kDebugMode) {
      print("Connecting to server $url [POST], body: $body");
    }
    Response r;
    try {
      r = await Requests.post(url, body: body, timeoutSeconds: 10);
      if (kDebugMode) {
        dev.log("content for $url: ${r.content()}");
      }
      if (r.hasError) {
        onErrorFunction(r.content());
      }
    } catch (e) {
      onErrorFunction("Connection error");
    }
    return r;
  }

  static Future<Response> _get(String url, {@required OnError onError}) async {
    if (kDebugMode) {
      print("Connecting to server $url [GET]");
    }
    Response r;
    try {
      r = await Requests.get(url, timeoutSeconds: 10);
      if (kDebugMode) {
        dev.log("content for $url: ${r.content()}");
      }
      if (r.hasError) {
        onError(r.content());
      }
    } catch (e) {
      onError("Connection error");
    }
    return r;
  }

  static bool _dealHasError(Response r, {@required OnError onError}) {
    if (r == null) return true;
    if (r.statusCode >= 400) {
      onError(_getMessage(r));
      print("Has error");
      return true;
    }
    return false;
  }

  static String _getMessage(Response r) {
    switch (r.statusCode) {
      case 403:
        return "Authentication required";
      default:
        return "Connection error";
    }
  }

  static void _convertToJson<T>(Response r,
      {@required T Function(dynamic e) transform,
      @required void Function(T res) onSuccess,
      @required OnError onError}) {
    if (r.content() == null) {
      onError("No content");
      return;
    }
    try {
      onSuccess(transform(jsonDecode(r.content())));
    } catch (e) {
      print(e);
      onError(
          "Your application is out of date and cannot function properly anymore, please update your application to the last version");
    }
  }

  static void _convertToJsonList<T>(Response r,
      {@required T Function(dynamic e) transform,
      @required void Function(List<T> res) onSuccess,
      @required void Function(String message) onError}) {
    try {
      List<dynamic> list = jsonDecode(r.content());
      onSuccess(list?.map((e) => transform(e))?.toList() ?? []);
    } catch (e) {
      print(e);
      onError(
          "Your application is out of date and cannot function properly anymore, please update your application to the last version");
    }
  }
  // endregion
}

typedef OnError = void Function(String message);
