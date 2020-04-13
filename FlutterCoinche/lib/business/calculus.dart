import 'package:FlutterCoinche/dto/bid.dart';
import 'package:FlutterCoinche/dto/game.dart';
import 'package:FlutterCoinche/dto/player_position.dart';

PlayerPosition getPlayerPositionTop(Game game) {
  switch (game.myPosition) {
    case PlayerPosition.SOUTH:
      return PlayerPosition.NORTH;
    case PlayerPosition.NORTH:
      return PlayerPosition.SOUTH;
    case PlayerPosition.EAST:
      return PlayerPosition.WEST;
    case PlayerPosition.WEST:
      return PlayerPosition.EAST;
  }
  return null;
}

Bid getPlayerBid(Game game, PlayerPosition position) {
  return game.bids.lastWhere(
    (element) => element.position == position,
    orElse: () => null,
  );
}

PlayerPosition getPlayerPositionLeft(Game game) {
  switch (game.myPosition) {
    case PlayerPosition.SOUTH:
      return PlayerPosition.WEST;
    case PlayerPosition.NORTH:
      return PlayerPosition.EAST;
    case PlayerPosition.EAST:
      return PlayerPosition.SOUTH;
    case PlayerPosition.WEST:
      return PlayerPosition.NORTH;
  }
  return null;
}

PlayerPosition getPlayerPositionRight(Game game) {
  switch (game.myPosition) {
    case PlayerPosition.SOUTH:
      return PlayerPosition.EAST;
    case PlayerPosition.NORTH:
      return PlayerPosition.WEST;
    case PlayerPosition.EAST:
      return PlayerPosition.NORTH;
    case PlayerPosition.WEST:
      return PlayerPosition.SOUTH;
  }
  return null;
}
