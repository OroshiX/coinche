import 'package:coinche/domain/dto/game_empty.dart';
import 'package:coinche/ui/resources/colors.dart';
import 'package:coinche/ui/screen/all_games/in_room_game.dart';
import 'package:coinche/ui/screen/all_games/join_game.dart';
import 'package:flutter/material.dart';

class ListGames extends StatelessWidget {
  final List<GameEmpty> games;

  final Future<void> Function(BuildContext context)
      onRefresh;

  const ListGames(
      {Key key,
      @required this.games,
      @required this.onRefresh})
      : super(key: key);

  @override
  Widget build(BuildContext context) {
    final inRoomGames = games.where((element) => element.inRoom).toList();
    final toJoin = games.where((element) => !element.inRoom).toList();
    return RefreshIndicator(
      onRefresh: () => onRefresh(context),
      child: ListView.builder(
        itemCount: games.length + 2,
        itemBuilder: (context, index) {
          if (index == 0) {
            return ListTile(
              title: Text(
                "In game",
                style: TextStyle(color: colorTextDark, fontSize: 20),
              ),
            );
          }
          if (index == inRoomGames.length + 1) {
            return ListTile(
              title: Text(
                "Join",
                style: TextStyle(
                  color: colorTextDark,
                  fontSize: 20,
                ),
              ),
            );
          }
          if (index <= inRoomGames.length) {
            GameEmpty gameInRoom = inRoomGames[index - 1];
            return InRoomGame(
              game: gameInRoom,
            );
          }
          GameEmpty game = toJoin[index - inRoomGames.length - 2];
          return JoinGame(
            game: game,
          );
        },
      ),
    );
  }
}
