package fr.hornik.coinche.model.values

import com.fasterxml.jackson.annotation.JsonValue

enum class PlayerPosition(@JsonValue val value: String) {
    NORTH("NORTH"),  EAST("EAST"), SOUTH("SOUTH"),WEST("WEST");

    operator fun plus(i: Int): PlayerPosition {
        val size = values().size
        return values()[(ordinal + size + (i % size)) % size]
    }

    operator fun minus(i: Int): PlayerPosition {
        val size = values().size
        return values()[(ordinal + (size - i % size)) % size]
    }
}