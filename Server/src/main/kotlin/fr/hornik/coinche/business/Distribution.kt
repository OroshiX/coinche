package fr.hornik.coinche.business

import fr.hornik.coinche.model.Card
import fr.hornik.coinche.model.CardPlayed
import fr.hornik.coinche.model.values.CardColor
import fr.hornik.coinche.model.values.CardValue
import fr.hornik.coinche.model.values.PlayerPosition
import fr.hornik.coinche.util.dbgLevel
import fr.hornik.coinche.util.debugPrintln

val allSpreads = listOf(Triple(3, 2, 3), Triple(2, 3, 3), Triple(3, 3, 2))

/**
 * @param cut: we cut the deck in 2 parts at this position
 * @param spread : first we give X cards, then Y, then Z (X,Y,Z)
 * @param dealerPosition: player who deals the cards
 * return cards of each player in the order: NORTH, EAST, SOUTH, WEST
 */
fun dealCards(trickNS: List<List<CardPlayed>>, tricksEW: List<List<CardPlayed>>,
              cut: Int, spread: Triple<Int, Int, Int>,
              dealerPosition: PlayerPosition): List<List<Card>> {
    val mapPos = mapOf(PlayerPosition.NORTH to 0,
                       PlayerPosition.EAST to 1,
                       PlayerPosition.SOUTH to 2,
                       PlayerPosition.WEST to 3)
    val deck = mutableListOf<Card>()
    val nbPlis = (trickNS.size + tricksEW.size)
    if (nbPlis != 8) println("**********ERROR we have $nbPlis ************, should be 8 ")
    //Les 2 paquets sont comptés donc renversés, EW posé dessus
    for (trick in tricksEW) {
        for (i in 0 until 4) {
            deck.add(trick[3 - i].card)
        }
    }
    for (trick in trickNS) {
        for (i in 0 until 4) {
            deck.add(trick[3 - i].card)
        }
    }
    // 1 sens trigo -1 sens horaire
    val sens = 1

    // Liste des 4 futures mains
    val fourHands = List(4) { mutableListOf<Card>() }

    // Premier à qui l'on donne
    val pos = mapPos.getValue(dealerPosition) - sens
    val (a, b, c) = spread
    val lpread = mutableListOf(a, b, c)
    // La carte n°cut sera la première carte donnée (0 sinon), cela correspond à cut cartes dessous
    var curCard = cut

    for (i in 0 until 3) {
        // 3 tours de donne
        for (j in 0 until 4) {
            // 4 joueurs
            for (k in 0 until lpread[i]) {
                // on donne lpread[i] cartes à la fois
                fourHands[(pos + j * sens + 8) % 4].add(deck[curCard % 32])
                curCard++
            }
        }
    }

    return fourHands
}

/**
 * @param spread : first we give X cards, then Y, then Z (X,Y,Z)
 * @param dealerPosition: player who deals the cards
 * return cards of each player in the order: NORTH, EAST, SOUTH, WEST
 */

fun firstDealOfCards(dealerPosition: PlayerPosition,
                     spread: Triple<Int, Int, Int>): List<List<Card>> {
    val mapPos = mapOf(PlayerPosition.NORTH to 0,
                       PlayerPosition.EAST to 1,
                       PlayerPosition.SOUTH to 2,
                       PlayerPosition.WEST to 3)

    val deckInit = mutableListOf(
            Card(CardValue.ACE, CardColor.HEART),
            Card(CardValue.TEN, CardColor.HEART),
            Card(CardValue.KING, CardColor.HEART),
            Card(CardValue.QUEEN, CardColor.HEART),
            Card(CardValue.JACK, CardColor.HEART),
            Card(CardValue.NINE, CardColor.HEART),
            Card(CardValue.EIGHT, CardColor.HEART),
            Card(CardValue.SEVEN, CardColor.HEART),
            Card(CardValue.ACE, CardColor.DIAMOND),
            Card(CardValue.TEN, CardColor.DIAMOND),
            Card(CardValue.KING, CardColor.DIAMOND),
            Card(CardValue.QUEEN, CardColor.DIAMOND),
            Card(CardValue.JACK, CardColor.DIAMOND),
            Card(CardValue.NINE, CardColor.DIAMOND),
            Card(CardValue.EIGHT, CardColor.DIAMOND),
            Card(CardValue.SEVEN, CardColor.DIAMOND),
            Card(CardValue.ACE, CardColor.CLUB),
            Card(CardValue.TEN, CardColor.CLUB),
            Card(CardValue.KING, CardColor.CLUB),
            Card(CardValue.QUEEN, CardColor.CLUB),
            Card(CardValue.JACK, CardColor.CLUB),
            Card(CardValue.NINE, CardColor.CLUB),
            Card(CardValue.EIGHT, CardColor.CLUB),
            Card(CardValue.SEVEN, CardColor.CLUB),
            Card(CardValue.ACE, CardColor.SPADE),
            Card(CardValue.TEN, CardColor.SPADE),
            Card(CardValue.KING, CardColor.SPADE),
            Card(CardValue.QUEEN, CardColor.SPADE),
            Card(CardValue.JACK, CardColor.SPADE),
            Card(CardValue.NINE, CardColor.SPADE),
            Card(CardValue.EIGHT, CardColor.SPADE),
            Card(CardValue.SEVEN, CardColor.SPADE))

    val deck = mutableListOf<Card>()
    val rnd = mutableListOf<Int>()
    for (i in 0 until 32) {
        rnd.add(i)
    }
    for (i in 0 until 32) {
        deck.add(deckInit.removeAt(rnd.random()))
        rnd.remove(31 - i)
    }

    // -1 sens trigo +1 sens horaire
    val sens = -1
    // Liste des 4 futures mains
    val fourHands = List(4) { mutableListOf<Card>() }

    // Premier à qui l'on donne
    val pos = mapPos.getValue(dealerPosition) + sens
    val (a, b, c) = spread
    val lpread = mutableListOf(a, b, c)
    var curCard = 0

    for (i in 0 until 3) {
        // 3 tours de donne
        for (j in 0 until 4) {
            // 4 joueurs
            for (k in 0 until lpread[i]) {
                // on donne lpread[i] cartes à la fois
                fourHands[(pos + j * sens + 4) % 4].add(deck[curCard])
                curCard++
            }
        }
    }
    return fourHands
}

fun printHand(debugLevel:dbgLevel  = dbgLevel.ALL, aListCard: List<Card>, Hand: String) {
    debugPrintln(debugLevel,"Hand $Hand :")
    for (aCard in aListCard) {
        debugPrintln(debugLevel,aCard)
    }
}



