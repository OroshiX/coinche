package fr.hornik.coinche.model.values

import com.fasterxml.jackson.annotation.JsonValue

enum class TableState(@JsonValue val value: String) {
    // Waiting for enough player to start the game
    JOINING("JOINING"),

    // Currently distributing - probably only internal state
    DISTRIBUTING("DISTRIBUTING"),

    // Bidding are occuring , waiting for 3 pass or a coinche/surcoinche bid
    BIDDING("BIDDING"),

    // Playing
    PLAYING("PLAYING"),

    // We have finished the current game - giving a small amount of time for player to stay or leave
    BETWEEN_GAMES("BETWEEN_GAMES"),

    // Game is over - do we want to start again a new full setOfGame ?
    ENDED("ENDED");

    fun incrementState(reGame: Boolean = true): TableState = when (this) {
        JOINING       -> DISTRIBUTING
        DISTRIBUTING  -> BIDDING
        BIDDING       -> PLAYING
        PLAYING       -> BETWEEN_GAMES
        BETWEEN_GAMES -> if (reGame) DISTRIBUTING else ENDED
        ENDED         -> ENDED
    }
}