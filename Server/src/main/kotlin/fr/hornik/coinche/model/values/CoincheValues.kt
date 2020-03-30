package fr.hornik.coinche.model.values

import com.fasterxml.jackson.annotation.JsonValue

enum class CoincheValues(@JsonValue val value: String?) {
    COINCHE("COINCHE"), SURCOINCHE("SURCOINCHE"), NONE(null)
}