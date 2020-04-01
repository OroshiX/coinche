package fr.hornik.coinche.business

import fr.hornik.coinche.model.Bid
import fr.hornik.coinche.model.Card
import fr.hornik.coinche.model.CardPlayed
import fr.hornik.coinche.model.Score
import fr.hornik.coinche.model.values.PlayerPosition

fun calculateWinnerTrick(cardsPlayed: List<CardPlayed>, bid: Bid): PlayerPosition {
    TODO("calculate")
}

/**
 * Only called if it is my turn to play
 */
fun allValidCardToPlay(myCardsInHand: List<Card>, bid: Bid, cardsOnTable: List<CardPlayed>): List<Card> {
    TODO("check ")
}

/**
 * Check if theCardToCheck is a valid choice to play considering our hand
 */
fun isValidCard(myCardsInHand: List<Card>, bid: Bid, cardsOnTable: List<CardPlayed>, theCardToCheck: Card): Boolean {
    TODO()
}

fun calculateScoreGame(plisNS: List<List<CardPlayed>>, plisEW: List<List<CardPlayed>>, dixDer: PlayerPosition,
                       bid: Bid): Score {
    TODO("La belote est dans les cardPlayed")
}

fun isValidBid(bids: List<Bid>, myBid: Bid): Boolean {
    TODO()
}