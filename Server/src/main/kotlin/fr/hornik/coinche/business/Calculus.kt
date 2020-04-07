package fr.hornik.coinche.business

import fr.hornik.coinche.model.*
import fr.hornik.coinche.model.values.BeloteValue
import fr.hornik.coinche.model.values.CardColor
import fr.hornik.coinche.model.values.CardValue
import fr.hornik.coinche.model.values.PlayerPosition


/**
 * Only called if it is my turn to play
 */

fun allValidCardsToPlay(myCardsInHand: List<Card>, bid: Bid, cardsOnTable: List<CardPlayed>): List<Card> {
    val trump = bid.curColor()

    if (cardsOnTable.size == 0) {                // Si aucune carte jouée tout ok
        return myCardsInHand
    }
    val curColor = cardsOnTable[0].card.color
    val list = mutableListOf<Card>()

    if (curColor != trump) {
        for (card in myCardsInHand) {
            if (card.color == curColor) list.add(card)
        }
    }

    val map = mapOf(CardValue.JACK to 7,
            CardValue.NINE to 6,
            CardValue.ACE to 5,
            CardValue.TEN to 4,
            CardValue.KING to 3,
            CardValue.QUEEN to 2,
            CardValue.EIGHT to 1,
            CardValue.SEVEN to 0)

    if (list.size == 0) {                    // Si je n'ai pas la couleur jouée ou atout demandé alors atout autorisé
        var threshold = -1                // seuil atout
        for (cardP in cardsOnTable) {
            if (cardP.card.color == trump && map[cardP.card.value]!! > threshold) {
                threshold = map[cardP.card.value]!!// update du seuil
            }
        }
        val upperTrump = mutableListOf<Card>()
        val lowerTrump = mutableListOf<Card>()
        for (card in myCardsInHand) {
            if (card.color == trump) {                // les atouts
                if (map[card.value]!! > threshold) {
                    upperTrump.add(card)            // les atouts au dessus seuil
                } else {
                    lowerTrump.add(card)            // les atouts sous seuil
                }
            }
        }
        if (upperTrump.size == 0) {
            for (trumpCard in lowerTrump) {
                list.add(trumpCard)            // si pas atouts audessus, les dessous sont autorisés
            }
        }
        for (trumpCard in upperTrump) {
            list.add(trumpCard)            // les atouts sup sont autorisés
        }



        val mapPos = mapOf(
                PlayerPosition.NORTH to 0,
                PlayerPosition.WEST to 1,
                PlayerPosition.SOUTH to 2,
                PlayerPosition.EAST to 3)

        var myPos = mapPos[cardsOnTable[cardsOnTable.size - 1].position]!! + 1
        val playerPosition = calculateWinnerTrick(cardsOnTable, bid)
        var master = false
        if ((mapPos[playerPosition]!! + myPos) % 2 == 0) {
            master = true    // partenaire maître
        }

        if (list.size == 0 || (master && trump != curColor)) {                // si pas d'atout ou partenaire maitre defausse ok
            for (card in myCardsInHand) {
                if (card.color != trump) {
                    list.add(card)
                }
            }
        }
    }
    return list
}

/**
 * Check if theCardToCheck is a valid choice to play considering our hand
 */
fun isValidCard(myCardsInHand: List<Card>, bid: Bid, cardsOnTable: List<CardPlayed>, theCardToCheck: Card): Boolean {
    val validCards = allValidCardsToPlay(myCardsInHand, bid, cardsOnTable)    // A mettre en argument si on veut eviter de tout recalucler
    return validCards.contains(theCardToCheck)
}


fun isValidBid(bids: List<Bid>, myBid: Bid): Boolean {
    var counter = bids.size // nombre d'enchères (en comptant les pass)
    while (counter > 0) {
        var lastBid = bids[counter - 1]
        when {
            (myBid is Pass) -> return true                        // Pass tjs ok
            (lastBid is Pass) -> counter--                        // si last = pass on regarde celle d'avant
            (lastBid is Coinche && lastBid.surcoinche) -> return false        // si surcoinche alors rien
            (lastBid is Coinche) -> return (myBid is Coinche && myBid.surcoinche)    // si coinche alors que surcoinche
            (myBid is Coinche) -> return !myBid.surcoinche                // valeur normale, si je coinche c'est bon
            (lastBid is General) -> return (myBid is General && (myBid.belote && !lastBid.belote))
            (lastBid is Capot) -> return (myBid is General || (myBid is Capot && (myBid.belote && !lastBid.belote)))
            (myBid is General || myBid is Capot) -> return true            // si pas gen ou capot alors je peux
            (myBid is SimpleBid && lastBid is SimpleBid) -> return (myBid.points > lastBid.points)                // si mon enchère plus haute que la sienne
            else -> return false

        }
    }
    return (!(myBid is Coinche))                                // si que des pass tout ok sauf (sur)coinche
}


