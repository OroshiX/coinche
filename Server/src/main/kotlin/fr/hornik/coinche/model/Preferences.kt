package fr.hornik.coinche.model

import fr.hornik.coinche.model.values.PrefsScore

data class Preferences(val counting: PrefsScore = PrefsScore.POINTSANNOUNCED,
                       // 5 minutes to join a game
                       val JoiningMaxTime:Long  = 300000,
                       // 2 minutes to bid
                       val BiddingMaxTime:Long = 120000)