package fr.hornik.coinche.exception

class NotInGameException(gameId: String) : Exception("You are not a part of the game $gameId")
