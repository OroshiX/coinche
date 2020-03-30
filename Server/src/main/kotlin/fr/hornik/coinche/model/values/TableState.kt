package fr.hornik.coinche.model.values

enum class TableState(val value: String) {
    JOINING("JOINING"), BIDDING("BIDDING"), PLAYING("PLAYING"), ENDED("ENDED")
}