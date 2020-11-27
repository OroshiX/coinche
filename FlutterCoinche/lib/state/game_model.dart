import 'dart:async';
import 'dart:collection';
import 'dart:developer' as dev;

import 'package:audioplayers/audio_cache.dart';
import 'package:audioplayers/audioplayers.dart';
import 'package:cloud_firestore/cloud_firestore.dart';
import 'package:coinche/domain/dto/bid.dart';
import 'package:coinche/domain/dto/card.dart';
import 'package:coinche/domain/dto/game.dart';
import 'package:coinche/domain/dto/game_empty.dart';
import 'package:coinche/domain/extensions/cards_extension.dart';
import 'package:coinche/domain/extensions/game_extensions.dart';
import 'package:coinche/service/network/server_communication.dart';
import 'package:flutter/foundation.dart';

class GameModel extends ChangeNotifier {
  String? _error;
  String? get error => _error;
  String? _success;
  String? get success => _success;
  set success(String? success) {
    _success = success;
    notifyListeners();
  }

  set error(String? value) {
    _error = value;
    notifyListeners();
  }

  late AudioCache _audioCache;

  Game _game = Game();

  Game get game => _game;

  set game(Game value) {
    _game = value;
    _game.cards.sortCards(_game.state, _game.currentBid);
    notifyListeners();
  }

  List<GameEmpty> _games = [];

  List<GameEmpty> get allGames => UnmodifiableListView(_games);
  StreamSubscription<Game>? _subscription;

  GameModel() {
    _audioCache = AudioCache(
        prefix: "assets/sounds/",
        fixedPlayer: AudioPlayer(mode: PlayerMode.LOW_LATENCY));
    _audioCache.loadAll(["bad.mp3", "click.mp3", "clic.mp3", "buttonPush.mp3"]);
  }

  set allGames(List<GameEmpty> value) {
    _games = value;
    notifyListeners();
  }

  void clearError() {
    error = null;
  }

  Future<void> refreshGames() {
    return ServerCommunication.allGames(
      onSuccess: (games) {
        allGames = games;
      },
      onError: (message) {
        error = message;
      },
    );
  }

  void joinGame(
      {required String gameId,
      required String? userUid,
      required void Function() onSuccess,
      required OnError onError}) {
    ServerCommunication.joinGame(
      gameId: gameId,
      onError: onError,
      onSuccess: () {
        changeGame(idGame: gameId, userUid: userUid);
        onSuccess();
      },
    );
  }

  /// Call setUser before this
  void changeGame({required String idGame, required String? userUid}) {
    _subscription?.cancel();

    _subscription = Firestore.instance
        .collection('playersSets')
        .document(idGame)
        .collection('players')
        .document(userUid)
        .snapshots()
        .map((event) => Game.fromJson(event.data))
        .listen((event) {
      if (kDebugMode) {
        dev.log("Change of game aspects: ${event.different(game)}");
      }
      game = event;
    });
  }

  @override
  void dispose() {
    super.dispose();
    _subscription?.cancel();
  }

  void playCard(CardModel cardModel) {
    ServerCommunication.playCard(
      cardModel,
      game.id,
      onError: (message) {
        _playError();
        error = message;
      },
      onSuccess: () {},
    );
  }

  void bid(Bid bid) {
    ServerCommunication.bid(
      bid,
      game.id,
      onSuccess: () {},
      onError: (message) {
        error = message;
      },
    );
  }

  void clearSuccess() {
    success = null;
  }

  void _playError() {
    _audioCache.play("bad.mp3", volume: 0.7);
  }

  void playSoftButton() {
    _audioCache.play("click.mp3", volume: 0.2);
  }

  void playHardButton() {
    _audioCache.play("clic.mp3", volume: 0.3);
  }

  void playPlop() {
    _audioCache.play("buttonPush.mp3", volume: 0.5);
  }
}
