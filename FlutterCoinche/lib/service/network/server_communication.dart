import 'dart:async';
import 'dart:convert';
import 'dart:developer' as dev;

import 'package:coinche/.env.dart';
import 'package:coinche/domain/dto/bid.dart';
import 'package:coinche/domain/dto/card.dart';
import 'package:coinche/domain/dto/game_empty.dart';
import 'package:coinche/domain/dto/login.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/foundation.dart';
import 'package:requests/requests.dart';

class ServerCommunication {
  static final String _baseUrl = environment["baseUrl"] ?? "";

  static Future<void> sendToken(IdTokenResult idTokenResult,
      {required void Function() onSuccess, required OnError onError}) async {
    var url = '$_baseUrl/loginToken';

    var r = await Requests.post(url,
        body: idTokenResult.token, bodyEncoding: RequestBodyEncoding.PlainText);
    if (_dealHasError(r, onError: onError)) return;
    onSuccess();
  }

  static Future<void> allGames(
      {required void Function(List<GameEmpty> games) onSuccess,
      required OnError onError}) async {
    var url = '$_baseUrl/lobby/allGames';

    var r = await _get(url, onError: onError);
    if (_dealHasError(r, onError: onError)) return;
    _convertToJsonList(r,
        transform: (e) => GameEmpty.fromJson(e),
        onSuccess: onSuccess,
        onError: onError);
  }

  static Future<void> logout({
    required OnError onError,
    required void Function() onSuccess,
  }) async {
    var url = "$_baseUrl/logout";
    var r = await _post(url, onError: onError);
    if (_dealHasError(r, onError: onError)) return;
    onSuccess();
  }

  static Future<void> createGame(String gameName,
      {required OnError onError,
      required void Function(GameEmpty) onSuccess}) async {
    var url = "$_baseUrl/lobby/createGame";
    print("connect to $url");
    var r = await Requests.post(url,
        body: gameName,
        timeoutSeconds: 10,
        bodyEncoding: RequestBodyEncoding.PlainText);
    if (_dealHasError(r, onError: onError)) return;
    _convertToJson(r,
        transform: (e) => GameEmpty.fromJson(e),
        onSuccess: onSuccess,
        onError: onError);
  }

  static Future<void> playCard(
    CardModel data,
    String gameId, {
    required void Function() onSuccess,
    required OnError onError,
  }) async {
    var url = "$_baseUrl/games/$gameId/playCard";
    print("connect to $url");
    var r = await _post(url, onError: onError, body: data.toJson());
    if (_dealHasError(r, onError: onError)) return;
    onSuccess();
  }

  static Future<void> joinGame({
    required String gameId,
    String? nickname,
    required void Function() onSuccess,
    required OnError onError,
  }) async {
    var url = "$_baseUrl/lobby/joinGame";
    var r = await _post(
      url,
      queryParameters: {
        "gameId": gameId,
        if (nickname != null) "nickname": nickname
      },
      onError: onError,
    );
    if (_dealHasError(r, onError: onError)) return;
    onSuccess();
  }

  static Future<void> bid(
    Bid bid,
    String gameId, {
    required OnError onError,
    required void Function() onSuccess,
  }) async {
    var url = "$_baseUrl/games/$gameId/announceBid";
    print("connect to $url");
    var r = await _post(url, onError: onError, body: bid.toJson());
    if (_dealHasError(r, onError: onError)) return;
    onSuccess();
  }

  static Future<void> isLoggedIn(
      {required void Function(Login login) onSuccess,
      required OnError onError}) async {
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
  static Future<Response?> _post(String url,
      {Map<String, dynamic>? body,
      required OnError onError,
      Map<String, dynamic>? queryParameters}) async {
    if (kDebugMode) {
      print("Connecting to server $url [POST], body: $body");
    }
    Response? r;
    try {
      r = await Requests.post(url,
          body: body,
          timeoutSeconds: 10,
          bodyEncoding: RequestBodyEncoding.JSON,
          queryParameters: queryParameters);
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

  static Future<Response?> _get(String url, {required OnError onError}) async {
    if (kDebugMode) {
      print("Connecting to server $url [GET]");
    }
    Response? r;
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

  static bool _dealHasError(Response? r, {required OnError onError}) {
    if (r == null) return true;
    if (r.statusCode >= 400) {
      onError(_getMessage(r));
      print("Has error");
      return true;
    }
    return false;
  }

  static String _getMessage(Response? r) {
    switch (r?.statusCode) {
      case 403:
        return "Authentication required";
      default:
        return "Connection error";
    }
  }

  static void _convertToJson<T>(Response? r,
      {required T Function(dynamic e) transform,
      required void Function(T res) onSuccess,
      required OnError onError}) {
    if (r == null || r.content() == null) {
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

  static void _convertToJsonList<T>(Response? r,
      {required T Function(dynamic e) transform,
      required void Function(List<T> res) onSuccess,
      required void Function(String message) onError}) {
    try {
      List<dynamic>? list = r == null ? [] : jsonDecode(r.content());
      onSuccess(list?.map((e) => transform(e)).toList() ?? []);
    } catch (e) {
      print(e);
      onError(
          "Your application is out of date and cannot function properly anymore, please update your application to the last version");
    }
  }
  // endregion
}

typedef OnError = void Function(String message);
