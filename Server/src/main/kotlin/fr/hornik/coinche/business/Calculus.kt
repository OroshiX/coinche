package fr.hornik.coinche.business

import fr.hornik.coinche.model.*
import fr.hornik.coinche.model.values.*

/**
 * Only called if it is my turn to play
 */
fun allValidCardsToPlay(myCardsInHand: List<Card>, bid: Bid,
                        cardsOnTable: List<CardPlayed>): List<Card> {
    val trump = bid.curColor()

    if (cardsOnTable.isEmpty()) {
        // Si aucune carte jouée tout ok
        return myCardsInHand
    }
    val curColor = cardsOnTable[0].card.color
    val validCards = mutableListOf<Card>()

    if (curColor != trump) {
        for (card in myCardsInHand) {
            if (card.color == curColor) validCards.add(card)
        }
    }

    val dominanceAtout =
            CardValue.values().associate { it to it.dominanceAtout }

    // Si je n'ai pas la couleur jouée ou atout demandé alors atout autorisé
    if (validCards.isEmpty()) {
        // seuil atout
        var threshold = -1
        for (cardP in cardsOnTable) {
            if (cardP.card.color == trump && dominanceAtout.getValue(
                        cardP.card.value) > threshold) {
                // update du seuil
                threshold = dominanceAtout.getValue(cardP.card.value)
            }
        }
        val upperTrump = mutableListOf<Card>()
        val lowerTrump = mutableListOf<Card>()
        for (card in myCardsInHand) {
            if (card.color == trump) {
                // les atouts
                if (dominanceAtout.getValue(card.value) > threshold) {
                    // les atouts au dessus seuil
                    upperTrump.add(card)
                } else {
                    // les atouts sous seuil
                    lowerTrump.add(card)
                }
            }
        }
        if (upperTrump.size == 0) {
            for (trumpCard in lowerTrump) {
                // si pas atouts au-dessus, les dessous sont autorisés
                validCards.add(trumpCard)
            }
        }
        for (trumpCard in upperTrump) {
            validCards.add(
                    trumpCard)            // les atouts sup sont autorisés
        }

        val mapPos = mapOf(
                PlayerPosition.NORTH to 0,
                PlayerPosition.WEST to 1,
                PlayerPosition.SOUTH to 2,
                PlayerPosition.EAST to 3)

        val myPos =
                (mapPos[cardsOnTable[cardsOnTable.size - 1].position]
                        ?: error("!!!!!!!!")) + 1
        val playerPosition = calculateWinnerTrick(cardsOnTable, bid)
        var master = false
        if ((mapPos.getValue(playerPosition) + myPos) % 2 == 0) {
            master = true    // partenaire maître
        }

        if (validCards.size == 0 || (master && trump != curColor)) {                // si pas d'atout ou partenaire maitre defausse ok
            for (card in myCardsInHand) {
                if (card.color != trump) {
                    validCards.add(card)
                }
            }
        }
    }
    return validCards
}

/**
 * Check if theCardToCheck is a valid choice to play considering our hand
 */
fun isValidCard(myCardsInHand: List<Card>, bid: Bid,
                cardsOnTable: List<CardPlayed>, theCardToCheck: Card): Boolean {
    val validCards = allValidCardsToPlay(myCardsInHand, bid,
                                         cardsOnTable)    // A mettre en argument si on veut eviter de tout recalucler
    return validCards.contains(theCardToCheck)
}

fun isValidBid(bids: List<Bid>, myBid: Bid): Boolean {
    var counter = bids.size // nombre d'enchères (en comptant les pass)
    while (counter > 0) {
        val lastBid = bids[counter - 1]
        when {
            myBid is Pass                              -> return true                        // Pass tjs ok
            lastBid is Pass                            -> counter--                        // si last = pass on regarde celle d'avant
            lastBid is Coinche && lastBid.surcoinche   -> return false        // si surcoinche alors rien
            lastBid is Coinche                         -> return myBid is Coinche && myBid.surcoinche    // si coinche alors que surcoinche
            myBid is Coinche                           -> return !myBid.surcoinche                // valeur normale, si je coinche c'est bon
            lastBid is General                         -> return myBid is General && myBid.belote && !lastBid.belote
            lastBid is Capot                           -> return myBid is General || myBid is Capot && myBid.belote && !lastBid.belote
            myBid is General || myBid is Capot         -> return true            // si pas gen ou capot alors je peux
            myBid is SimpleBid && lastBid is SimpleBid -> return myBid.points > lastBid.points                // si mon enchère plus haute que la sienne
            else                                       -> return false

        }
    }
    return myBid !is Coinche                                // si que des pass tout ok sauf (sur)coinche
}

