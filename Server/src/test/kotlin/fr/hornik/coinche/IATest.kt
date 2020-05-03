package fr.hornik.coinche

import fr.hornik.coinche.business.printHand
import fr.hornik.coinche.model.Card
import fr.hornik.coinche.model.IARun
import fr.hornik.coinche.model.Pass
import fr.hornik.coinche.model.SimpleBid
import fr.hornik.coinche.model.values.CardColor
import fr.hornik.coinche.model.values.CardValue
import fr.hornik.coinche.model.values.PlayerPosition
import fr.hornik.coinche.util.dbgLevel
import fr.hornik.coinche.util.debugPrintln
import fr.hornik.coinche.util.traceLevel
import org.junit.jupiter.api.Test

class IATest {



    @Test
    fun testEnchere90 () {

        val nameTest = "Enchere90"

        // North : 100 Spade
        // South : Coinche
        // East plays K Heart
        // South plays 8 Club ( no heart / no spade )
        // West has AS 10S AH 9H 7H JC JD 7D
        // playable should be true for A,9 or 7 H
        val oldTraceLevel = traceLevel
        // traceLevel = dbgLevel.ALL

        var myCards: MutableList<Card> = mutableListOf<Card>()

        myCards.add(Card(CardValue.ACE, CardColor.SPADE, playable = null))
        myCards.add(Card(CardValue.TEN, CardColor.SPADE, playable = null))
        myCards.add(Card(CardValue.ACE, CardColor.HEART, playable = null))
        myCards.add(Card(CardValue.NINE, CardColor.HEART, playable = null))
        myCards.add(Card(CardValue.SEVEN, CardColor.HEART, playable = null))
        myCards.add(Card(CardValue.JACK, CardColor.CLUB, playable = null))
        myCards.add(Card(CardValue.JACK, CardColor.DIAMOND, playable = null))
        myCards.add(Card(CardValue.SEVEN, CardColor.DIAMOND, playable = null))

        printHand(dbgLevel.DEBUG, myCards, "$nameTest:Mycards are ")


        val testBid1 = IARun.enchere(PlayerPosition.NORTH, listOf(), myCards, 0)
        traceLevel = oldTraceLevel

        assert((testBid1.curPoint() >= 80) && (testBid1.curPoint() <= 120)) { "$nameTest FAIL - $testBid1 is not accurate" }
        debugPrintln(dbgLevel.REGULAR, "$nameTest:PASS - Bid was : $testBid1")


    }


    @Test
    fun testEnchere100 () {
        val nameTest = "Enchere100"

        val oldTraceLevel = traceLevel
        //traceLevel = dbgLevel.ALL
        // Bid should be at minimum 120 Club


        var myCards: MutableList<Card> = mutableListOf<Card>()


        myCards.add(Card(CardValue.JACK, CardColor.SPADE, playable = null))
        myCards.add(Card(CardValue.NINE, CardColor.SPADE, playable = null))
        myCards.add(Card(CardValue.ACE, CardColor.SPADE, playable = null))
        myCards.add(Card(CardValue.SEVEN, CardColor.SPADE, playable = null))
        myCards.add(Card(CardValue.ACE, CardColor.HEART, playable = null))
        myCards.add(Card(CardValue.ACE, CardColor.CLUB, playable = null))
        myCards.add(Card(CardValue.ACE, CardColor.DIAMOND, playable = null))
        myCards.add(Card(CardValue.TEN, CardColor.DIAMOND, playable = null))

        printHand(dbgLevel.DEBUG, myCards, "$nameTest:Mycards are ")


        val testBid1 = IARun.enchere(PlayerPosition.NORTH, listOf(), myCards, 0)
        traceLevel = oldTraceLevel

        assert(testBid1.curPoint() == 500) { "$nameTest FAIL - $testBid1 is not accurate" }

        debugPrintln(dbgLevel.REGULAR, "$nameTest:PASS - Bid was : $testBid1")

    }

