import 'dart:async';

import 'package:coinche/state/login_model.dart';
import 'package:coinche/theme/colors.dart';
import 'package:coinche/ui/screen/all_games/all_games_screen.dart';
import 'package:coinche/ui/screen/login/login_manual_screen.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

class SplashScreen extends StatefulWidget {
  static const String routeName = "/splash";

  @override
  _SplashScreenState createState() => _SplashScreenState();
}

class _SplashScreenState extends State<SplashScreen> {
  @override
  void initState() {
    super.initState();
    Future.wait([
      context.read<LoginModel>().checkLoggedFuture,
      Future.delayed(Duration(seconds: 3), () {}),
    ]).then((_) {
      var loggedIn = context.read<LoginModel>().loggedIn;
      if (loggedIn) {
        Navigator.of(context)?.pushReplacementNamed(AllGamesScreen.routeName);
      } else {
        Navigator.of(context)
            ?.pushReplacementNamed(LoginManualScreen.routeName);
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    return Container(color: kColorBeauBlue);
  }
}
