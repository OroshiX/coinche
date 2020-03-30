package fr.hornik.coinche.model.values

import com.fasterxml.jackson.annotation.JsonValue

enum class BeloteValue(@JsonValue val value: String?) {
    BELOTE("BELOTE"), REBELOTE("REBELOTE"), NONE(null)
}