package fr.hornik.coinche.model.values

import com.fasterxml.jackson.annotation.JsonValue

enum class CardColor(@JsonValue val value: String) {
    DIAMOND("DIAMOND"), SPADE("SPADE"), CLUB("CLUB"), HEART("HEART")
}