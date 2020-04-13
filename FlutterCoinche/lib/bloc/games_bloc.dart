import 'dart:async';

import 'package:FlutterCoinche/bloc/bloc_base.dart';
import 'package:FlutterCoinche/dto/card.dart' as card;
import 'package:FlutterCoinche/dto/game.dart';
import 'package:FlutterCoinche/dto/game_empty.dart';
import 'package:FlutterCoinche/fire/fire_auth_service.dart';
import 'package:cloud_firestore/cloud_firestore.dart';
import 'package:flutter/material.dart';
import 'package:rxdart/rxdart.dart';

class GamesBloc with ChangeNotifier implements BlocBase {
  BehaviorSubject<Game> gameBehavior = BehaviorSubject();
  BehaviorSubject<List<GameEmpty>> allMyGames;
  MyAuthUser _myAuthUser;
  StreamSubscription<Game> subscription;

  GamesBloc() {
    allMyGames = BehaviorSubject();
  }

  void addAllMyGames(List<GameEmpty> games) {
    allMyGames.add(games);
  }

  void setUser(MyAuthUser myAuthUser) {
    this._myAuthUser = myAuthUser;
    notifyListeners();
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
      gameBehavior.add(event);
    });
  }

  @override
  void dispose() {
    super.dispose();
    gameBehavior.close();
    subscription.cancel();
    allMyGames.close();
  }
}
