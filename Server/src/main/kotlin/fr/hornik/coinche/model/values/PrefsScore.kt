package fr.hornik.coinche.model.values

import com.fasterxml.jackson.annotation.JsonValue

enum class PrefsScore(@JsonValue val value: String) {
    POINTSANNOUNCED("POINTSANNOUNCED"), POINTSMARKED("POINTSMARKED")
}
