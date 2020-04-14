import 'dart:async';

import 'package:FlutterCoinche/dto/card.dart' as card;
import 'package:FlutterCoinche/dto/game.dart';
import 'package:FlutterCoinche/dto/game_empty.dart';
import 'package:FlutterCoinche/fire/fire_auth_service.dart';
import 'package:audioplayers/audio_cache.dart';
import 'package:audioplayers/audioplayers.dart';
import 'package:bloc_provider/bloc_provider.dart';
import 'package:cloud_firestore/cloud_firestore.dart';
import 'package:rxdart/rxdart.dart';

class GamesBloc implements Bloc {
  final BehaviorSubject<Game> _gameController =
      BehaviorSubject<Game>.seeded(Game());

  AudioCache audioCache;

  ValueStream<Game> get game => _gameController;

  ValueStream<List<GameEmpty>> get allGames => _allMyGames;

  BehaviorSubject<List<GameEmpty>> _allMyGames;
  MyAuthUser _myAuthUser;
  StreamSubscription<Game> subscription;

  GamesBloc() {
    _allMyGames = BehaviorSubject();
    audioCache = AudioCache(
        prefix: "sounds/",
        fixedPlayer: AudioPlayer(mode: PlayerMode.LOW_LATENCY));
  }

  void playError() {
    audioCache.play("bad.mp3", volume: 0.7);
  }

  void playSoftButton() {
    audioCache.play("click.mp3", volume: 0.2);
  }

  void playHardButton() {
    audioCache.play("clic.mp3", volume: 0.3);
  }

  void playPlop() {
    audioCache.play("buttonPush.mp3", volume: 0.5);
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
    /*
    Firestore.instance
        .collection('playersSets')
        .document(idGame)
        .collection('players')
        .document(_myAuthUser.uid)
        .get()
        .then((value) {
      print(value.data);
      gameBehavior.add(Game.fromJson(value.data));
      notifyListeners();
    });
*/
    subscription = Firestore.instance
        .collection('playersSets')
        .document(idGame)
        .collection('players')
        .document(_myAuthUser.uid)
        .snapshots()
        .map((event) => Game.fromJson(event.data))
        .listen((event) {
      event.cards.sort((card.Card c1, card.Card c2) {
        if (c1.color.index < c2.color.index) return -1;
        if (c1.color.index > c2.color.index) return 1;
        return card.compareValue(c2.value, c1.value, false);
      });
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
