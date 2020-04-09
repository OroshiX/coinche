package fr.hornik.coinche.exception

class GameFullException(gameId: String) :
        Exception("The game $gameId is full, you cannot join it.") {

}