/**
 * Can we continue bidding or not?
 * @return true if we should start playing, else false (continue bidding)
 */
fun isLastBid(bids: List<Bid>): Boolean {
    val bidsLength = bids.size
    var nbLastPass = 0
    when {
        bids.isEmpty() -> return false                // pas d'encheres on continue
        bids[bidsLength - 1] is Coinche && bids[bidsLength -1].surcoinche -> return true      // si derniere enchere est surcoinche on joue
        bidsLength < 3 -> return false                  // Moins de 3 encheres on continue
        else -> {
            for (i in 0 until 3) {
                if (bids[bidsLength -1 - i] is Pass) nbLastPass ++     // On compte les pass parmi les 3 dernieres encheres
            }
            return nbLastPass                                           // Si 3 pass parmi ceux la on joue sinon non
        }
    }
}

/**
 * What is the current bid, considering all the bids?
 * @return the bid that we all agreed on, and that we should play
 */
fun getCurrentBid(allBids: List<Bid>) : Bid {
    var bidsLength = allBids.size
    // Cette boucle possible car soit on sarrete avant datteindre le out of bounds (annonce coinche sucrcoinche -> listsize = 3) soit listesize >= 4
    for (j in 1..4) {
        if (allBids[bidsLength-j] !is Pass) return allBids[bidsLength-j]
    }
    return allBids[0] // on ne passe par ici que si il n'y a eu que des pass dans ce cas on renvoie pass (on passe au tour suivant)
}

/**
 *  Fonction Gagnant
 */
fun calculateWinnerTrick(cardsPlayed: List<CardPlayed>,
                         bid: Bid): PlayerPosition {
    // Pour 1 à 4 cartes sur la table
    val trump = bid.curColor()

    // Couleur de la 1ere carte
    val curColor = cardsPlayed[0].card.color

    var isTrump = false
    var i = 0
    var strongest = 0
    val nbCartes = cardsPlayed.size

    while (i < nbCartes) {
        // Y a t il un atout parmi les cartes jouées
        if (cardsPlayed[i].card.color == trump) {
            isTrump = true
            strongest = i
            i = nbCartes
        }
        i++
    }
    // Mapping dominance atout
    val mapAtout = CardValue.values().associate { it to it.dominanceAtout }

    // Mapping dominance couleur
    val mapColor = CardValue.values().associate { it to it.dominanceCouleur }
    var domColor = curColor
    var mapper = mapColor

    if (isTrump) {
        domColor = trump
        mapper = mapAtout
    }
    for (j in strongest + 1 until nbCartes) {
        // Si couleur dominante et plus forte on update
        if (cardsPlayed[j].card.color == domColor) {
            if (mapper.getValue(cardsPlayed[j].card.value) > mapper.getValue(
                        cardsPlayed[strongest].card.value))
                strongest = j
        }
    }
    return cardsPlayed[strongest].position
}

