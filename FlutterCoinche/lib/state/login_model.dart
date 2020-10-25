import 'package:coinche/service/network/my_auth_user.dart';
import 'package:coinche/service/network/server_communication.dart';
import 'package:flutter/foundation.dart';

class LoginModel extends ChangeNotifier {
  bool _loggedIn;

  Future<void> _futureLogged;

  MyAuthUser _user;
  MyAuthUser get user => _user;

  Future<void> get checkLoggedFuture => _futureLogged;
  bool get loggedIn => _loggedIn;
  String _error;
  String get error => _error;
  set error(String message) {
    _error = message;
    notifyListeners();
  }

  set loggedIn(value) {
    _loggedIn = value;
    notifyListeners();
  }

  LoginModel() {
    _loggedIn = false;
    _futureLogged = _checkLoggedIn();
  }

  Future<void> _checkLoggedIn() {
    return ServerCommunication.isLoggedIn(
      onSuccess: (value) {
        if (value.isLoggedIn) {
          _user = MyAuthUser(uid: value.uid, displayName: value.nickName);
        }
        loggedIn = value.isLoggedIn;
      },
      onError: (message) {
        error = message;
      },
    );
  }

  void setUser(MyAuthUser user) {
    _user = user;
    notifyListeners();
  }
}
