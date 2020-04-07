package fr.hornik.coinche.model

import com.fasterxml.jackson.annotation.JsonAlias
import fr.hornik.coinche.model.values.PlayerPosition

data class Nicknames(@JsonAlias("NORTH") val NORTH: String,
                     @JsonAlias("SOUTH") val SOUTH: String,
                     @JsonAlias("EAST") val EAST: String,
                     @JsonAlias("WEST") val WEST: String) {
    constructor(players: List<Player>) : this(
            NORTH = players.firstOrNull { it.position == PlayerPosition.NORTH }?.nickname ?: "",
            SOUTH = players.firstOrNull { it.position == PlayerPosition.SOUTH }?.nickname ?: "",
            EAST = players.firstOrNull { it.position == PlayerPosition.EAST }?.nickname ?: "",
            WEST = players.firstOrNull { it.position == PlayerPosition.WEST }?.nickname ?: ""
    )
}