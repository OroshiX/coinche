package fr.hornik.coinche.model

import fr.hornik.coinche.model.values.CoincheValues

data class Bid(val color: String, val points: Int, var coinche: CoincheValues)
