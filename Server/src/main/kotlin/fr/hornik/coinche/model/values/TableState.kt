package fr.hornik.coinche.model.values

import com.fasterxml.jackson.annotation.JsonValue

enum class TableState(@JsonValue val value: String) {
    JOINING("JOINING"), BIDDING("BIDDING"), PLAYING("PLAYING"), ENDED("ENDED"), DISTRIBUTING("DISTRIBUTING")
}