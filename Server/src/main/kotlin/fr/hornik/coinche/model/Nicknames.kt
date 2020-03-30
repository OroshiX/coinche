package fr.hornik.coinche.model

import com.fasterxml.jackson.annotation.JsonAlias

data class Nicknames(@JsonAlias("NORTH") val NORTH: String,
                     @JsonAlias("SOUTH") val SOUTH: String,
                     @JsonAlias("EAST") val EAST: String,
                     @JsonAlias("WEST") val WEST: String)