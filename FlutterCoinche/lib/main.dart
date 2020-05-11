import 'package:FlutterCoinche/state/games_bloc.dart';
import 'package:FlutterCoinche/ui/screen/all_games/all_games_screen.dart';
import 'package:FlutterCoinche/ui/screen/game/stated_game_screen.dart';
import 'package:FlutterCoinche/ui/screen/login_screen.dart';
import 'package:FlutterCoinche/ui/screen/testing_offline_game.dart';
import 'package:bloc_provider/bloc_provider.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';

void main() {
  runApp(BlocProvider(
      creator: (BuildContext context, BlocCreatorBag bag) => GamesBloc(),
      child: MyApp()));
}

class MyApp extends StatelessWidget {
  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
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
        StatedGameScreen.routeName: (context) => StatedGameScreen(),
        TestingOfflineGame.routeName: (context) => TestingOfflineGame(),
      },
      home: LoginScreen(),
    );
  }
}
