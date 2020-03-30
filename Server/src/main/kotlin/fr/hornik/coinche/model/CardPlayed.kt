package fr.hornik.coinche.model

import fr.hornik.coinche.model.values.BeloteValue
import fr.hornik.coinche.model.values.PlayerPosition

data class CardPlayed(val card: Card, val belote: BeloteValue, val position: PlayerPosition)
