package fr.hornik.coinche.model

import fr.hornik.coinche.model.values.CardColor
import fr.hornik.coinche.model.values.CoincheValues

data class Bid(val color: CardColor, val points: Int, var coinche: CoincheValues)
//TODO see Sealed class for GENERAL / PASS / CAPOT / CAPOT BELOTE / GENERAL BELOTE