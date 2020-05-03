import 'package:FlutterCoinche/dto/bid.dart';
import 'package:FlutterCoinche/dto/player_position.dart';

PlayerPosition getPlayerPositionTop(PlayerPosition myPosition) {
  switch (myPosition) {
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

Bid getPlayerBid(List<Bid> bids, PlayerPosition position) {
  return bids?.lastWhere(
    (element) => element.position == position,
    orElse: () => null,
  );
}

PlayerPosition getPlayerPositionLeft(PlayerPosition myPosition) {
  switch (myPosition) {
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

PlayerPosition getPlayerPositionRight(PlayerPosition myPosition) {
  switch (myPosition) {
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
