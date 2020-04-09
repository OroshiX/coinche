package fr.hornik.coinche.model

data class Score(var northSouth: Int = 0, var eastWest: Int = 0) {
    operator fun plusAssign(scoreGame: Score) {
        northSouth += scoreGame.northSouth
        eastWest += scoreGame.eastWest
    }

}