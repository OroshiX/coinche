package fr.hornik.coinche.model.values

import com.fasterxml.jackson.annotation.JsonValue

enum class PlayerPosition(@JsonValue val value: String) {
    NORTH("NORTH"), SOUTH("SOUTH"), EAST("EAST"), WEST("WEST")
}