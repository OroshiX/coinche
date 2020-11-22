// @dart=2.9
// todo remove this when null safety has arrived in build_runner
import 'package:coinche/state/game_model.dart';
import 'package:coinche/state/login_model.dart';
import 'package:coinche/ui/screen/all_games/all_games_screen.dart';
import 'package:coinche/ui/screen/game/game_screen_provided.dart';
import 'package:coinche/ui/screen/login/login_manual_screen.dart';
import 'package:coinche/ui/screen/login/login_screen.dart';
import 'package:coinche/ui/screen/splash/splash_screen.dart';
import 'package:coinche/ui/screen/testing_offline_game.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

void main() {
  runApp(ChangeNotifierProvider(
    create: (context) => GameModel(),
    builder: (context, child) => MyApp(),
    lazy: false,
  ));
}

class MyApp extends StatelessWidget {
  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return ChangeNotifierProvider(
      create: (context) => LoginModel(),
      child: MaterialApp(
        title: 'Flutter Demo',
        theme: ThemeData(
          // This is the theme of your application.
          //
          // Try running your application with "flutter run". You'll see the
          // application has a blue toolbar. Then, without quitting the app, try
          // changing the primarySwatch below to Colors.green and then invoke
          // "hot reload" (press "r" in the console where you ran "flutter run",
          // or simply save your changes to "hot reload" in a Flutter IDE).
          // Notice that the counter didn't reset back to zero; the application
          // is not restarted.
          primarySwatch: Colors.blue,
        ),
        routes: {
          LoginScreen.routeName: (context) => LoginScreen(),
          AllGamesScreen.routeName: (context) => AllGamesScreen(),
          GameScreenProvided.routeName: (context) => GameScreenProvided(),
          TestingOfflineGame.routeName: (context) => TestingOfflineGame(),
          SplashScreen.routeName: (context) => SplashScreen(),
          LoginManualScreen.routeName: (context) => LoginManualScreen(),
        },
        home: SplashScreen(),
      ),
    );
  }
}
