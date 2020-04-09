package fr.hornik.coinche.model

import fr.hornik.coinche.model.values.PlayerPosition

data class Player(val uid: String = "", val position: PlayerPosition = PlayerPosition.NORTH, var nickname: String = "",
                  var cardsInHand: MutableList<Card> = mutableListOf())
