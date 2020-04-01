package fr.hornik.coinche.model

import fr.hornik.coinche.model.values.PlayerPosition

data class Player(val uid: String, val position: PlayerPosition,
                  val cardsInHand: List<Card>, val nickname: String)
