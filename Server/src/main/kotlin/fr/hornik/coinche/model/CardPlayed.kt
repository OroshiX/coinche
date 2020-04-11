package fr.hornik.coinche.model

import fr.hornik.coinche.model.values.BeloteValue
import fr.hornik.coinche.model.values.PlayerPosition

data class CardPlayed(val card: Card = Card(), val belote: BeloteValue = BeloteValue.NONE, val position: PlayerPosition= PlayerPosition.NORTH)
