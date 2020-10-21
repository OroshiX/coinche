import 'dart:async';
import 'dart:collection';

import 'package:FlutterCoinche/domain/dto/bid.dart';
import 'package:FlutterCoinche/domain/dto/card.dart';
import 'package:FlutterCoinche/domain/dto/game.dart';
import 'package:FlutterCoinche/domain/dto/game_empty.dart';
import 'package:FlutterCoinche/service/network/my_auth_user.dart';
import 'package:FlutterCoinche/service/network/server_communication.dart';
import 'package:cloud_firestore/cloud_firestore.dart';
import 'package:flutter/foundation.dart';

class GameModel extends ChangeNotifier {
  String _error;
  String get error => _error;
  String _success;
  String get success => _success;
  set success(String success) {
    _success = success;
    notifyListeners();
  }

  set error(String value) {
    _error = value;
    notifyListeners();
  }

  Game _game;
  Game get game => _game;
  List<GameEmpty> _games;
  List<GameEmpty> get allGames => UnmodifiableListView(_games);
  MyAuthUser _myAuthUser;
  StreamSubscription<Game> _subscription;
  GameModel() {
    _game = Game();
  }

  set allGames(List<GameEmpty> value) {
    _games = value;
    notifyListeners();
  }

  void clearError() {
    error = null;
  }

  void setUser(MyAuthUser myAuthUser) {
    this._myAuthUser = myAuthUser;
  }

  Future<void> refreshGames() {
    return ServerCommunication.allGames().then((value) => allGames = value,
        onError: (message) => error = message);
  }

  /// Call setUser before this
  void changeGame(String idGame) {
    if (_subscription != null) {
      _subscription.cancel();
    }
    _subscription = Firestore.instance
        .collection('playersSets')
        .document(idGame)
        .collection('players')
        .document(_myAuthUser.uid)
        .snapshots()
        .map((event) => Game.fromJson(event.data))
        .listen((event) {
      _game = event;
      notifyListeners();
    });
  }

  @override
  void dispose() {
    super.dispose();
    _subscription?.cancel();
  }

  void playCard(CardModel cardModel) {
    ServerCommunication.playCard(cardModel, game.id).then((value) {},
        onError: (message) {
      _playError();
      //                   BlocProvider.of<GamesBloc>(context).playError();
      error = message;
    });
  }

  void _playError() {
    // TODO play a sound
  }

  void bid(Bid bid) {
    ServerCommunication.bid(bid, game.id).then((value) {
      success = "Bid $value placed";
    }, onError: (message) {
      error = message;
    });
  }

  void clearSuccess() {
    success = null;
  }
}
