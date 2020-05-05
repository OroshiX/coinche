import 'package:FlutterCoinche/dto/bid.dart';
import 'package:FlutterCoinche/dto/player_position.dart';
import 'package:flutter/material.dart';

PlayerPosition _getPlayerPositionTop(PlayerPosition myPosition) {
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

PlayerPosition _getPlayerPositionLeft(PlayerPosition myPosition) {
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

PlayerPosition _getPlayerPositionRight(PlayerPosition myPosition) {
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

Map<AxisDirection, PlayerPosition> getPosTableToCardinal(
    PlayerPosition myPosition) {
  return {
    AxisDirection.left: _getPlayerPositionLeft(myPosition),
    AxisDirection.right: _getPlayerPositionRight(myPosition),
    AxisDirection.up: _getPlayerPositionTop(myPosition),
    AxisDirection.down: myPosition
  };
}

Map<PlayerPosition, AxisDirection> getCardinalToPosTable(PlayerPosition myPosition) {
  return {
    _getPlayerPositionLeft(myPosition): AxisDirection.left,
    _getPlayerPositionRight(myPosition): AxisDirection.right,
    _getPlayerPositionTop(myPosition): AxisDirection.up,
    myPosition: AxisDirection.down
  };
}

List<PlayerPosition> oppositeTeam(PlayerPosition myPosition) {
  switch (myPosition) {
    case PlayerPosition.NORTH:
    case PlayerPosition.SOUTH:
      return [PlayerPosition.EAST, PlayerPosition.WEST];
    case PlayerPosition.EAST:
    case PlayerPosition.WEST:
      return [PlayerPosition.NORTH, PlayerPosition.SOUTH];
  }
  return [];
}
List<PlayerPosition> myTeam(PlayerPosition myPosition) {
  switch (myPosition) {
    case PlayerPosition.NORTH:
    case PlayerPosition.SOUTH:
      return [PlayerPosition.NORTH, PlayerPosition.SOUTH];
    case PlayerPosition.EAST:
    case PlayerPosition.WEST:
      return [PlayerPosition.EAST, PlayerPosition.WEST];
  }
  return [];
}