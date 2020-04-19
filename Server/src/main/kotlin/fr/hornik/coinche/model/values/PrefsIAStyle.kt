package fr.hornik.coinche.model.values

import com.fasterxml.jackson.annotation.JsonValue

enum class PrefsIAStyle(@JsonValue val value: String) {
    BALANCED("BALANCED"), DEFENSIVE("DEFENSIVE"), AGGRESSIVE("AGGRESSIVE")
}
