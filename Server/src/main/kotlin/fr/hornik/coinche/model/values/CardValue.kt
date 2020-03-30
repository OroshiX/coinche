package fr.hornik.coinche.model.values

import com.fasterxml.jackson.annotation.JsonValue

enum class CardValue(@JsonValue val value: Int) {
    ACE(1), KING(13), QUEEN(12), JACK(11),
    TEN(10), NINE(9), EIGHT(8), SEVEN(7)
}