package fr.hornik.coinche

import fr.hornik.coinche.business.masterColor
import fr.hornik.coinche.business.printHand
import fr.hornik.coinche.business.totalRemainingNumber
import fr.hornik.coinche.model.*
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

        val nameTest = object {}.javaClass.enclosingMethod.name

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
        val nameTest = object {}.javaClass.enclosingMethod.name

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
        val prevBid = Capot(CardColor.DIAMOND,PlayerPosition.NORTH,true)
        val listBids = listOf(Pass(PlayerPosition.EAST), prevBid, Pass(PlayerPosition.WEST))


        val testBid1 = IARun.enchere(PlayerPosition.NORTH, listBids, myCards, 0)
        traceLevel = oldTraceLevel

        assert(testBid1.curPoint() == 500) { "$nameTest FAIL - $testBid1 is not accurate" }

        debugPrintln(dbgLevel.REGULAR, "$nameTest:PASS - Bid was : $testBid1")

    }

    @Test
    fun testEnchere110 () {
        val nameTest = object {}.javaClass.enclosingMethod.name

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


        val prevBid = SimpleBid(CardColor.DIAMOND, 120, PlayerPosition.SOUTH)
        val listBids = listOf(Pass(PlayerPosition.EAST), prevBid, Pass(PlayerPosition.WEST))

        val testBid1 = IARun.enchere(PlayerPosition.NORTH, listBids, myCards, 0)

        traceLevel = oldTraceLevel
        assert((testBid1.curPoint() >= 110) && (testBid1.curPoint() <= 140)) { "$nameTest FAIL - $testBid1 is not accurate" }
        debugPrintln(dbgLevel.REGULAR, "$nameTest:PASS - Bid $testBid1")


    }

    @Test
    fun testEnchere120() {
        val nameTest = object {}.javaClass.enclosingMethod.name

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
        val nameTest = object {}.javaClass.enclosingMethod.name

        // Bid should be Generale + Belote
        val oldTraceLevel = traceLevel
        // traceLevel = dbgLevel.ALL
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
    @Test
    fun testEnchere150() {
        // Test ou l'on doit prendre l'enchere du partenaire
        val nameTest = object {}.javaClass.enclosingMethod.name

        // Bid should be Generale + Belote
        val oldTraceLevel = traceLevel
        // traceLevel = dbgLevel.ALL
        var myCards: MutableList<Card> = mutableListOf<Card>()

        myCards.add(Card(CardValue.JACK, CardColor.HEART, playable = null))
        myCards.add(Card(CardValue.NINE, CardColor.HEART, playable = null))

        myCards.add(Card(CardValue.NINE, CardColor.DIAMOND, playable = null))
        myCards.add(Card(CardValue.SEVEN, CardColor.DIAMOND, playable = null))
        myCards.add(Card(CardValue.ACE, CardColor.DIAMOND, playable = null))

        myCards.add(Card(CardValue.ACE, CardColor.SPADE, playable = null))

        myCards.add(Card(CardValue.TEN, CardColor.CLUB, playable = null))
        myCards.add(Card(CardValue.ACE, CardColor.CLUB, playable = null))

        printHand(dbgLevel.DEBUG, myCards, "$nameTest:Mycards are ")


        val prevBid = SimpleBid(CardColor.DIAMOND, 140, PlayerPosition.SOUTH)
        val listBids = listOf(Pass(), prevBid, Pass())

        val testBid1 = IARun.enchere(PlayerPosition.NORTH, listBids, myCards, 0)
        traceLevel = oldTraceLevel
        assert(((testBid1.curPoint() <= 130 ))) { "$nameTest FAIL - $testBid1 is not accurate" }
        debugPrintln(dbgLevel.REGULAR, "$nameTest:PASS - Bid $testBid1")

    }
    @Test
    fun testEnchere155() {
        // Test ou l'on doit prendre l'enchere du partenaire
        val nameTest = object {}.javaClass.enclosingMethod.name

        // Bid should be Generale + Belote
        val oldTraceLevel = traceLevel
        //traceLevel = dbgLevel.ALL
        var myCards: MutableList<Card> = mutableListOf<Card>()

        myCards.add(Card(CardValue.JACK, CardColor.HEART, playable = null))
        myCards.add(Card(CardValue.NINE, CardColor.HEART, playable = null))

        myCards.add(Card(CardValue.NINE, CardColor.DIAMOND, playable = null))
        myCards.add(Card(CardValue.QUEEN, CardColor.DIAMOND, playable = null))
        myCards.add(Card(CardValue.ACE, CardColor.DIAMOND, playable = null))

        myCards.add(Card(CardValue.ACE, CardColor.SPADE, playable = null))

        myCards.add(Card(CardValue.TEN, CardColor.CLUB, playable = null))
        myCards.add(Card(CardValue.ACE, CardColor.CLUB, playable = null))

        printHand(dbgLevel.DEBUG, myCards, "$nameTest:Mycards are ")


        val prevBid = SimpleBid(CardColor.DIAMOND, 140, PlayerPosition.SOUTH)
        val listBids = listOf(Pass(), prevBid, Pass())

        val testBid1 = IARun.enchere(PlayerPosition.NORTH, listBids, myCards, 0)
        traceLevel = oldTraceLevel
        assert(((testBid1.curPoint() > 130 ))) { "$nameTest FAIL - $testBid1 is not accurate" }
        debugPrintln(dbgLevel.REGULAR, "$nameTest:PASS - Bid $testBid1")

    }
    @Test
    fun testEnchere160() {
        // Test ou l'on doit prendre l'enchere du partenaire
        val nameTest = object {}.javaClass.enclosingMethod.name

        val oldTraceLevel = traceLevel
        //traceLevel = dbgLevel.ALL
        var myCards: MutableList<Card> = mutableListOf<Card>()

        myCards.add(Card(CardValue.JACK, CardColor.HEART, playable = null))

        myCards.add(Card(CardValue.NINE, CardColor.CLUB, playable = null))
        myCards.add(Card(CardValue.JACK, CardColor.CLUB, playable = null))
        myCards.add(Card(CardValue.ACE, CardColor.CLUB, playable = null))

        myCards.add(Card(CardValue.TEN, CardColor.SPADE, playable = null))
        myCards.add(Card(CardValue.ACE, CardColor.SPADE, playable = null))

        myCards.add(Card(CardValue.TEN, CardColor.DIAMOND, playable = null))
        myCards.add(Card(CardValue.SEVEN, CardColor.DIAMOND, playable = null))

        printHand(dbgLevel.DEBUG, myCards, "$nameTest:Mycards are ")


        val prevBid = SimpleBid(CardColor.HEART, 80, PlayerPosition.EAST)
        val listBids = listOf(Pass(PlayerPosition.NORTH), prevBid, Pass(PlayerPosition.SOUTH))

        val testBid1 = IARun.enchere(PlayerPosition.WEST, listBids, myCards, 0)
        traceLevel = oldTraceLevel
        assert(((testBid1.curPoint() >= 130 ) && testBid1.curColor() == CardColor.HEART)) { "$nameTest FAIL - $testBid1 is not accurate" }
        debugPrintln(dbgLevel.REGULAR, "$nameTest:PASS - Bid $testBid1")

    }

    @Test
    fun testEnchere170() {
        // Test ou l'on doit prendre l'enchere du partenaire
        val nameTest = object {}.javaClass.enclosingMethod.name

        val oldTraceLevel = traceLevel
        // traceLevel = dbgLevel.ALL
        var myCards: MutableList<Card> = mutableListOf<Card>()

        myCards.add(Card(CardValue.ACE, CardColor.HEART, playable = null))

        myCards.add(Card(CardValue.NINE, CardColor.CLUB, playable = null))
        myCards.add(Card(CardValue.JACK, CardColor.CLUB, playable = null))
        myCards.add(Card(CardValue.SEVEN, CardColor.CLUB, playable = null))

        myCards.add(Card(CardValue.TEN, CardColor.SPADE, playable = null))
        myCards.add(Card(CardValue.ACE, CardColor.SPADE, playable = null))

        myCards.add(Card(CardValue.TEN, CardColor.DIAMOND, playable = null))
        myCards.add(Card(CardValue.SEVEN, CardColor.DIAMOND, playable = null))

        printHand(dbgLevel.DEBUG, myCards, "$nameTest:Mycards are ")


        val prevBid = SimpleBid(CardColor.CLUB, 80, PlayerPosition.EAST)
        val listBids = listOf(Pass(PlayerPosition.NORTH), prevBid, Pass(PlayerPosition.SOUTH))

        val testBid1 = IARun.enchere(PlayerPosition.WEST, listBids, myCards, 0)
        traceLevel = oldTraceLevel
        assert(((testBid1.curPoint() >= 130 ) && testBid1.curColor() == CardColor.CLUB)) { "$nameTest FAIL - $testBid1 is not accurate" }
        debugPrintln(dbgLevel.REGULAR, "$nameTest:PASS - Bid $testBid1")

    }
    @Test
    fun testEnchere180() {
        // Test ou l'on doit prendre l'enchere du partenaire
        val nameTest = object {}.javaClass.enclosingMethod.name

        val oldTraceLevel = traceLevel
        //traceLevel = dbgLevel.ALL
        var myCards: MutableList<Card> = mutableListOf<Card>()

        myCards.add(Card(CardValue.ACE, CardColor.HEART, playable = null))
        myCards.add(Card(CardValue.EIGHT, CardColor.HEART, playable = null))


        myCards.add(Card(CardValue.TEN, CardColor.SPADE, playable = null))
        myCards.add(Card(CardValue.SEVEN, CardColor.SPADE, playable = null))
        myCards.add(Card(CardValue.ACE, CardColor.SPADE, playable = null))

        myCards.add(Card(CardValue.TEN, CardColor.DIAMOND, playable = null))
        myCards.add(Card(CardValue.SEVEN, CardColor.DIAMOND, playable = null))
        myCards.add(Card(CardValue.EIGHT, CardColor.DIAMOND, playable = null))

        printHand(dbgLevel.DEBUG, myCards, "$nameTest:Mycards are ")


        val prevBid = SimpleBid(CardColor.CLUB, 120, PlayerPosition.EAST)
        val listBids = listOf(Pass(PlayerPosition.NORTH), prevBid, Pass(PlayerPosition.SOUTH))

        val testBid1 = IARun.enchere(PlayerPosition.WEST, listBids, myCards, 0)
        traceLevel = oldTraceLevel
        assert(((testBid1.curPoint() == 130 ) && testBid1.curColor() == CardColor.CLUB)) { "$nameTest FAIL - $testBid1 is not accurate" }
        debugPrintln(dbgLevel.REGULAR, "$nameTest:PASS - Bid $testBid1")

    }

    @Test
    fun testEnchere190() {
        // Test ou l'on doit prendre l'enchere du partenaire
        val nameTest = object {}.javaClass.enclosingMethod.name

        val oldTraceLevel = traceLevel
        //traceLevel = dbgLevel.ALL
        var myCards: MutableList<Card> = mutableListOf<Card>()

        myCards.add(Card(CardValue.ACE, CardColor.HEART, playable = null))
        myCards.add(Card(CardValue.EIGHT, CardColor.HEART, playable = null))


        myCards.add(Card(CardValue.TEN, CardColor.SPADE, playable = null))
        myCards.add(Card(CardValue.SEVEN, CardColor.SPADE, playable = null))
        myCards.add(Card(CardValue.ACE, CardColor.SPADE, playable = null))

        myCards.add(Card(CardValue.TEN, CardColor.DIAMOND, playable = null))
        myCards.add(Card(CardValue.SEVEN, CardColor.DIAMOND, playable = null))

        myCards.add(Card(CardValue.QUEEN, CardColor.CLUB, playable = null))

        printHand(dbgLevel.DEBUG, myCards, "$nameTest:Mycards are ")


        val prevBid = SimpleBid(CardColor.CLUB, 120, PlayerPosition.EAST)
        val listBids = listOf(Pass(PlayerPosition.NORTH), prevBid, Pass(PlayerPosition.SOUTH))

        val testBid1 = IARun.enchere(PlayerPosition.WEST, listBids, myCards, 0)
        traceLevel = oldTraceLevel
        assert(((testBid1.curPoint() == 250 ) && testBid1.curColor() == CardColor.CLUB)) { "$nameTest FAIL - $testBid1 is not accurate" }
        debugPrintln(dbgLevel.REGULAR, "$nameTest:PASS - Bid $testBid1")

    }

    @Test
    fun testEnchere200() {
        // Test ou l'on doit prendre l'enchere du partenaire
        val nameTest = object {}.javaClass.enclosingMethod.name

        val oldTraceLevel = traceLevel
        //traceLevel = dbgLevel.ALL
        var myCards: MutableList<Card> = mutableListOf<Card>()



        myCards.add(Card(CardValue.JACK, CardColor.SPADE, playable = null))
        myCards.add(Card(CardValue.NINE, CardColor.SPADE, playable = null))
        myCards.add(Card(CardValue.ACE, CardColor.SPADE, playable = null))
        myCards.add(Card(CardValue.KING, CardColor.SPADE, playable = null))
        myCards.add(Card(CardValue.QUEEN, CardColor.SPADE, playable = null))

        myCards.add(Card(CardValue.ACE, CardColor.DIAMOND, playable = null))
        myCards.add(Card(CardValue.TEN, CardColor.DIAMOND, playable = null))


        printHand(dbgLevel.DEBUG, myCards, "$nameTest:Mycards are ")


        val prevBid = General(CardColor.CLUB,  PlayerPosition.EAST)
        val listBids = listOf(Pass(PlayerPosition.NORTH), prevBid, Pass(PlayerPosition.SOUTH))

        val testBid1 = IARun.enchere(PlayerPosition.WEST, listBids, myCards, 0)
        traceLevel = oldTraceLevel
        assert(((testBid1.curPoint() >= 500 ) && testBid1.curColor() == CardColor.SPADE)) { "$nameTest FAIL - $testBid1 is not accurate" }
        debugPrintln(dbgLevel.REGULAR, "$nameTest:PASS - Bid $testBid1")

    }
    @Test
    fun testEnchere210() {
        // le partner coinche 80 Coeur
        // Verifier que malgre notre jeu de general on ne fait pas une enchere irreguliere ( sinon exception dans le timer : pas bon )


        val nameTest = object {}.javaClass.enclosingMethod.name

        val oldTraceLevel = traceLevel
        //traceLevel = dbgLevel.ALL
        var myCards: MutableList<Card> = mutableListOf<Card>()



        myCards.add(Card(CardValue.JACK, CardColor.SPADE, playable = null))
        myCards.add(Card(CardValue.NINE, CardColor.SPADE, playable = null))
        myCards.add(Card(CardValue.ACE, CardColor.SPADE, playable = null))
        myCards.add(Card(CardValue.KING, CardColor.SPADE, playable = null))
        myCards.add(Card(CardValue.QUEEN, CardColor.SPADE, playable = null))

        myCards.add(Card(CardValue.ACE, CardColor.DIAMOND, playable = null))
        myCards.add(Card(CardValue.TEN, CardColor.DIAMOND, playable = null))


        printHand(dbgLevel.DEBUG, myCards, "$nameTest:Mycards are ")


        val prevBid = SimpleBid(CardColor.HEART,  80,PlayerPosition.EAST)
        val partnerBid = Coinche(prevBid,PlayerPosition.SOUTH,false)
        val lastBid = Pass(PlayerPosition.WEST)
        val listBids = listOf(prevBid,partnerBid,lastBid)

        val testBid1 = IARun.enchere(PlayerPosition.NORTH, listBids, myCards, 0)
        traceLevel = oldTraceLevel
        assert(testBid1 is Pass ) { "$nameTest FAIL - $testBid1 is not accurate" }
        debugPrintln(dbgLevel.REGULAR, "$nameTest:PASS - Bid $testBid1")

    }

    @Test
    fun testEnchere220() {
        // le partner coinche 80 Coeur
        // Verifier que malgre notre jeu de general on ne fait pas une enchere irreguliere ( sinon exception dans le timer : pas bon )


        val nameTest = object {}.javaClass.enclosingMethod.name

        val oldTraceLevel = traceLevel
        //traceLevel = dbgLevel.ALL
        var myCards: MutableList<Card> = mutableListOf<Card>()



        myCards.add(Card(CardValue.JACK, CardColor.SPADE, playable = null))
        myCards.add(Card(CardValue.NINE, CardColor.SPADE, playable = null))
        myCards.add(Card(CardValue.ACE, CardColor.SPADE, playable = null))
        myCards.add(Card(CardValue.KING, CardColor.SPADE, playable = null))
        myCards.add(Card(CardValue.QUEEN, CardColor.SPADE, playable = null))

        myCards.add(Card(CardValue.ACE, CardColor.DIAMOND, playable = null))
        myCards.add(Card(CardValue.TEN, CardColor.DIAMOND, playable = null))


        printHand(dbgLevel.DEBUG, myCards, "$nameTest:Mycards are ")


        val prevBid = SimpleBid(CardColor.HEART,  80,PlayerPosition.EAST)
        val partnerBid = Coinche(prevBid,PlayerPosition.SOUTH,false)
        val listBids = listOf(prevBid,partnerBid)

        val testBid1 = IARun.enchere(PlayerPosition.WEST, listBids, myCards, 0)
        traceLevel = oldTraceLevel
        assert(testBid1 is Pass ) { "$nameTest FAIL - $testBid1 is not accurate" }
        debugPrintln(dbgLevel.REGULAR, "$nameTest:PASS - Bid $testBid1")

    }

    @Test
    fun testPlayCard001() {


        val nameTest = object {}.javaClass.enclosingMethod.name

        val oldTraceLevel = traceLevel
        //traceLevel = dbgLevel.ALL

        val prevBid = SimpleBid(CardColor.HEART,  80,PlayerPosition.EAST)
        val partnerBid = Coinche(prevBid,PlayerPosition.SOUTH,false)
        val listBids = listOf(prevBid,partnerBid)

        val aCard = masterColor(CardColor.HEART,CardColor.HEART,allCardsPlayed = listOf())

        debugPrintln(dbgLevel.DEBUG,"masterCard at ${CardColor.HEART} is ${aCard}")
        traceLevel = oldTraceLevel
        assert(aCard?.value ?: CardValue.SEVEN == CardValue.JACK ) { "$nameTest FAIL - $aCard is not accurate" }
        debugPrintln(dbgLevel.REGULAR, "$nameTest:PASS - Master is  $aCard")

    }
    @Test
    fun testPlayCard002() {

        val nameTest = object {}.javaClass.enclosingMethod.name

        val oldTraceLevel = traceLevel
        traceLevel = dbgLevel.ALL

        val prevBid = SimpleBid(CardColor.HEART,  80,PlayerPosition.EAST)
        val partnerBid = Coinche(prevBid,PlayerPosition.SOUTH,false)
        val listBids = listOf(prevBid,partnerBid)

        val aCard = masterColor(CardColor.HEART,CardColor.DIAMOND,allCardsPlayed = listOf())

        debugPrintln(dbgLevel.DEBUG,"masterCard at ${CardColor.DIAMOND} is ${aCard}")
        traceLevel = oldTraceLevel
        assert(aCard?.value ?: CardValue.SEVEN == CardValue.ACE ) { "$nameTest FAIL - $aCard is not accurate" }
        debugPrintln(dbgLevel.REGULAR, "$nameTest:PASS - Master is  $aCard")

    }
    @Test
    fun testPlayCard003() {

        val nameTest = object {}.javaClass.enclosingMethod.name

        val oldTraceLevel = traceLevel
        traceLevel = dbgLevel.ALL
        var playedCards: MutableList<CardPlayed> = mutableListOf()

        playedCards.add(CardPlayed(Card(CardValue.JACK, CardColor.HEART, playable = null)))
        playedCards.add(CardPlayed(Card(CardValue.ACE, CardColor.DIAMOND, playable = null)))
        playedCards.add(CardPlayed(Card(CardValue.ACE, CardColor.SPADE, playable = null)))
        playedCards.add(CardPlayed(Card(CardValue.TEN, CardColor.SPADE, playable = null)))
        playedCards.add(CardPlayed(Card(CardValue.KING, CardColor.SPADE, playable = null)))
        playedCards.add(CardPlayed(Card(CardValue.SEVEN, CardColor.SPADE, playable = null)))
        playedCards.add(CardPlayed(Card(CardValue.EIGHT, CardColor.SPADE, playable = null)))
        playedCards.add(CardPlayed(Card(CardValue.ACE, CardColor.CLUB, playable = null)))
        playedCards.add(CardPlayed(Card(CardValue.TEN, CardColor.CLUB, playable = null)))
        playedCards.add(CardPlayed(Card(CardValue.KING, CardColor.CLUB, playable = null)))
        playedCards.add(CardPlayed(Card(CardValue.QUEEN, CardColor.CLUB, playable = null)))
        playedCards.add(CardPlayed(Card(CardValue.JACK, CardColor.CLUB, playable = null)))
        playedCards.add(CardPlayed(Card(CardValue.NINE, CardColor.CLUB, playable = null)))
        playedCards.add(CardPlayed(Card(CardValue.EIGHT, CardColor.CLUB, playable = null)))
        playedCards.add(CardPlayed(Card(CardValue.SEVEN, CardColor.CLUB, playable = null)))


        val prevBid = SimpleBid(CardColor.HEART,  80,PlayerPosition.EAST)
        val partnerBid = Coinche(prevBid,PlayerPosition.SOUTH,false)
        val listBids = listOf(prevBid,partnerBid)
        val atout = CardColor.HEART
        val color = CardColor.DIAMOND
        val color2 = CardColor.SPADE
        val color3 = CardColor.CLUB

        val seven = CardValue.SEVEN
        val aCard = masterColor(color,atout,allCardsPlayed = playedCards)
        val bCard = masterColor(atout,atout,allCardsPlayed = playedCards)
        val cCard = masterColor(color2,atout,allCardsPlayed = playedCards)
        val dCard = masterColor(color3,atout,allCardsPlayed = playedCards)

        debugPrintln(dbgLevel.DEBUG,"masterCard at ${color} is ${aCard}")
        debugPrintln(dbgLevel.DEBUG,"masterCard at ${atout} is ${bCard}")
        debugPrintln(dbgLevel.DEBUG,"masterCard at ${color2} is ${cCard}")
        debugPrintln(dbgLevel.DEBUG,"masterCard at ${color3} is ${dCard}")
        traceLevel = oldTraceLevel
        assert((aCard?.value ?: seven == CardValue.TEN )
                && (bCard?.value ?: seven == CardValue.NINE )
                && (cCard?.value ?: seven == CardValue.QUEEN )
                && (dCard == null ) ) { "$nameTest FAIL - $aCard is not accurate" }
        debugPrintln(dbgLevel.REGULAR, "$nameTest:PASS - Master is  $aCard and $bCard")

    }

    @Test
    fun testPlayCard004() {

        val nameTest = object {}.javaClass.enclosingMethod.name

        val oldTraceLevel = traceLevel
        traceLevel = dbgLevel.ALL
        var myCards: MutableList<Card> = mutableListOf<Card>()



        myCards.add(Card(CardValue.QUEEN, CardColor.SPADE, playable = null))
        myCards.add(Card(CardValue.NINE, CardColor.SPADE, playable = null))


        var playedCards: MutableList<CardPlayed> = mutableListOf()

        playedCards.add(CardPlayed(Card(CardValue.JACK, CardColor.HEART, playable = null)))
        playedCards.add(CardPlayed(Card(CardValue.ACE, CardColor.SPADE, playable = null)))
        playedCards.add(CardPlayed(Card(CardValue.TEN, CardColor.SPADE, playable = null)))
        playedCards.add(CardPlayed(Card(CardValue.KING, CardColor.SPADE, playable = null)))
        playedCards.add(CardPlayed(Card(CardValue.SEVEN, CardColor.SPADE, playable = null)))
        playedCards.add(CardPlayed(Card(CardValue.EIGHT, CardColor.SPADE, playable = null)))
        playedCards.add(CardPlayed(Card(CardValue.ACE, CardColor.CLUB, playable = null)))
        playedCards.add(CardPlayed(Card(CardValue.TEN, CardColor.CLUB, playable = null)))
        playedCards.add(CardPlayed(Card(CardValue.KING, CardColor.CLUB, playable = null)))
        playedCards.add(CardPlayed(Card(CardValue.QUEEN, CardColor.CLUB, playable = null)))
        playedCards.add(CardPlayed(Card(CardValue.JACK, CardColor.CLUB, playable = null)))
        playedCards.add(CardPlayed(Card(CardValue.NINE, CardColor.CLUB, playable = null)))
        playedCards.add(CardPlayed(Card(CardValue.EIGHT, CardColor.CLUB, playable = null)))
        playedCards.add(CardPlayed(Card(CardValue.SEVEN, CardColor.CLUB, playable = null)))


        val prevBid = SimpleBid(CardColor.HEART,  80,PlayerPosition.EAST)
        val partnerBid = Coinche(prevBid,PlayerPosition.SOUTH,false)
        val listBids = listOf(prevBid,partnerBid)
        val heart = CardColor.HEART
        val diamond = CardColor.DIAMOND
        val spade = CardColor.SPADE
        val club = CardColor.CLUB
        val heartnb = totalRemainingNumber(heart,playedCards,myCards)
        val spadenb = totalRemainingNumber(spade,playedCards,myCards)
        val clubnb = totalRemainingNumber(club,playedCards,myCards)
        val diamondnb = totalRemainingNumber(diamond,playedCards,myCards)



        traceLevel = oldTraceLevel
        assert((heartnb==7) && (diamondnb ==8)&& (spadenb==1) && (clubnb==0)) { "$nameTest FAIL -  h=$heartnb d=$diamondnb s=$spadenb c=$clubnb is not accurate" }
        debugPrintln(dbgLevel.REGULAR, "$nameTest:PASS - \"$nameTest FAIL -  h=$heartnb d=$diamondnb s=$spadenb c=$clubnb is OK \n")

    }

}

