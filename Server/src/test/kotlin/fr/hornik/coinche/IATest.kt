package fr.hornik.coinche

import fr.hornik.coinche.business.printHand
import fr.hornik.coinche.model.Card
import fr.hornik.coinche.model.IARun
import fr.hornik.coinche.model.Pass
import fr.hornik.coinche.model.SimpleBid
import fr.hornik.coinche.model.values.CardColor
import fr.hornik.coinche.model.values.CardValue
import fr.hornik.coinche.model.values.PlayerPosition
import org.junit.jupiter.api.Test

class IATest {

    enum class dbgLevel(val value: Int) {
        NONE(0), DEBUG(1), FUNCTION(2), SCORE(4), HTML(8), HTMLFUNC(10), MISC(16);

        infix fun and(traceLevel: dbgLevel): Any {
            return traceLevel.value and this.value

        }
        infix fun or(traceLevel: dbgLevel): Any {
            return traceLevel.value or this.value

        }
        fun toInt():Int {
            return this.value
        }
    }

    var TraceLevel: dbgLevel = dbgLevel.NONE

    fun DBGprintln(wantedLevel: dbgLevel, Str: Any) {

        if ((wantedLevel and TraceLevel) != 0) {
            println("$wantedLevel : $Str")
        }


    }

    @Test
    fun testEnchere90 () {

        val nameTest = "Enchere90"

        // North : 100 Spade
        // South : Coinche
        // East plays K Heart
        // South plays 8 Club ( no heart / no spade )
        // West has AS 10S AH 9H 7H JC JD 7D
        // playable should be true for A,9 or 7 H


        var myCards: MutableList<Card> = mutableListOf<Card>()

        myCards.add(Card(CardValue.ACE, CardColor.SPADE,playable = null))
        myCards.add(Card(CardValue.TEN, CardColor.SPADE,playable = null))
        myCards.add(Card(CardValue.ACE, CardColor.HEART,playable = null))
        myCards.add(Card(CardValue.NINE, CardColor.HEART,playable = null))
        myCards.add(Card(CardValue.SEVEN, CardColor.HEART,playable = null))
        myCards.add(Card(CardValue.JACK, CardColor.CLUB,playable = null))
        myCards.add(Card(CardValue.JACK, CardColor.DIAMOND,playable = null))
        myCards.add(Card(CardValue.SEVEN, CardColor.DIAMOND,playable = null))

        if ((dbgLevel.DEBUG and TraceLevel) != 0)
            printHand(myCards,"Mycards are ")



        val testBid1 = IARun.enchere(PlayerPosition.NORTH,listOf(), myCards,0)


        when (testBid1) {
            is Pass -> println("$nameTest:FAIL - you should bid with such a game ***************\n")
            is SimpleBid -> println("$nameTest:PASS - Bid was : $testBid1")
            else -> println("$nameTest:UNRESOLVED with  $testBid1")
        }



    }


    @Test
    fun testEnchere100 () {
        val nameTest = "Enchere100"


       // Bid should be at minimum 120 Club


        var myCards: MutableList<Card> = mutableListOf<Card>()

        myCards.add(Card(CardValue.ACE, CardColor.CLUB,playable = null))
        myCards.add(Card(CardValue.KING, CardColor.CLUB,playable = null))
        myCards.add(Card(CardValue.JACK, CardColor.CLUB,playable = null))
        myCards.add(Card(CardValue.EIGHT, CardColor.CLUB,playable = null))
        myCards.add(Card(CardValue.SEVEN, CardColor.CLUB,playable = null))
        myCards.add(Card(CardValue.ACE, CardColor.DIAMOND,playable = null))
        myCards.add(Card(CardValue.TEN, CardColor.DIAMOND,playable = null))
        myCards.add(Card(CardValue.JACK, CardColor.DIAMOND,playable = null))

        if ((dbgLevel.DEBUG and TraceLevel) != 0)
            printHand(myCards,"Mycards are ")



        val testBid1 = IARun.enchere(PlayerPosition.NORTH,listOf(), myCards,0)


        when (testBid1) {
            is Pass -> println("$nameTest:FAIL - you should bid with such a game ***************\n")
            is SimpleBid -> println("$nameTest:PASS - Bid was : $testBid1")
            else -> println("$nameTest:UNRESOLVED with  $testBid1")
        }



    }

    @Test
    fun testEnchere110 () {
        val nameTest = "Enchere110"

        // Bid should be at minimum 120 Club


        var myCards: MutableList<Card> = mutableListOf<Card>()

        myCards.add(Card(CardValue.ACE, CardColor.CLUB,playable = null))
        myCards.add(Card(CardValue.KING, CardColor.CLUB,playable = null))
        myCards.add(Card(CardValue.JACK, CardColor.CLUB,playable = null))
        myCards.add(Card(CardValue.EIGHT, CardColor.CLUB,playable = null))
        myCards.add(Card(CardValue.SEVEN, CardColor.CLUB,playable = null))
        myCards.add(Card(CardValue.ACE, CardColor.DIAMOND,playable = null))
        myCards.add(Card(CardValue.TEN, CardColor.DIAMOND,playable = null))
        myCards.add(Card(CardValue.JACK, CardColor.DIAMOND,playable = null))

        if ((dbgLevel.DEBUG and TraceLevel) != 0)
            printHand(myCards,"Mycards are ")


        val prevBid = SimpleBid(CardColor.DIAMOND,130,PlayerPosition.NORTH)
        val listBids = listOf(Pass(),prevBid,Pass())

        val testBid1 = IARun.enchere(PlayerPosition.NORTH,listBids, myCards,0)


        when (testBid1) {
            is Pass -> println("$nameTest:PASS - - your partner did bid already very high  : $testBid1")
            else -> println("$nameTest:FAIL - you cannot bid higher - it was already very hight from your partner - you tried  $testBid1")
        }



    }

}