    @Test
    fun testEnchere110 () {
        val nameTest = "Enchere110"

        // Bid should be at minimum 120 Club
        val oldTraceLevel = traceLevel
        //traceLevel = dbgLevel.ALL

        var myCards: MutableList<Card> = mutableListOf<Card>()

        myCards.add(Card(CardValue.ACE, CardColor.CLUB, playable = null))
        myCards.add(Card(CardValue.KING, CardColor.CLUB, playable = null))
        myCards.add(Card(CardValue.JACK, CardColor.CLUB, playable = null))
        myCards.add(Card(CardValue.EIGHT, CardColor.CLUB, playable = null))
        myCards.add(Card(CardValue.SEVEN, CardColor.CLUB, playable = null))
        myCards.add(Card(CardValue.ACE, CardColor.DIAMOND, playable = null))
        myCards.add(Card(CardValue.TEN, CardColor.DIAMOND, playable = null))
        myCards.add(Card(CardValue.JACK, CardColor.DIAMOND, playable = null))

        printHand(dbgLevel.DEBUG, myCards, "$nameTest:Mycards are ")


        val prevBid = SimpleBid(CardColor.DIAMOND, 120, PlayerPosition.NORTH)
        val listBids = listOf(Pass(), prevBid, Pass())

        val testBid1 = IARun.enchere(PlayerPosition.NORTH, listBids, myCards, 0)

        traceLevel = oldTraceLevel
        assert((testBid1.curPoint() >= 110) && (testBid1.curPoint() <= 140)) { "$nameTest FAIL - $testBid1 is not accurate" }
        debugPrintln(dbgLevel.REGULAR, "$nameTest:PASS - Bid $testBid1")


    }

    @Test
    fun testEnchere120() {
        val nameTest = "Enchere120"

        // Bid should be at minimum 110 Club
        val oldTraceLevel = traceLevel
        //traceLevel = dbgLevel.ALL
        var myCards: MutableList<Card> = mutableListOf<Card>()

        myCards.add(Card(CardValue.JACK, CardColor.HEART, playable = null))
        myCards.add(Card(CardValue.NINE, CardColor.HEART, playable = null))
        myCards.add(Card(CardValue.TEN, CardColor.HEART, playable = null))
        myCards.add(Card(CardValue.ACE, CardColor.CLUB, playable = null))
        myCards.add(Card(CardValue.KING, CardColor.CLUB, playable = null))
        myCards.add(Card(CardValue.ACE, CardColor.SPADE, playable = null))
        myCards.add(Card(CardValue.TEN, CardColor.SPADE, playable = null))
        myCards.add(Card(CardValue.TEN, CardColor.DIAMOND, playable = null))

        printHand(dbgLevel.DEBUG, myCards, "$nameTest:Mycards are ")


        val prevBid = SimpleBid(CardColor.DIAMOND, 80, PlayerPosition.SOUTH)
        val listBids = listOf(Pass(), prevBid, Pass())

        val testBid1 = IARun.enchere(PlayerPosition.NORTH, listBids, myCards, 0)
        traceLevel = oldTraceLevel
        assert(((testBid1.curPoint() >= 110) && (testBid1.curPoint() <= 120))) { "$nameTest FAIL - $testBid1 is not accurate" }
        debugPrintln(dbgLevel.REGULAR, "$nameTest:PASS - Bid $testBid1")

    }

    @Test
    fun testEnchere140() {
        val nameTest = "Enchere140"

        // Bid should be Generale + Belote
        val oldTraceLevel = traceLevel
        //traceLevel = dbgLevel.ALL
        var myCards: MutableList<Card> = mutableListOf<Card>()

        myCards.add(Card(CardValue.JACK, CardColor.HEART, playable = null))
        myCards.add(Card(CardValue.NINE, CardColor.HEART, playable = null))
        myCards.add(Card(CardValue.ACE, CardColor.HEART, playable = null))
        myCards.add(Card(CardValue.QUEEN, CardColor.HEART, playable = null))
        myCards.add(Card(CardValue.KING, CardColor.HEART, playable = null))
        myCards.add(Card(CardValue.ACE, CardColor.SPADE, playable = null))
        myCards.add(Card(CardValue.ACE, CardColor.CLUB, playable = null))
        myCards.add(Card(CardValue.ACE, CardColor.DIAMOND, playable = null))

        printHand(dbgLevel.DEBUG, myCards, "$nameTest:Mycards are ")


        val prevBid = SimpleBid(CardColor.DIAMOND, 160, PlayerPosition.SOUTH)
        val listBids = listOf(Pass(), prevBid, Pass())

        val testBid1 = IARun.enchere(PlayerPosition.NORTH, listBids, myCards, 0)
        traceLevel = oldTraceLevel
        assert(((testBid1.curPoint() >= 520 ))) { "$nameTest FAIL - $testBid1 is not accurate" }
        debugPrintln(dbgLevel.REGULAR, "$nameTest:PASS - Bid $testBid1")

    }

}

