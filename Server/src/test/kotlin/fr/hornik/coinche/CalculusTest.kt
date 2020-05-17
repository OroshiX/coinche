package fr.hornik.coinche

import fr.hornik.coinche.business.*
import fr.hornik.coinche.model.*
import fr.hornik.coinche.model.values.BeloteValue
import fr.hornik.coinche.model.values.CardColor
import fr.hornik.coinche.model.values.CardValue
import fr.hornik.coinche.model.values.PlayerPosition
import fr.hornik.coinche.util.dbgLevel
import fr.hornik.coinche.util.debugPrintln
import fr.hornik.coinche.util.traceLevel
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CalculusTest {
    private lateinit var bids: List<Bid>
    private val south = PlayerPosition.SOUTH
    private val north = PlayerPosition.NORTH
    private val west = PlayerPosition.WEST
    private val east = PlayerPosition.EAST
    private val heart = CardColor.HEART
    private val club = CardColor.CLUB
    private val spade = CardColor.SPADE
    private val diamond = CardColor.DIAMOND


    @BeforeEach
    fun initBids() {
        //traceLevel = dbgLevel.NONE

        debugPrintln(dbgLevel.FUNCTION, "Enter initBids")

        bids = listOf(
                SimpleBid(heart, 80, north),
                Pass(east),
                Capot(spade, north, true),
                General(diamond, south, false),
                Coinche(SimpleBid(club, 90, west), north, surcoinche = true))
    }

    @Test
    fun testBid() {
        //testUnit()
        val nameTest = object {}.javaClass.enclosingMethod.name


        var EW: MutableList<MutableList<CardPlayed>> = mutableListOf()
        var NS: MutableList<MutableList<CardPlayed>> = mutableListOf()
        var aPair = Pair(EW, NS)

        for (i in 1..20) {
            debugPrintln(dbgLevel.HTML, "<h2> Partie N $i</h2>\n\n")
            aPair = testPartie(pliNS = NS, pliEW = EW)
            EW = aPair.first
            NS = aPair.second

        }
        assert(true) { "$nameTest FAIL " }

    }

    fun testUnit() {
        debugPrintln(dbgLevel.FUNCTION,"Enter testUnit")

        for (bid in bids) {
            debugPrintln(dbgLevel.MISC, bid)
        }
    }


    @Test
    fun testValidCartes () {
        // EAST : 100 HEART
        // East plays 10 Heart
        // South should be able to play K HEART  and only K Heart
        val nameTest = object {}.javaClass.enclosingMethod.name

        var myCards: MutableList<Card> = mutableListOf<Card>()
        var currBid :Bid = SimpleBid(CardColor.HEART, 100, PlayerPosition.EAST)
        var listCardPlayed = listOf(CardPlayed(Card(CardValue.TEN,CardColor.HEART),belote = BeloteValue.NONE,position = PlayerPosition.EAST))
        myCards.add(Card(CardValue.KING,CardColor.CLUB,playable = null))
        myCards.add(Card(CardValue.NINE,CardColor.CLUB,playable = null))
        myCards.add(Card(CardValue.KING,CardColor.DIAMOND,playable = null))
        myCards.add(Card(CardValue.SEVEN,CardColor.DIAMOND,playable = null))
        myCards.add(Card(CardValue.EIGHT,CardColor.DIAMOND,playable = null))
        myCards.add(Card(CardValue.EIGHT,CardColor.SPADE,playable = null))
        myCards.add(Card(CardValue.NINE,CardColor.SPADE,playable = null))
        myCards.add(Card(CardValue.KING,CardColor.HEART,playable = null))
        printHand(dbgLevel.DEBUG,myCards,"Mycards are ")
        val c = allValidCardsToPlay(myCardsInHand = myCards, bid = currBid, cardsOnTable = listCardPlayed)

        debugPrintln(dbgLevel.DEBUG, "\nBid is $currBid\n\n")

        printHand(dbgLevel.DEBUG, listCardPlayed.map { it -> it.card }, "\n\nOn table")

        printHand(dbgLevel.DEBUG, c, "\nAnd I can Play")

        for (mcard in myCards) {
            mcard.playable = c.contains(mcard)
        }
        assert(!((myCards.filter{e -> (e.playable == true)}.size != 1 ) || (myCards.filter{e -> (e.playable == true)}.first().color != CardColor.HEART))) { "$nameTest FAIL " }
        debugPrintln(dbgLevel.REGULAR, "$nameTest:PASS .......")



    }
    @Test
    fun testCoincheValidCartes () {

        // North : 100 Spade
        // South : Coinche
        // East plays K Heart
        // South plays 8 Club ( no heart / no spade )
        // West has AS 10S AH 9H 7H JC JD 7D
        // playable should be true for A,9 or 7 H

        val nameTest = object {}.javaClass.enclosingMethod.name
        val oldTraceLevel = traceLevel
        //traceLevel = dbgLevel.ALL

        var myCards: MutableList<Card> = mutableListOf<Card>()
        var currBid :Bid = SimpleBid(CardColor.SPADE, 100, PlayerPosition.NORTH)
        var listCardPlayed = listOf(CardPlayed(Card(CardValue.KING,CardColor.HEART),belote = BeloteValue.NONE,position = PlayerPosition.EAST),(CardPlayed(Card(CardValue.EIGHT,CardColor.CLUB),belote = BeloteValue.NONE,position = PlayerPosition.SOUTH)))
        var suBid:Bid = Coinche(currBid,PlayerPosition.SOUTH,surcoinche = false)


        myCards.add(Card(CardValue.ACE,CardColor.SPADE,playable = null))
        myCards.add(Card(CardValue.TEN,CardColor.SPADE,playable = null))
        myCards.add(Card(CardValue.ACE,CardColor.HEART,playable = null))
        myCards.add(Card(CardValue.NINE,CardColor.HEART,playable = null))
        myCards.add(Card(CardValue.SEVEN,CardColor.HEART,playable = null))
        myCards.add(Card(CardValue.JACK,CardColor.CLUB,playable = null))
        myCards.add(Card(CardValue.JACK,CardColor.DIAMOND,playable = null))
        myCards.add(Card(CardValue.SEVEN,CardColor.DIAMOND,playable = null))

        printHand(dbgLevel.DEBUG,myCards,"Mycards are ")
        val c = allValidCardsToPlay(myCardsInHand = myCards, bid = suBid, cardsOnTable = listCardPlayed)

        debugPrintln(dbgLevel.DEBUG,"\nBid is $suBid\n\n")
        if ((dbgLevel.DEBUG and traceLevel) != 0) {

            printHand(dbgLevel.DEBUG,listCardPlayed.map { it -> it.card }, "\n\nOn table")

            printHand(dbgLevel.DEBUG,c, "\nAnd I can Play")
        }
        for (mcard in myCards) {
            mcard.playable = c.contains(mcard)
        }



        traceLevel = oldTraceLevel
        assert((c.size == 3 ) || c.none { e -> (e.color != CardColor.HEART)} ) { "$nameTest FAIL " }
        debugPrintln(dbgLevel.REGULAR, "$nameTest:PASS .......")
    }


    fun testPartie(pliNS: MutableList<MutableList<CardPlayed>>, pliEW: MutableList<MutableList<CardPlayed>>): Pair<MutableList<MutableList<CardPlayed>>, MutableList<MutableList<CardPlayed>>> {
        val nameTest = object {}.javaClass.enclosingMethod.name
        val oldTraceLevel = traceLevel
        //traceLevel = dbgLevel.ALL

        debugPrintln(dbgLevel.HTML, "<table><tr><td>\n")
        val mapPos = mapOf(PlayerPosition.NORTH to 0,
                PlayerPosition.WEST to 1,
                PlayerPosition.SOUTH to 2,
                PlayerPosition.EAST to 3)

        val spreadRand = listOf(Triple(3, 2, 3), Triple(2, 3, 3), Triple(3, 3, 2))
        val lRand = PlayerPosition.values()
        val tHand = (0..3).toList()
        val ptsRand = listOf(80, 90, 100, 110, 120, 130, 140, 150, 160)
        var firstD = lRand.random()
        var taker = lRand.random()
        val aBid = SimpleBid(CardColor.values().random(), ptsRand.random(), taker)
        var temp1: List<List<Card>>

        if ((pliNS.size == 0) && (pliEW.size == 0)) {
            temp1 = firstDealOfCards(firstD, spreadRand.random())
        } else {
            temp1 = dealCards(pliNS, pliEW, 10, spreadRand.random(), firstD)
        }
        var fourHands = listOf<MutableList<Card>>(temp1[0].toMutableList(), temp1[1].toMutableList(), temp1[2].toMutableList(), temp1[3].toMutableList())

        var indexPlayer = (mapPos[firstD]!! + 1) % 4
        val PlisNS: MutableList<MutableList<CardPlayed>> = mutableListOf()
        val PlisEW: MutableList<MutableList<CardPlayed>> = mutableListOf()
        var dixDer = PlayerPosition.NORTH
        debugPrintln(dbgLevel.HTML, "<pre>Contrat : $aBid ")


        for (tour in 1..8) {
            if ((tour % 2) == 1) {
                debugPrintln(dbgLevel.HTML, "</pre><tr><td><pre>\n <b>Tour $tour</b>")
            } else {
                debugPrintln(dbgLevel.HTML, "</pre><td><pre>\n<b>Tour $tour</b>")
            }
            val listCardP = mutableListOf<CardPlayed>()
            for (i in 0..3) {
                fourHands[tHand[indexPlayer]].sortWith(Comparator { o1, o2 -> o1.color.compareTo(o2.color) })
                if ((dbgLevel.HTML and traceLevel) != 0)
                    printHand(dbgLevel.DEBUG,fourHands[tHand[indexPlayer]], "Player ${lRand[tHand[indexPlayer]]}")
                val c = allValidCardsToPlay(fourHands[indexPlayer], aBid, listCardP)
                debugPrintln(dbgLevel.HTML, "")

                    printHand(dbgLevel.DEBUG,c, "valid cards for ${lRand[indexPlayer]}")
                debugPrintln(dbgLevel.HTML, "<font color=#FF0000 bgcolor=0x00FFFF> ${lRand[indexPlayer]} plays  ${c[0].value}  of ${c[0].color} </font>")
                listCardP.add(CardPlayed(c[0], BeloteValue.NONE, lRand[indexPlayer]))
                fourHands[tHand[indexPlayer]].remove(c[0])
                indexPlayer = (indexPlayer + 1) % 4
            }
            indexPlayer = mapPos[calculateWinnerTrick(listCardP, aBid.curColor())]!!
            if (tour == 8) {
                dixDer = lRand[indexPlayer]
            }
            if (indexPlayer % 2 == 0) {
                PlisNS.add(listCardP)
            } else {
                PlisEW.add(listCardP)
            }
        }
        debugPrintln(dbgLevel.HTML, "</pre></tr><tr><td>")
        debugPrintln(dbgLevel.REGULAR, "\n\nScore = ${calculateScoreTricks(PlisNS, PlisEW, dixDer, aBid)}")
        debugPrintln(dbgLevel.HTML, "</table>")
        debugPrintln(dbgLevel.HTML, "<pre>")
        debugPrintln(dbgLevel.HTML, "\n\nDisplay Structure of tricks\n")
        var i = 0
        for (li in PlisEW) {
            i = i + 1
            printHand(dbgLevel.DEBUG,li.map { it -> it.card }, "EastWest Pli $i")
        }
        for (li in PlisNS) {
            i = i + 1
            printHand(dbgLevel.DEBUG, li.map { it -> it.card }, "North South Pli $i")
        }
        debugPrintln(dbgLevel.HTML, "</pre><br><br><br>")

        traceLevel = oldTraceLevel

        // Not really a unit test more a system test .... Ananlysis is manual.
        assert(true) { "$nameTest FAIL " }
        debugPrintln(dbgLevel.REGULAR, "$nameTest:PASS .......")
        return (Pair(PlisEW, PlisNS))

    }


    @Test
    fun Bid001() {
        val nameTest = object {}.javaClass.enclosingMethod.name


        // Scenario :
        // N : PASS
        // E : 80 Heart
        // S : 100 DIAMOND
        // W : 100 HEART -> should be Invalid


        val oldTraceLevel = traceLevel
        //traceLevel = dbgLevel.ALL
        val allBids = listOf(
                Pass(north),
                SimpleBid(heart, 80, east),
                SimpleBid(diamond, 100, south)
        )
        val bid = SimpleBid(heart, 100, west)

        val Returnvalue = isValidBid(allBids, bid)

        assert(!Returnvalue) { "$nameTest FAIL " }
        debugPrintln(dbgLevel.REGULAR, "$nameTest:PASS .......")
    }

    @Test
    fun Bid002() {


        // Scenario :
        // N : PASS
        // E : 80 Heart
        // S : 100 DIAMOND
        // W : 110 HEART -> should be Valid

        val nameTest = object {}.javaClass.enclosingMethod.name

        val oldTraceLevel = traceLevel
        //traceLevel = dbgLevel.ALL

        val allBids = listOf(
                Pass(north),
                SimpleBid(heart, 80, east),
                SimpleBid(diamond, 100, south)
        )
        val bid = SimpleBid(heart, 110, west)

        val Returnvalue = isValidBid(allBids, bid)

        assert(Returnvalue) { "$nameTest FAIL " }
        debugPrintln(dbgLevel.REGULAR, "$nameTest:PASS .......")

    }
    @Test
    fun Bid003() {

        // Scenario :
        // N : PASS
        // E : 80 Heart
        // S : 100 DIAMOND
        // W : PASS
        // N : Coinche --> should be invalid


        val nameTest = object {}.javaClass.enclosingMethod.name

        val oldTraceLevel = traceLevel
        //traceLevel = dbgLevel.ALL

        val allBids = listOf(
                Pass(north),
                SimpleBid(heart, 80, east),
                SimpleBid(diamond, 100, south),
                Pass(west)
        )
        val bid = Coinche(SimpleBid(), north, surcoinche = false)
        val Returnvalue = isValidBid(allBids, bid)

        assert(!Returnvalue) { "$nameTest FAIL " }
        debugPrintln(dbgLevel.REGULAR, "$nameTest:PASS .......")

    }
    @Test
    fun Bid004() {
        // Scenario :
        // N : PASS
        // E : 80 Heart
        // S : 100 DIAMOND
        // W : PASS
        // N : PASS
        // E : Coinche --> should be valid

        val nameTest = object {}.javaClass.enclosingMethod.name

        val oldTraceLevel = traceLevel
        //traceLevel = dbgLevel.ALL

        val allBids = listOf(
                Pass(north),
                SimpleBid(heart, 80, east),
                SimpleBid(diamond, 100, south),
                Pass(west),
                Pass(north)
        )
        val bid = Coinche(SimpleBid(), east, surcoinche = false)
        val Returnvalue = isValidBid(allBids, bid)

        assert(Returnvalue) { "$nameTest FAIL " }
        debugPrintln(dbgLevel.REGULAR, "$nameTest:PASS .......")

    }
    @Test
    fun Bid005() {
        // Scenario :
        // N : PASS
        // E : 80 Heart
        // S : 100 DIAMOND
        // W : PASS
        // N : PASS
        // E : COINCHE
        // S : PASS
        // W : PASS
        // N : SURCOINCHE -> Valid

        val nameTest = object {}.javaClass.enclosingMethod.name

        val oldTraceLevel = traceLevel
        //traceLevel = dbgLevel.ALL

        val allBids = listOf(
                Pass(north),
                SimpleBid(heart, 80, east),
                SimpleBid(diamond, 100, south),
                Pass(west),
                Pass(north),
                Coinche(SimpleBid(), east, false),
                Pass(south),
                Pass(west)
        )
        val bid = Coinche(SimpleBid(), east, surcoinche = true)
        val Returnvalue = isValidBid(allBids, bid)

        assert(Returnvalue) { "$nameTest FAIL " }
        debugPrintln(dbgLevel.REGULAR, "$nameTest:PASS .......")

    }

    @Test
    fun Bid006() {
        // Scenario :
        // N : PASS
        // E : 80 Heart
        // S : 100 DIAMOND
        // W : PASS
        // N : PASS
        // E : COINCHE
        // S : PASS
        // W : SURCOINCHE -> Invalid

        val nameTest = object {}.javaClass.enclosingMethod.name

        val oldTraceLevel = traceLevel
        //traceLevel = dbgLevel.ALL

        val allBids = listOf(
                Pass(north),
                SimpleBid(heart, 80, east),
                SimpleBid(diamond, 100, south),
                Pass(west),
                Pass(north),
                Coinche(SimpleBid(), east, false),
                Pass(south)
        )
        val bid = Coinche(SimpleBid(), west, surcoinche = true)
        val Returnvalue = isValidBid(allBids, bid)

        assert(!Returnvalue) { "$nameTest FAIL " }
        debugPrintln(dbgLevel.REGULAR, "$nameTest:PASS .......")

    }

    @Test
    fun Bid007() {
        // Scenario :
        // E : PASS
        // S : 80 Heart
        // W : 100 DIAMOND
        // N : PASS
        // E : PASS
        // S : COINCHE
        // W : PASS
        // N : SURCOINCHE -> Invalid
        val nameTest = object {}.javaClass.enclosingMethod.name

        val oldTraceLevel = traceLevel
        //traceLevel = dbgLevel.ALL

        val allBids = listOf(
                Pass(east),
                SimpleBid(heart, 80, south),
                SimpleBid(diamond, 100, west),
                Pass(north),
                Pass(east),
                Coinche(SimpleBid(), south, false),
                Pass(west)
        )
        val bid = Coinche(SimpleBid(), north, surcoinche = true)
        val Returnvalue = isValidBid(allBids, bid)

        assert(!Returnvalue) { "$nameTest FAIL " }
        debugPrintln(dbgLevel.REGULAR, "$nameTest:PASS .......")

    }

    @Test
    fun Bid008() {
        // Scenario :
        // S : PASS
        // W : 80 Heart
        // N : 100 DIAMOND
        // E : PASS
        // S : PASS
        // W : COINCHE
        // N : PASS
        // E : SURCOINCHE -> Invalid


        val nameTest = object {}.javaClass.enclosingMethod.name

        val oldTraceLevel = traceLevel
        //traceLevel = dbgLevel.ALL

        val allBids = listOf(
                Pass(south),
                SimpleBid(heart, 80, west),
                SimpleBid(diamond, 100, north),
                Pass(east),
                Pass(south),
                Coinche(SimpleBid(), west, false),
                Pass(north)
        )
        val bid = Coinche(SimpleBid(), east, surcoinche = true)
        val Returnvalue = isValidBid(allBids, bid)

        assert(!Returnvalue) { "$nameTest FAIL " }
        debugPrintln(dbgLevel.REGULAR, "$nameTest:PASS .......")

    }

    @Test
    fun Bid009() {
        // Scenario :
        // N : 80 Heart
        // E : 100 DIAMOND
        // S : PASS
        // W : PASS
        // N : COINCHE
        // E : PASS
        // S : SURCOINCHE -> Invalid

        val nameTest = object {}.javaClass.enclosingMethod.name

        val oldTraceLevel = traceLevel
        //traceLevel = dbgLevel.ALL

        val allBids = listOf(
                Pass(west),
                SimpleBid(heart, 80, north),
                SimpleBid(diamond, 100, east),
                Pass(south),
                Pass(west),
                Coinche(SimpleBid(), north, false),
                Pass(east)
        )
        val bid = Coinche(SimpleBid(), south, surcoinche = true)
        val Returnvalue = isValidBid(allBids, bid)

        assert(!Returnvalue) { "$nameTest FAIL " }
        debugPrintln(dbgLevel.REGULAR, "$nameTest:PASS .......")

    }

    @Test
    fun Bid010() {
        // Scenario :
        // E : PASS
        // S : 90 Heart
        // W : 100 DIAMOND
        // N : PASS
        // E : PASS
        // S : COINCHE
        // W : PASS
        // N : PASS
        // E : SURCOINCHE -> Valid

        val nameTest = object {}.javaClass.enclosingMethod.name

        val oldTraceLevel = traceLevel
        //traceLevel = dbgLevel.ALL

        val allBids = listOf(
                Pass(east),
                SimpleBid(heart, 90, south),
                SimpleBid(diamond, 100, west),
                Pass(north),
                Pass(east),
                Coinche(SimpleBid(), south, false),
                Pass(west),
                Pass(north)
        )
        val bid = Coinche(SimpleBid(), east, surcoinche = true)
        val Returnvalue = isValidBid(allBids, bid)

        assert(Returnvalue) { "$nameTest FAIL " }
        debugPrintln(dbgLevel.REGULAR, "$nameTest:PASS .......")

    }

    @Test
    fun Bid011() {
        // Scenario :
        // S : 160 Diamond
        // W : 180 Heart
        // N : PASS
        // E : PASS
        // S : CAPOT Heart
        // W : CAPOT BELOTE DIAMOND
        // N : PASS
        // E : PASS
        // S : GENERALE HEART
        // W : PASS
        // N : PASS
        // E : GENERALE BELOTEE
        // S : Coinche
        // W : PASS
        // N : PASS
        // W : Surcoinche
        // All Bids are Valide

        val nameTest = object {}.javaClass.enclosingMethod.name

        val oldTraceLevel = traceLevel
        //traceLevel = dbgLevel.ALL

        val allBids = listOf(
                SimpleBid(diamond, 160, south),
                SimpleBid(heart, 180, west),
                Pass(north),
                Pass(east),
                Capot(heart, south),
                Capot(diamond, west, true),
                Pass(north),
                Pass(east),
                General(diamond, south),
                Pass(west),
                Pass(north),
                General(heart, east, true),
                Coinche(SimpleBid(), south),
                Pass(west),
                Pass(north),
                Coinche(SimpleBid(), west, true)
        )

        val callBids : MutableList<Bid> = mutableListOf()

        for (bid in allBids) {
            debugPrintln(dbgLevel.DEBUG,"$nameTest: callBids = $callBids and we bet : $bid")
            assert(isValidBid(callBids, bid)) { "$nameTest FAIL " }
            callBids.add(bid)
        }

        debugPrintln(dbgLevel.REGULAR, "$nameTest:PASS .......")

    }

    @Test
    fun Bid012() {
        // Scenario :
        // Template for more Bid tests if needed

        val nameTest = object {}.javaClass.enclosingMethod.name

        val oldTraceLevel = traceLevel
        //traceLevel = dbgLevel.ALL


        val allBids : List<Bid> = listOf( )

        val callBids : MutableList<Bid> = mutableListOf()

        for (bid in allBids) {
            debugPrintln(dbgLevel.DEBUG,"$nameTest: callBids = $callBids and we bet : $bid")
            assert(isValidBid(callBids, bid)) { "$nameTest FAIL " }
            callBids.add(bid)
        }

        debugPrintln(dbgLevel.REGULAR, "$nameTest:PASS .......")

    }


}
