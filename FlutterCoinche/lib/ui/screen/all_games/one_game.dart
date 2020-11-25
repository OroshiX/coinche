import 'package:coinche/domain/dto/game_empty.dart';
import 'package:coinche/domain/dto/table_state.dart';
import 'package:coinche/theme/colors.dart';
import 'package:coinche/ui/inner_shadow.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';

class OneGame extends StatelessWidget {
  final GameEmpty game;

  const OneGame({Key? key, required this.game}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    Color color = Colors.red;
    switch (game.state) {
      case TableState.joining:
        color = Colors.blue;
        break;
      case TableState.distributing:
      case TableState.bidding:
      case TableState.playing:
        color = Colors.orange;
        break;
      case TableState.betweenGames:
        color = Colors.amber;
        break;
      case TableState.ended:
        color = Colors.blueGrey;
        break;
    }

    return Column(
      mainAxisAlignment: MainAxisAlignment.start,
      crossAxisAlignment: CrossAxisAlignment.start,
      mainAxisSize: MainAxisSize.min,
      children: [
        Padding(
          padding: const EdgeInsets.only(left: 23.0, top: 4),
          child: Text(
            game.state.toString().split(".").last,
            style: TextStyle(color: kColorTextDark, fontStyle: FontStyle.italic),
          ),
        ),
        ListTile(
          title: Text(
            "Game: ${game.name.toString().replaceAll(GameEmpty.automatedString, "")}",
            style: TextStyle(
                color: kColorTextDark,
                fontSize: 18,
                fontWeight: FontWeight.bold),
          ),
          leading: InnerShadow(
            color: kColorShadow,
            blur: 2,
            offset: Offset(2, 2),
            child: InnerShadow(
              blur: 2,
              color: Colors.white,
              offset: Offset(-2, -2),
              child: Container(
                  padding: EdgeInsets.all(8),
                  decoration: BoxDecoration(
                    shape: BoxShape.circle,
                    color: kColorGradient2,
                  ),
                  child: Text(
                    game.hasBots ? "🤖" : "🙂",
                    style: TextStyle(color: kColorTextDark, fontSize: 26),
                  )),
            ),
          ),
          subtitle: Text("Created by ${game.nicknameCreator}"),
          trailing: Row(
            mainAxisSize: MainAxisSize.min,
            crossAxisAlignment: CrossAxisAlignment.center,
            mainAxisAlignment: MainAxisAlignment.end,
            children: [
              Text(
                game.nbJoined.toString(),
                style: TextStyle(color: kColorText, fontSize: 18),
              ),
              SizedBox(
                width: 2,
              ),
              Icon(game.nbJoined > 1 ? Icons.people : Icons.person),
              SizedBox(
                width: 20,
              ),
              Container(
                width: 10,
                height: 10,
                decoration: BoxDecoration(
                  color: color,
                  borderRadius: BorderRadius.circular(5),
                ),
              ),
              SizedBox(
                width: 10,
              ),
            ],
          ),
        ),
      ],
    );
  }
}
