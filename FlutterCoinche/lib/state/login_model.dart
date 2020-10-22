import 'package:FlutterCoinche/service/network/my_auth_user.dart';
import 'package:FlutterCoinche/service/network/server_communication.dart';
import 'package:flutter/foundation.dart';

class LoginModel extends ChangeNotifier {
  bool _loggedIn;

  Future<void> _futureLogged;

  MyAuthUser _user;
  MyAuthUser get user => _user;

  Future<void> get checkLoggedFuture => _futureLogged;
  bool get loggedIn => _loggedIn;
  set loggedIn(value) {
    _loggedIn = value;
    notifyListeners();
  }

  LoginModel() {
    _loggedIn = false;
    _futureLogged = _checkLoggedIn();
  }

  Future<void> _checkLoggedIn() {
    return ServerCommunication.isLoggedIn().then((value) {
      if (value.isLoggedIn) {
        _user = MyAuthUser(uid: value.uid, displayName: value.nickName);
      }
      loggedIn = value.isLoggedIn;
    });
  }

  void setUser(MyAuthUser user) {
    _user = user;
    notifyListeners();
  }
}
