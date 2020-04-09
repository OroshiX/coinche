package fr.hornik.coinche

import fr.hornik.coinche.business.*
import fr.hornik.coinche.model.*
import fr.hornik.coinche.model.values.BeloteValue
import fr.hornik.coinche.model.values.CardColor
import fr.hornik.coinche.model.values.CardValue
import fr.hornik.coinche.model.values.PlayerPosition
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CalculusTest {
    private lateinit var bids: List<Bid>

    enum class dbgLevel(val value: Int) {
        NONE(0), DEBUG(1), FUNCTION(2), SCORE(4), HTML(8), HTMLFUNC(10), MISC(16);

        infix fun and(traceLevel: CalculusTest.dbgLevel): Any {
            return traceLevel.value and this.value

        }
        infix fun or(traceLevel: CalculusTest.dbgLevel): Any {
            return traceLevel.value or this.value

        }
        fun toInt():Int {
            return this.value
        }
    }

    var TraceLevel: dbgLevel = dbgLevel.FUNCTION

    @BeforeEach
    fun initBids() {
        DBGprintln(dbgLevel.FUNCTION,"Enter initBids")

        bids = listOf(
                SimpleBid(CardColor.HEART, 80, PlayerPosition.NORTH),
                Pass(PlayerPosition.EAST),
                Capot(CardColor.SPADE, PlayerPosition.NORTH, true),
                General(CardColor.DIAMOND, PlayerPosition.SOUTH, false),
                Coinche(SimpleBid(CardColor.CLUB, 90, PlayerPosition.WEST), PlayerPosition.NORTH, surcoinche = true))
    }

    val deck = listOf(
            Card(CardValue.KING, CardColor.SPADE, null) // TODO ETC.
    )

    @Test
    fun testBid() {
        //testUnit()
        DBGprintln(dbgLevel.FUNCTION,"Enter testBid")

        var EW: MutableList<MutableList<CardPlayed>> = mutableListOf()
        var NS: MutableList<MutableList<CardPlayed>> = mutableListOf()
        var aPair = Pair(EW, NS)

        for (i in 1..2000) {
            DBGprintln(dbgLevel.HTML, "<h2> Partie N $i</h2>\n\n")
            aPair = testPartie(pliNS = NS, pliEW = EW)
            EW = aPair.first
            NS = aPair.second

        }

    }

    fun testUnit() {
        DBGprintln(dbgLevel.FUNCTION,"Enter testUnit")

        for (bid in bids) {
            DBGprintln(dbgLevel.MISC, bid)
        }
    }

    fun DBGprintln(wantedLevel: dbgLevel, Str: Any) {

        if ((wantedLevel and TraceLevel) != 0) {
            println("$wantedLevel : $Str")
        }


    }

    fun testPartie(pliNS: MutableList<MutableList<CardPlayed>>, pliEW: MutableList<MutableList<CardPlayed>>): Pair<MutableList<MutableList<CardPlayed>>, MutableList<MutableList<CardPlayed>>> {
        DBGprintln(dbgLevel.FUNCTION,"Enter testPartie")

        DBGprintln(dbgLevel.HTML, "<table><tr><td>\n")
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
        DBGprintln(dbgLevel.HTML, "<pre>Contrat : $aBid ")


        for (tour in 1..8) {
            if ((tour % 2) == 1) {
                DBGprintln(dbgLevel.HTML, "</pre><tr><td><pre>\n <b>Tour $tour</b>")
            } else {
                DBGprintln(dbgLevel.HTML, "</pre><td><pre>\n<b>Tour $tour</b>")
            }
            val listCardP = mutableListOf<CardPlayed>()
            for (i in 0..3) {
                fourHands[tHand[indexPlayer]].sortWith(Comparator { o1, o2 -> o1.color.compareTo(o2.color) })
                if ((dbgLevel.HTML and TraceLevel) != 0)
                    printHand(fourHands[tHand[indexPlayer]], "Player ${lRand[tHand[indexPlayer]]}")
                val c = allValidCardsToPlay(fourHands[indexPlayer], aBid, listCardP)
                DBGprintln(dbgLevel.HTML, "")

                if ((dbgLevel.HTML and TraceLevel) != 0)
                    printHand(c, "valid cards for ${lRand[indexPlayer]}")
                DBGprintln(dbgLevel.HTML, "<font color=#FF0000 bgcolor=0x00FFFF> ${lRand[indexPlayer]} plays  ${c[0].value}  of ${c[0].color} </font>")
                listCardP.add(CardPlayed(c[0], BeloteValue.NONE, lRand[indexPlayer]))
                fourHands[tHand[indexPlayer]].remove(c[0])
                indexPlayer = (indexPlayer + 1) % 4
            }
            indexPlayer = mapPos[calculateWinnerTrick(listCardP, aBid)]!!
            if (tour == 8) {
                dixDer = lRand[indexPlayer]
            }
            if (indexPlayer % 2 == 0) {
                PlisNS.add(listCardP)
            } else {
                PlisEW.add(listCardP)
            }
        }
        DBGprintln(dbgLevel.HTML, "</pre></tr><tr><td>")
        DBGprintln(dbgLevel.SCORE, "\n\nScore = ${calculateScoreTricks(PlisNS, PlisEW, dixDer, aBid)}")
        DBGprintln(dbgLevel.HTML, "</table>")
        DBGprintln(dbgLevel.HTML, "<pre>")
        DBGprintln(dbgLevel.HTML, "\n\nDisplay Structure of tricks\n")
        var i = 0
        for (li in PlisEW) {
            i = i + 1
            if ((dbgLevel.HTML and TraceLevel) != 0)
                printHand(li.map { it -> it.card }, "EastWest Pli $i")
        }
        for (li in PlisNS) {
            i = i + 1
            if ((dbgLevel.HTML and TraceLevel) != 0)

                printHand(li.map { it -> it.card }, "North South Pli $i")
        }
        DBGprintln(dbgLevel.HTML, "</pre><br><br><br>")
        return (Pair(PlisEW, PlisNS))

    }
}