package fr.hornik.coinche.model

import fr.hornik.coinche.model.values.PrefsScore
import fr.hornik.coinche.model.values.PrefsIAStyle

data class Preferences(
                    // Counting Points Policy ( 2 Values possible : POINTSANNOUNCED or POINTSMARKED )
                       val counting: PrefsScore = PrefsScore.POINTSANNOUNCED,
                    // 5 minutes to join a game
                       val JoiningMaxTime: Long = 15000,
                    // 2 minutes to bid
                       val BiddingMaxTime: Long = 120000,
                    // IA Style : BALANCED - AGGRESSIVE - DEFENSIVE
                       val IAStyle: PrefsIAStyle = PrefsIAStyle.BALANCED
)