fun calculateScoreGame(plisNS: List<List<CardPlayed>>,
                       plisEW: List<List<CardPlayed>>, dixDer: PlayerPosition,
                       bid: Bid,
                       scoreRule: PrefsScore = PrefsScore.POINTSANNOUNCED): Score {
    val score = calculateScoreTricks(plisNS, plisEW, dixDer, bid)
    println("DEBUG : score reel NS ${score.northSouth}, EW ${score.eastWest}")
    var threshold = 0
    var posTaker = PlayerPosition.NORTH
    when (bid) {
        is SimpleBid -> {
            threshold = bid.points
            posTaker = bid.position
        }
        is Capot     -> {
            threshold = 250
            posTaker = bid.position
            if (bid.belote) threshold += 20
        }

        is General   -> {
            threshold = 500
            posTaker = bid.position
            if (bid.belote) threshold += 20
        }
        is Coinche   -> {
            posTaker = bid.annonce.position
            when (bid.annonce) {
                is SimpleBid -> threshold = bid.annonce.points
                is General   -> {
                    threshold = 500
                    if (bid.annonce.belote) threshold += 20
                }
                is Capot     -> {
                    threshold = 250
                    if (bid.annonce.belote) threshold += 20
                }
            }
        }
    }
    var pointsNS = 0
    var pointsEW = 0
    var multiplier = 1
    if (bid is Coinche) {
        multiplier = if (bid.surcoinche) 4
        else 2
    }
    // calcul des points : le gagnant recoit la valeur de l'enchere exactement * le multiplicateur coinche/surcoinche
    // TODO utiliser l'argument preferences pour implementer d'autres modes de calculs
    when {
        (scoreRule == PrefsScore.POINTSANNOUNCED) -> {
            // NS a pris
            if (posTaker == PlayerPosition.NORTH || posTaker == PlayerPosition.SOUTH) {
                if (score.northSouth < threshold) {
                    pointsEW = multiplier * threshold
                } else pointsNS = multiplier * threshold
            }
            // EW a pris
            else {
                if (score.eastWest < threshold) {
                    pointsNS = multiplier * threshold
                } else pointsEW = multiplier * threshold
            }
        }
        (scoreRule == PrefsScore.POINTSMARKED)    -> {
            // NS a pris
            if (posTaker == PlayerPosition.NORTH || posTaker == PlayerPosition.SOUTH) {
                if (score.northSouth < threshold) {
                    // NS perd il ne marque pas de points, EW gagne la valeur du contrat + 160
                    pointsEW = multiplier * (threshold + 160)
                } else {
                    // NS gagne il marque ses points arrondis a la dizaine la plus proche (135->140) + la valeur du contrat, EW marque ses points
                    pointsNS =
                            multiplier * (threshold + (score.northSouth + 5) / 10 * 10)
                    pointsEW = score.eastWest
                }
            }
            // EW a pris
            else {
                if (score.eastWest < threshold) {
                    // EW perd il ne marque pas de points, NS gagne la valeur du contrat + 160
                    pointsNS = multiplier * (threshold + 160)
                } else {
                    // EW gagne il marque ses points arrondis a la dizaine la plus proche (135->140) + la valeur du contrat, NS marque ses points
                    pointsEW =
                            multiplier * (threshold + (score.eastWest + 5) / 10 * 10)
                    pointsNS = score.northSouth
                }
            }

        }
    }
    return Score(pointsNS, pointsEW)
}

fun calculateScoreTricks(plisNS: List<List<CardPlayed>>,
                         plisEW: List<List<CardPlayed>>, dixDer: PlayerPosition,
                         bid: Bid): Score {

    val mapAtout = CardValue.values()
            .associate { cardValue -> cardValue to cardValue.atoutPoints }

    val mapColor = CardValue.values()
            .associate { cardValue -> cardValue to cardValue.colorPoints }

    val trump = bid.curColor()

    // iterate for plis NS
    var (sumNS, beloteNS) = iterateCardPlayed(plisNS, trump, mapAtout, mapColor)
    var (sumEW, beloteEW) = iterateCardPlayed(plisEW, trump, mapAtout, mapColor)

    when (dixDer) {
        PlayerPosition.NORTH, PlayerPosition.SOUTH -> sumNS += 10
        else                                       -> sumEW += 10
    }

    val posTaker = bid.positionAnnouncer() // Quelle est la position du preneur

    sumNS = sumIfGeneraleOrCapot(sumNS, plisNS, posTaker, bid)
    sumEW = sumIfGeneraleOrCapot(sumEW, plisEW, posTaker, bid)

    // On rajoute la valeur de la belote au preneur
    when (posTaker) {
        PlayerPosition.NORTH, PlayerPosition.SOUTH -> sumNS += beloteNS
        else                                       -> sumEW += beloteEW
    }
    return Score(sumNS, sumEW)
}

fun iterateCardPlayed(plis: List<List<CardPlayed>>, trump: CardColor,
                      mapAtout: Map<CardValue, Int>,
                      mapColor: Map<CardValue, Int>): Pair<Int, Int> {
    var sum = 0
    var belote = 0
    for (pli in plis) {
        for (cardPlayed in pli) {
            if (cardPlayed.card.color == trump) {
                // Points atout
                sum += mapAtout[cardPlayed.card.value] ?: 0
                // Belote ?
                if (cardPlayed.belote == BeloteValue.REBELOTE) belote = 20
            } else {
                // Points couleur
                sum += mapColor[cardPlayed.card.value] ?: 0
            }
        }
    }
    return sum to belote
}

fun sumIfGeneraleOrCapot(sumBefore: Int,
                         plis: List<List<CardPlayed>>,
                         posTaker: PlayerPosition,
                         bid: Bid): Int {
    var sum = sumBefore
    if (sumBefore == 162) {
        // si 162 avant belote alors capot
        sum = 250
        var isGene = true
        for (trick in plis) {
            if (calculateWinnerTrick(trick, bid) != posTaker) {
                // pas de générale deso
                isGene = false
                break
            }
        }
        if (isGene) sum = 500
    }
    return sum

}

/**
 * Fonction cartes Valides
 */