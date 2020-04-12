import 'package:FlutterCoinche/bloc/bloc_base.dart';
import 'package:FlutterCoinche/dto/game.dart';
import 'package:FlutterCoinche/fire/fire_auth_service.dart';
import 'package:cloud_firestore/cloud_firestore.dart';
import 'package:rxdart/rxdart.dart';

class GamesBloc implements BlocBase {
  BehaviorSubject<Game> gameBehavior = BehaviorSubject();
  MyAuthUser _myAuthUser;

  GamesBloc();

  void setUser(MyAuthUser myAuthUser) {
    this._myAuthUser = myAuthUser;
  }

  void changeGame(String idGame) {
    Firestore.instance
        .collection('playersSets')
        .document(idGame)
        .collection('players')
        .document(_myAuthUser.uid)
        .get()
        .then((value) {
      print(value.data);
      gameBehavior.add(Game.fromJson(value.data));
    });

    Firestore.instance
        .collection('playersSets')
        .document(idGame)
        .collection('players')
        .document(_myAuthUser.uid)
        .snapshots()
        .map((event) => Game.fromJson(event.data))
        .listen((event) {
      gameBehavior.add(event);
    });
  }

  @override
  void dispose() {
    gameBehavior.close();
  }
}
