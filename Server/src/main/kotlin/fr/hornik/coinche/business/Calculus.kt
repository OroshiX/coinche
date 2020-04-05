package fr.hornik.coinche.business

import fr.hornik.coinche.model.*
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
    when (myBid) {
        is SimpleBid -> {
            print("Color : ${myBid.color}, points: ${myBid.points}, coinche : ${myBid.coinche}, player who took: ${myBid.position}")
        }
        is General   -> {
            print("Color : ${myBid.color}, coinche : ${myBid.coinche}, player who took: ${myBid.position}")
        }
        is Capot     -> {
            print("Color : ${myBid.color}, coinche : ${myBid.coinche}, player who took: ${myBid.position}")
        }
        is Pass      -> {
            print("pass")
        }
    }
    TODO()
}