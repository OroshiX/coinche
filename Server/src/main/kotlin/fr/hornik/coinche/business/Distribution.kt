package fr.hornik.coinche.business

import fr.hornik.coinche.model.Card
import fr.hornik.coinche.model.CardPlayed
import fr.hornik.coinche.model.values.CardColor
import fr.hornik.coinche.model.values.CardValue
import fr.hornik.coinche.model.values.PlayerPosition

/**
 * @param cut: we cut the deck in 2 parts at this position
 * @param spread : first we give X cards, then Y, then Z (X,Y,Z)
 * @param dealerPosition: player who deals the cards
 * return cards of each player in the order: NORTH, EAST, SOUTH, WEST
 */
fun dealCards(tricksNS: List<List<CardPlayed>>, tricksEW: List<List<CardPlayed>>, cut: Int,
              spread: Triple<Int, Int, Int>, dealerPosition: PlayerPosition): List<List<Card>> {
    TODO()
}

/**
 * @param spread : first we give X cards, then Y, then Z (X,Y,Z)
 * @param dealerPosition: player who deals the cards
 * return cards of each player in the order: NORTH, EAST, SOUTH, WEST
 */
fun firstDealOfCards(dealerPosition: PlayerPosition, spread: Triple<Int, Int, Int>): List<List<Card>> {
    TODO("Deal deck in random order")
}

val deck = listOf(
        Card(CardValue.KING, CardColor.SPADE, null) // TODO ETC.
)