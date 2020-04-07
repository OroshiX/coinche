package fr.hornik.coinche.model.values

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonValue

enum class CardValue(@JsonValue val value: Int,
                     @JsonIgnore val atoutPoints: Int,
                     @JsonIgnore val colorPoints: Int,
                     @JsonIgnore val dominanceAtout: Int,
                     @JsonIgnore val dominanceCouleur: Int) {
    ACE(1, 11, 11, 5, 7),
    KING(13, 4, 4, 3, 5),
    QUEEN(12, 3, 3, 2, 4),
    JACK(11, 20, 2, 7, 3),
    TEN(10, 10, 10, 4, 6),
    NINE(9, 14, 0, 6, 2),
    EIGHT(8, 0, 0, 1, 1),
    SEVEN(7, 0, 0, 0, 0)
}