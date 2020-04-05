package fr.hornik.coinche.model

import fr.hornik.coinche.model.values.CardColor
import fr.hornik.coinche.model.values.CoincheValues
import fr.hornik.coinche.model.values.PlayerPosition

sealed class Bid
data class SimpleBid(val color: CardColor, val points: Int, val coinche: CoincheValues, val position: PlayerPosition) :
        Bid()

data class General(val color: CardColor, val coinche: CoincheValues, val position: PlayerPosition) : Bid()
data class Capot(val color: CardColor, val coinche: CoincheValues, val position: PlayerPosition) : Bid()
object Pass : Bid()