import 'dart:async';

import 'package:coinche/domain/dto/game.dart';
import 'package:coinche/domain/dto/game_empty.dart';
import 'package:coinche/service/network/my_auth_user.dart';
import 'package:audioplayers/audio_cache.dart';
import 'package:audioplayers/audioplayers.dart';
import 'package:bloc_provider/bloc_provider.dart';
import 'package:cloud_firestore/cloud_firestore.dart';
import 'package:rxdart/rxdart.dart';

class GamesBloc implements Bloc {
  final BehaviorSubject<Game> _gameController =
      BehaviorSubject<Game>.seeded(Game());

  AudioCache _audioCache;

  ValueStream<Game> get game => _gameController;

  ValueStream<List<GameEmpty>> get allGames => _allMyGames;

  BehaviorSubject<List<GameEmpty>> _allMyGames;
  MyAuthUser _myAuthUser;
  StreamSubscription<Game> subscription;

  GamesBloc() {
    _allMyGames = BehaviorSubject();
    _audioCache = AudioCache(
        prefix: "sounds/",
        fixedPlayer: AudioPlayer(mode: PlayerMode.LOW_LATENCY));
    _audioCache.loadAll(["bad.mp3", "click.mp3", "clic.mp3", "buttonPush.mp3"]);
  }

  void playError() {
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

  void addAllMyGames(List<GameEmpty> games) {
    _allMyGames.add(games);
  }

  void setUser(MyAuthUser myAuthUser) {
    this._myAuthUser = myAuthUser;
  }

  void changeGame(String idGame) {
    if (subscription != null) {
      subscription.cancel();
    }

    subscription = Firestore.instance
        .collection('playersSets')
        .document(idGame)
        .collection('players')
        .document(_myAuthUser.uid)
        .snapshots()
        .map((event) => Game.fromJson(event.data))
        .listen((event) {
      _gameController.sink.add(event);
    });
  }

  @override
  void dispose() {
    _gameController.close();
    subscription.cancel();
    _allMyGames.close();
  }
}