// *** Fonction Gagnant ***
// ************************
// ************************

fun calculateWinnerTrick(cardsPlayed: List<CardPlayed>, bid: Bid): PlayerPosition {     // Pour 1 à 4 cartes sur la table
    val trump = bid.curColor()

    val curColor = cardsPlayed[0].card.color                    // Couleur de la 1ere carte

    var isTrump = false
    var i = 0
    var strongest = 0
    var nbCartes = cardsPlayed.size

    while (i < nbCartes) {                                // Y a t il un atout parmi les cartes jouées
        if (cardsPlayed[i].card.color == trump) {
            isTrump = true
            strongest = i
            i = nbCartes

        }
        i++
    }
    val mapAtout = mapOf(CardValue.JACK to 7,                    // Mapping dominance atout
            CardValue.NINE to 6,
            CardValue.ACE to 5,
            CardValue.TEN to 4,
            CardValue.KING to 3,
            CardValue.QUEEN to 2,
            CardValue.EIGHT to 1,
            CardValue.SEVEN to 0)
    val mapColor = mapOf(CardValue.ACE to 7,                    // Mapping dominance couleur
            CardValue.TEN to 6,
            CardValue.KING to 5,
            CardValue.QUEEN to 4,
            CardValue.JACK to 3,
            CardValue.NINE to 2,
            CardValue.EIGHT to 1,
            CardValue.SEVEN to 0)
    var domColor = curColor
    var mapper = mapColor

    if (isTrump) {
        domColor = trump
        mapper = mapAtout
    }
    for (j in strongest + 1 until nbCartes) {                    // Si couleur dominante et plus forte on update
        if (cardsPlayed[j].card.color == domColor) {
            if (mapper[cardsPlayed[j].card.value]!! > mapper[cardsPlayed[strongest].card.value]!!)
                strongest = j
        }
    }


    return cardsPlayed[strongest].position
}

fun calculateScoreGame(plisNS: List<List<CardPlayed>>, plisEW: List<List<CardPlayed>>, dixDer: PlayerPosition, bid: Bid): Score {
    val mapAtout = mapOf(
            CardValue.JACK to 20,            // Mapping points atout
            CardValue.NINE to 14,
            CardValue.ACE to 11,
            CardValue.TEN to 10,
            CardValue.KING to 4,
            CardValue.QUEEN to 3,
            CardValue.EIGHT to 0,
            CardValue.SEVEN to 0)
    val mapColor = mapOf(
            CardValue.ACE to 11,            // Mapping points couleur
            CardValue.TEN to 10,
            CardValue.KING to 4,
            CardValue.QUEEN to 3,
            CardValue.JACK to 2,
            CardValue.NINE to 0,
            CardValue.EIGHT to 0,
            CardValue.SEVEN to 0)
    var sumNS = 0
    var sumEW = 0
    var belote = 0
    val trump = bid.curColor()
    for (listCardPlayed in plisNS) {
        for (cardP in listCardPlayed) {
            if (cardP.card.color == trump) {
                sumNS += mapAtout[cardP.card.value]!!    // Points atout
                if (cardP.belote == BeloteValue.REBELOTE) belote = 20 // Belote
            } else {
                sumNS += mapColor[cardP.card.value]!!    // Poins couleur
            }
        }
    }

    for (cardPlayed in plisEW) {
        for (cardP in cardPlayed) {
            if (cardP.card.color == trump) {
                sumEW += mapAtout[cardP.card.value]!!    // Points atout
                if (cardP.belote == BeloteValue.REBELOTE) belote = 20 // Belote
            } else {
                sumEW += mapColor[cardP.card.value]!!    // Points couleur
            }
        }
    }

    if (dixDer == PlayerPosition.NORTH || dixDer == PlayerPosition.SOUTH) {
        sumNS += 10
    } else {
        sumEW += 10
    }

    var posTaker = bid.position()                                // Quel est la position du preneur

    if (sumNS == 162) {
        sumNS = 250                    // si 162 avant belote alors capot
        var isGene = true
        for (trick in plisNS) {
            if (calculateWinnerTrick(trick, bid) != posTaker) isGene = false        // pas de générale deso
        }
        if (isGene) sumNS = 500                // si générale alors 500

    }
    if (sumEW == 162) {
        sumEW = 250                    // si 162 avant belote alors capot
        var isGene = true
        for (trick in plisEW) {
            if (calculateWinnerTrick(trick, bid) != posTaker) isGene = false        // pas de générale deso
        }
        if (isGene) sumEW = 500                // si générale alors 500

    }



    if (posTaker == PlayerPosition.NORTH || posTaker == PlayerPosition.SOUTH) { // On rajoute la valeur de la belote au preneur
        sumNS += belote
    } else {
        sumEW += belote
    }
    return Score(sumNS, sumEW)
}


// *** Fonction cartes Valides ***
// *******************************
// *******************************



