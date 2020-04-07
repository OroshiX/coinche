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
fun dealCards(trickNS: List<List<CardPlayed>>, tricksEW: List<List<CardPlayed>>,
              cut: Int, spread: Triple<Int, Int, Int>,
              dealerPosition: PlayerPosition): List<List<Card>> {
    val mapPos = mapOf(PlayerPosition.NORTH to 0,
                       PlayerPosition.EAST to 1,
                       PlayerPosition.SOUTH to 2,
                       PlayerPosition.WEST to 3)
    val deck = mutableListOf<Card>()

    for (trick in tricksEW) {            //Les 2 paquets sont comptés donc renversés, EW posé dessus
        for (i in 0 until 4) {
            deck.add(trick[3 - i].card)
        }
    }
    for (trick in trickNS) {
        for (i in 0 until 4) {
            deck.add(trick[3 - i].card)
        }
    }

    val sens = 1        // 1 sens trigo -1 sens horaire
    val ce = mutableListOf<Card>()
    val code = mutableListOf<Card>()
    val nest = mutableListOf<Card>()
    val pasbeau = mutableListOf<Card>()
    val fourHands = mutableListOf(ce, code, nest,
                                  pasbeau)        // Liste des 4 futures mains
    var pos =
            mapPos[dealerPosition]!! - sens                    // Premier à qui l'on donne
    val (a, b, c) = spread
    val lpread = mutableListOf(a, b, c)
    var curCard =
            cut    // La carte n°cut sera la première carte donnée (0 sinon), cela correspond à cut cartes dessous

    for (i in 0 until 3) {                                    // 3 tours de donne
        for (j in 0 until 4) {                                // 4 joueurs
            for (k in 0 until lpread[i]) {                        // on donne lpread[i] cartes à la fois
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
            Card(CardValue.ACE, CardColor.HEART, null),
            Card(CardValue.TEN, CardColor.HEART, null),
            Card(CardValue.KING, CardColor.HEART, null),
            Card(CardValue.QUEEN, CardColor.HEART, null),
            Card(CardValue.JACK, CardColor.HEART, null),
            Card(CardValue.NINE, CardColor.HEART, null),
            Card(CardValue.EIGHT, CardColor.HEART, null),
            Card(CardValue.SEVEN, CardColor.HEART, null),
            Card(CardValue.ACE, CardColor.DIAMOND, null),
            Card(CardValue.TEN, CardColor.DIAMOND, null),
            Card(CardValue.KING, CardColor.DIAMOND, null),
            Card(CardValue.QUEEN, CardColor.DIAMOND, null),
            Card(CardValue.JACK, CardColor.DIAMOND, null),
            Card(CardValue.NINE, CardColor.DIAMOND, null),
            Card(CardValue.EIGHT, CardColor.DIAMOND, null),
            Card(CardValue.SEVEN, CardColor.DIAMOND, null),
            Card(CardValue.ACE, CardColor.CLUB, null),
            Card(CardValue.TEN, CardColor.CLUB, null),
            Card(CardValue.KING, CardColor.CLUB, null),
            Card(CardValue.QUEEN, CardColor.CLUB, null),
            Card(CardValue.JACK, CardColor.CLUB, null),
            Card(CardValue.NINE, CardColor.CLUB, null),
            Card(CardValue.EIGHT, CardColor.CLUB, null),
            Card(CardValue.SEVEN, CardColor.CLUB, null),
            Card(CardValue.ACE, CardColor.SPADE, null),
            Card(CardValue.TEN, CardColor.SPADE, null),
            Card(CardValue.KING, CardColor.SPADE, null),
            Card(CardValue.QUEEN, CardColor.SPADE, null),
            Card(CardValue.JACK, CardColor.SPADE, null),
            Card(CardValue.NINE, CardColor.SPADE, null),
            Card(CardValue.EIGHT, CardColor.SPADE, null),
            Card(CardValue.SEVEN, CardColor.SPADE, null))

    val deck = mutableListOf<Card>()
    val rnd = mutableListOf<Int>()
    for (i in 0 until 32) {
        rnd.add(i)
    }
    for (i in 0 until 32) {
        deck.add(deckInit.removeAt(rnd.random()))
        rnd.remove(31 - i)
    }

    val sens = -1        // -1 sens trigo +1 sens horaire
    val ce = mutableListOf<Card>()
    val code = mutableListOf<Card>()
    val nest = mutableListOf<Card>()
    val pasbeau = mutableListOf<Card>()
    val fourHands = mutableListOf<MutableList<Card>>(ce, code, nest,
                                                     pasbeau)        // Liste des 4 futures mains
    var pos =
            mapPos[dealerPosition]!! + sens                    // Premier à qui l'on donne
    val (a, b, c) = spread
    val lpread = mutableListOf<Int>(a, b, c)
    var curCard = 0

    for (i in 0 until 3) {                                    // 3 tours de donne
        for (j in 0 until 4) {                                // 4 joueurs
            for (k in 0 until lpread[i]) {                        // on donne lpread[i] cartes à la fois
                fourHands[(pos + j * sens + 4) % 4].add(deck[curCard])
                curCard++
            }
        }
    }
    return fourHands
}

fun printHand(aListCard: List<Card>, Hand: String) {
    println("Hand $Hand :")
    for (aCard in aListCard) {
        println(aCard)
    }
}



