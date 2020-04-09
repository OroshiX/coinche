package fr.hornik.coinche.model.values

import com.fasterxml.jackson.annotation.JsonValue

enum class TableState(@JsonValue val value: String) {
    JOINING("JOINING"),
    DISTRIBUTING("DISTRIBUTING"),
    BIDDING("BIDDING"),
    PLAYING("PLAYING"),
    BETWEEN_GAMES("BETWEEN_GAMES"),
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