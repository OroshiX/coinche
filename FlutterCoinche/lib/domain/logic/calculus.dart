import 'package:coinche/domain/dto/bid.dart';
import 'package:coinche/domain/dto/player_position.dart';
import 'package:flutter/material.dart';

PlayerPosition _getPlayerPositionTop(PlayerPosition myPosition) {
  switch (myPosition) {
    case PlayerPosition.south:
      return PlayerPosition.north;
    case PlayerPosition.north:
      return PlayerPosition.south;
    case PlayerPosition.east:
      return PlayerPosition.west;
    case PlayerPosition.west:
      return PlayerPosition.east;
  }
}

Bid? getPlayerBid(List<Bid> bids, PlayerPosition position) {
  var i = bids.lastIndexWhere((element) => element.position == position);
  return i == -1 ? null : bids[i];
}

PlayerPosition _getPlayerPositionLeft(PlayerPosition myPosition) {
  switch (myPosition) {
    case PlayerPosition.south:
      return PlayerPosition.west;
    case PlayerPosition.north:
      return PlayerPosition.east;
    case PlayerPosition.east:
      return PlayerPosition.south;
    case PlayerPosition.west:
      return PlayerPosition.north;
  }
}

PlayerPosition _getPlayerPositionRight(PlayerPosition myPosition) {
  switch (myPosition) {
    case PlayerPosition.south:
      return PlayerPosition.east;
    case PlayerPosition.north:
      return PlayerPosition.west;
    case PlayerPosition.east:
      return PlayerPosition.north;
    case PlayerPosition.west:
      return PlayerPosition.south;
  }
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

Map<PlayerPosition, AxisDirection> getCardinalToPosTable(
    PlayerPosition myPosition) {
  return {
    _getPlayerPositionLeft(myPosition): AxisDirection.left,
    _getPlayerPositionRight(myPosition): AxisDirection.right,
    _getPlayerPositionTop(myPosition): AxisDirection.up,
    myPosition: AxisDirection.down
  };
}

List<PlayerPosition> oppositeTeam(PlayerPosition myPosition) {
  switch (myPosition) {
    case PlayerPosition.north:
    case PlayerPosition.south:
      return [PlayerPosition.east, PlayerPosition.west];
    case PlayerPosition.east:
    case PlayerPosition.west:
      return [PlayerPosition.north, PlayerPosition.south];
  }
}

List<PlayerPosition> myTeam(PlayerPosition myPosition) {
  switch (myPosition) {
    case PlayerPosition.north:
    case PlayerPosition.south:
      return [PlayerPosition.north, PlayerPosition.south];
    case PlayerPosition.east:
    case PlayerPosition.west:
      return [PlayerPosition.east, PlayerPosition.west];
  }
}
