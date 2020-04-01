import 'package:flutter/material.dart';
import 'package:flutter/cupertino.dart';
class AllGamesScreen extends StatelessWidget {
  static const routeName = "/allGames";

  @override
  Widget build(BuildContext context) {
    return Expanded(
      child: Center(
        child: Container(
          color: Colors.green,
          child: Text("Success"),
        ),
      ),
    );
  }
}
