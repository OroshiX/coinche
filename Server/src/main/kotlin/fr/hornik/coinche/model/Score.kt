package fr.hornik.coinche.model

data class Score(var northSouth: Int = 0, var eastWest: Int = 0) {
    operator fun plusAssign(scoreGame: Score) {
        northSouth += scoreGame.northSouth
        eastWest += scoreGame.eastWest
    }

    fun toFirebase(): Map<String, Int> {
        return mapOf("northSouth" to northSouth,
                     "eastWest" to eastWest)
    }
}