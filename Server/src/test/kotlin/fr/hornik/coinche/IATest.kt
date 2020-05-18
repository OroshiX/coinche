package fr.hornik.coinche

import fr.hornik.coinche.business.*
import fr.hornik.coinche.model.*
import fr.hornik.coinche.model.values.CardColor
import fr.hornik.coinche.model.values.CardValue
import fr.hornik.coinche.model.values.PlayerPosition
import fr.hornik.coinche.util.dbgLevel
import fr.hornik.coinche.util.debugPrintln
import fr.hornik.coinche.util.traceLevel
import org.junit.jupiter.api.Test

class IATest {
    private val heart = CardColor.HEART
    private val diamond = CardColor.DIAMOND
    private val spade = CardColor.SPADE
    private val club = CardColor.CLUB
    private val north = PlayerPosition.NORTH
    private val south = PlayerPosition.SOUTH
    private val east = PlayerPosition.EAST
    private val west = PlayerPosition.WEST
    private val seven = CardValue.SEVEN
    private val eight = CardValue.EIGHT
    private val nine = CardValue.NINE
    private val ten = CardValue.TEN
    private val jack = CardValue.JACK
    private val queen = CardValue.QUEEN
    private val king = CardValue.KING
    private val ace = CardValue.ACE

    @Test
    fun testEnchere90() {

        val nameTest = object {}.javaClass.enclosingMethod.name

        // North : 100 Spade
        // South : Coinche
        // East plays K Heart
        // South plays 8 Club ( no heart / no spade )
        // West has AS 10S AH 9H 7H JC JD 7D
        // playable should be true for A,9 or 7 H
        val oldTraceLevel = traceLevel
        // traceLevel = dbgLevel.ALL

        val myCards = mutableListOf<Card>()

        myCards.add(Card(ace, spade, playable = null))
        myCards.add(Card(ten, spade, playable = null))
        myCards.add(Card(ace, heart, playable = null))
        myCards.add(Card(nine, heart, playable = null))
        myCards.add(Card(seven, heart, playable = null))
        myCards.add(Card(jack, club, playable = null))
        myCards.add(Card(jack, diamond, playable = null))
        myCards.add(Card(seven, diamond, playable = null))

        printHand(dbgLevel.DEBUG, myCards, "$nameTest:Mycards are ")


        val testBid1 = IARun.enchere(north, listOf(), myCards, 0)
        traceLevel = oldTraceLevel

        assert((testBid1.curPoint() >= 80) && (testBid1.curPoint() <= 120)) { "$nameTest FAIL - $testBid1 is not accurate" }
        debugPrintln(dbgLevel.REGULAR, "$nameTest:PASS - Bid was : $testBid1")


    }


    @Test
    fun testEnchere100() {
        val nameTest = object {}.javaClass.enclosingMethod.name

        val oldTraceLevel = traceLevel
        //traceLevel = dbgLevel.ALL
        // Bid should be at minimum 120 Club


        val myCards = mutableListOf<Card>()


        myCards.add(Card(jack, spade, playable = null))
        myCards.add(Card(nine, spade, playable = null))
        myCards.add(Card(ace, spade, playable = null))
        myCards.add(Card(seven, spade, playable = null))
        myCards.add(Card(ace, heart, playable = null))
        myCards.add(Card(ace, club, playable = null))
        myCards.add(Card(ace, diamond, playable = null))
        myCards.add(Card(ten, diamond, playable = null))

        printHand(dbgLevel.DEBUG, myCards, "$nameTest:Mycards are ")
        val prevBid = Capot(diamond, north, true)
        val listBids = listOf(Pass(east), prevBid, Pass(west))


        val testBid1 = IARun.enchere(north, listBids, myCards, 0)
        traceLevel = oldTraceLevel

        assert(testBid1.curPoint() == 500) { "$nameTest FAIL - $testBid1 is not accurate" }

        debugPrintln(dbgLevel.REGULAR, "$nameTest:PASS - Bid was : $testBid1")

    }

    @Test
    fun testEnchere110() {
        val nameTest = object {}.javaClass.enclosingMethod.name

        // Bid should be at minimum 120 Club
        val oldTraceLevel = traceLevel
        //traceLevel = dbgLevel.ALL

        val myCards = mutableListOf<Card>()

        myCards.add(Card(ace, club, playable = null))
        myCards.add(Card(king, club, playable = null))
        myCards.add(Card(jack, club, playable = null))
        myCards.add(Card(eight, club, playable = null))
        myCards.add(Card(seven, club, playable = null))
        myCards.add(Card(ace, diamond, playable = null))
        myCards.add(Card(ten, diamond, playable = null))
        myCards.add(Card(jack, diamond, playable = null))

        printHand(dbgLevel.DEBUG, myCards, "$nameTest:Mycards are ")


        val prevBid = SimpleBid(diamond, 120, south)
        val listBids = listOf(Pass(east), prevBid, Pass(west))

        val testBid1 = IARun.enchere(north, listBids, myCards, 0)

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
        val myCards = mutableListOf<Card>()

        myCards.add(Card(jack, heart, playable = null))
        myCards.add(Card(nine, heart, playable = null))
        myCards.add(Card(ten, heart, playable = null))
        myCards.add(Card(ace, club, playable = null))
        myCards.add(Card(king, club, playable = null))
        myCards.add(Card(ace, spade, playable = null))
        myCards.add(Card(ten, spade, playable = null))
        myCards.add(Card(ten, diamond, playable = null))

        printHand(dbgLevel.DEBUG, myCards, "$nameTest:Mycards are ")


        val prevBid = SimpleBid(diamond, 80, south)
        val listBids = listOf(Pass(), prevBid, Pass())

        val testBid1 = IARun.enchere(north, listBids, myCards, 0)
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
        val myCards = mutableListOf<Card>()

        myCards.add(Card(jack, heart, playable = null))
        myCards.add(Card(nine, heart, playable = null))
        myCards.add(Card(ace, heart, playable = null))
        myCards.add(Card(queen, heart, playable = null))
        myCards.add(Card(king, heart, playable = null))
        myCards.add(Card(ace, spade, playable = null))
        myCards.add(Card(ace, club, playable = null))
        myCards.add(Card(ace, diamond, playable = null))

        printHand(dbgLevel.DEBUG, myCards, "$nameTest:Mycards are ")


        val prevBid = SimpleBid(diamond, 160, south)
        val listBids = listOf(Pass(), prevBid, Pass())

        val testBid1 = IARun.enchere(north, listBids, myCards, 0)
        traceLevel = oldTraceLevel
        assert(((testBid1.curPoint() >= 520))) { "$nameTest FAIL - $testBid1 is not accurate" }
        debugPrintln(dbgLevel.REGULAR, "$nameTest:PASS - Bid $testBid1")

    }

    @Test
    fun testEnchere150() {
        // Test ou l'on doit prendre l'enchere du partenaire
        val nameTest = object {}.javaClass.enclosingMethod.name

        // Bid should be Generale + Belote
        val oldTraceLevel = traceLevel
        // traceLevel = dbgLevel.ALL
        val myCards = mutableListOf<Card>()

        myCards.add(Card(jack, heart, playable = null))
        myCards.add(Card(nine, heart, playable = null))

        myCards.add(Card(nine, diamond, playable = null))
        myCards.add(Card(seven, diamond, playable = null))
        myCards.add(Card(ace, diamond, playable = null))

        myCards.add(Card(ace, spade, playable = null))

        myCards.add(Card(ten, club, playable = null))
        myCards.add(Card(ace, club, playable = null))

        printHand(dbgLevel.DEBUG, myCards, "$nameTest:Mycards are ")


        val prevBid = SimpleBid(diamond, 140, south)
        val listBids = listOf(Pass(), prevBid, Pass())

        val testBid1 = IARun.enchere(north, listBids, myCards, 0)
        traceLevel = oldTraceLevel
        assert(((testBid1.curPoint() <= 130))) { "$nameTest FAIL - $testBid1 is not accurate" }
        debugPrintln(dbgLevel.REGULAR, "$nameTest:PASS - Bid $testBid1")

    }

    @Test
    fun testEnchere155() {
        // Test ou l'on doit prendre l'enchere du partenaire
        val nameTest = object {}.javaClass.enclosingMethod.name

        // Bid should be Generale + Belote
        val oldTraceLevel = traceLevel
        //traceLevel = dbgLevel.ALL
        val myCards = mutableListOf<Card>()

        myCards.add(Card(jack, heart, playable = null))
        myCards.add(Card(nine, heart, playable = null))

        myCards.add(Card(nine, diamond, playable = null))
        myCards.add(Card(queen, diamond, playable = null))
        myCards.add(Card(ace, diamond, playable = null))

        myCards.add(Card(ace, spade, playable = null))

        myCards.add(Card(ten, club, playable = null))
        myCards.add(Card(ace, club, playable = null))

        printHand(dbgLevel.DEBUG, myCards, "$nameTest:Mycards are ")


        val prevBid = SimpleBid(diamond, 140, south)
        val listBids = listOf(Pass(), prevBid, Pass())

        val testBid1 = IARun.enchere(north, listBids, myCards, 0)
        traceLevel = oldTraceLevel
        assert(((testBid1.curPoint() > 130))) { "$nameTest FAIL - $testBid1 is not accurate" }
        debugPrintln(dbgLevel.REGULAR, "$nameTest:PASS - Bid $testBid1")

    }

    @Test
    fun testEnchere160() {
        // Test ou l'on doit prendre l'enchere du partenaire
        val nameTest = object {}.javaClass.enclosingMethod.name

        val oldTraceLevel = traceLevel
        //traceLevel = dbgLevel.ALL
        val myCards = mutableListOf<Card>()

        myCards.add(Card(jack, heart, playable = null))

        myCards.add(Card(nine, club, playable = null))
        myCards.add(Card(jack, club, playable = null))
        myCards.add(Card(ace, club, playable = null))

        myCards.add(Card(ten, spade, playable = null))
        myCards.add(Card(ace, spade, playable = null))

        myCards.add(Card(ten, diamond, playable = null))
        myCards.add(Card(seven, diamond, playable = null))

        printHand(dbgLevel.DEBUG, myCards, "$nameTest:Mycards are ")


        val prevBid = SimpleBid(heart, 80, east)
        val listBids = listOf(Pass(north), prevBid, Pass(south))

        val testBid1 = IARun.enchere(west, listBids, myCards, 0)
        traceLevel = oldTraceLevel
        assert(((testBid1.curPoint() >= 130) && testBid1.curColor() == heart)) { "$nameTest FAIL - $testBid1 is not accurate" }
        debugPrintln(dbgLevel.REGULAR, "$nameTest:PASS - Bid $testBid1")

    }

    @Test
    fun testEnchere170() {
        // Test ou l'on doit prendre l'enchere du partenaire
        val nameTest = object {}.javaClass.enclosingMethod.name

        val oldTraceLevel = traceLevel
        // traceLevel = dbgLevel.ALL
        val myCards = mutableListOf<Card>()

        myCards.add(Card(ace, heart, playable = null))

        myCards.add(Card(nine, club, playable = null))
        myCards.add(Card(jack, club, playable = null))
        myCards.add(Card(seven, club, playable = null))

        myCards.add(Card(ten, spade, playable = null))
        myCards.add(Card(ace, spade, playable = null))

        myCards.add(Card(ten, diamond, playable = null))
        myCards.add(Card(seven, diamond, playable = null))

        printHand(dbgLevel.DEBUG, myCards, "$nameTest:Mycards are ")


        val prevBid = SimpleBid(club, 80, east)
        val listBids = listOf(Pass(north), prevBid, Pass(south))

        val testBid1 = IARun.enchere(west, listBids, myCards, 0)
        traceLevel = oldTraceLevel
        assert(((testBid1.curPoint() >= 130) && testBid1.curColor() == club)) { "$nameTest FAIL - $testBid1 is not accurate" }
        debugPrintln(dbgLevel.REGULAR, "$nameTest:PASS - Bid $testBid1")

    }

    @Test
    fun testEnchere180() {
        // Test ou l'on doit prendre l'enchere du partenaire
        val nameTest = object {}.javaClass.enclosingMethod.name

        val oldTraceLevel = traceLevel
        //traceLevel = dbgLevel.ALL
        val myCards = mutableListOf<Card>()

        myCards.add(Card(ace, heart, playable = null))
        myCards.add(Card(eight, heart, playable = null))


        myCards.add(Card(ten, spade, playable = null))
        myCards.add(Card(seven, spade, playable = null))
        myCards.add(Card(ace, spade, playable = null))

        myCards.add(Card(ten, diamond, playable = null))
        myCards.add(Card(seven, diamond, playable = null))
        myCards.add(Card(eight, diamond, playable = null))

        printHand(dbgLevel.DEBUG, myCards, "$nameTest:Mycards are ")


        val prevBid = SimpleBid(club, 120, east)
        val listBids = listOf(Pass(north), prevBid, Pass(south))

        val testBid1 = IARun.enchere(west, listBids, myCards, 0)
        traceLevel = oldTraceLevel
        assert(((testBid1.curPoint() == 130) && testBid1.curColor() == club)) { "$nameTest FAIL - $testBid1 is not accurate" }
        debugPrintln(dbgLevel.REGULAR, "$nameTest:PASS - Bid $testBid1")

    }

    @Test
    fun testEnchere190() {
        // Test ou l'on doit prendre l'enchere du partenaire
        val nameTest = object {}.javaClass.enclosingMethod.name

        val oldTraceLevel = traceLevel
        //traceLevel = dbgLevel.ALL
        val myCards = mutableListOf<Card>()

        myCards.add(Card(ace, heart, playable = null))
        myCards.add(Card(eight, heart, playable = null))


        myCards.add(Card(ten, spade, playable = null))
        myCards.add(Card(seven, spade, playable = null))
        myCards.add(Card(ace, spade, playable = null))

        myCards.add(Card(ten, diamond, playable = null))
        myCards.add(Card(seven, diamond, playable = null))

        myCards.add(Card(queen, club, playable = null))

        printHand(dbgLevel.DEBUG, myCards, "$nameTest:Mycards are ")


        val prevBid = SimpleBid(club, 120, east)
        val listBids = listOf(Pass(north), prevBid, Pass(south))

        val testBid1 = IARun.enchere(west, listBids, myCards, 0)
        traceLevel = oldTraceLevel
        assert(((testBid1.curPoint() == 250) && testBid1.curColor() == club)) { "$nameTest FAIL - $testBid1 is not accurate" }
        debugPrintln(dbgLevel.REGULAR, "$nameTest:PASS - Bid $testBid1")

    }

    @Test
    fun testEnchere200() {
        // Test ou l'on doit prendre l'enchere du partenaire
        val nameTest = object {}.javaClass.enclosingMethod.name

        val oldTraceLevel = traceLevel
        //traceLevel = dbgLevel.ALL
        val myCards = mutableListOf<Card>()



        myCards.add(Card(jack, spade, playable = null))
        myCards.add(Card(nine, spade, playable = null))
        myCards.add(Card(ace, spade, playable = null))
        myCards.add(Card(king, spade, playable = null))
        myCards.add(Card(queen, spade, playable = null))

        myCards.add(Card(ace, diamond, playable = null))
        myCards.add(Card(ten, diamond, playable = null))


        printHand(dbgLevel.DEBUG, myCards, "$nameTest:Mycards are ")


        val prevBid = General(club, east)
        val listBids = listOf(Pass(north), prevBid, Pass(south))

        val testBid1 = IARun.enchere(west, listBids, myCards, 0)
        traceLevel = oldTraceLevel
        assert(((testBid1.curPoint() >= 500) && testBid1.curColor() == spade)) { "$nameTest FAIL - $testBid1 is not accurate" }
        debugPrintln(dbgLevel.REGULAR, "$nameTest:PASS - Bid $testBid1")

    }

    @Test
    fun testEnchere210() {
        // le partner coinche 80 Coeur
        // Verifier que malgre notre jeu de general on ne fait pas une enchere irreguliere ( sinon exception dans le timer : pas bon )


        val nameTest = object {}.javaClass.enclosingMethod.name

        val oldTraceLevel = traceLevel
        //traceLevel = dbgLevel.ALL
        val myCards = mutableListOf<Card>()



        myCards.add(Card(jack, spade, playable = null))
        myCards.add(Card(nine, spade, playable = null))
        myCards.add(Card(ace, spade, playable = null))
        myCards.add(Card(king, spade, playable = null))
        myCards.add(Card(queen, spade, playable = null))

        myCards.add(Card(ace, diamond, playable = null))
        myCards.add(Card(ten, diamond, playable = null))


        printHand(dbgLevel.DEBUG, myCards, "$nameTest:Mycards are ")


        val prevBid = SimpleBid(heart, 80, east)
        val partnerBid = Coinche(prevBid, south, false)
        val lastBid = Pass(west)
        val listBids = listOf(prevBid, partnerBid, lastBid)

        val testBid1 = IARun.enchere(north, listBids, myCards, 0)
        traceLevel = oldTraceLevel
        assert(testBid1 is Pass) { "$nameTest FAIL - $testBid1 is not accurate" }
        debugPrintln(dbgLevel.REGULAR, "$nameTest:PASS - Bid $testBid1")

    }

    @Test
    fun testEnchere220() {
        // le partner coinche 80 Coeur
        // Verifier que malgre notre jeu de general on ne fait pas une enchere irreguliere ( sinon exception dans le timer : pas bon )


        val nameTest = object {}.javaClass.enclosingMethod.name

        val oldTraceLevel = traceLevel
        //traceLevel = dbgLevel.ALL
        val myCards = mutableListOf<Card>()



        myCards.add(Card(jack, spade, playable = null))
        myCards.add(Card(nine, spade, playable = null))
        myCards.add(Card(ace, spade, playable = null))
        myCards.add(Card(king, spade, playable = null))
        myCards.add(Card(queen, spade, playable = null))

        myCards.add(Card(ace, diamond, playable = null))
        myCards.add(Card(ten, diamond, playable = null))


        printHand(dbgLevel.DEBUG, myCards, "$nameTest:Mycards are ")


        val prevBid = SimpleBid(heart, 80, east)
        val partnerBid = Coinche(prevBid, south, false)
        val listBids = listOf(prevBid, partnerBid)

        val testBid1 = IARun.enchere(west, listBids, myCards, 0)
        traceLevel = oldTraceLevel
        assert(testBid1 is Pass) { "$nameTest FAIL - $testBid1 is not accurate" }
        debugPrintln(dbgLevel.REGULAR, "$nameTest:PASS - Bid $testBid1")

    }

    @Test
    fun testPlayCard001() {


        val nameTest = object {}.javaClass.enclosingMethod.name

        val oldTraceLevel = traceLevel
        //traceLevel = dbgLevel.ALL

        val aCard = masterColor(heart, heart, allCardsPlayed = listOf())

        debugPrintln(dbgLevel.DEBUG, "masterCard at $heart is $aCard")
        traceLevel = oldTraceLevel
        assert(aCard?.value ?: seven == jack) { "$nameTest FAIL - $aCard is not accurate" }
        debugPrintln(dbgLevel.REGULAR, "$nameTest:PASS - Master is  $aCard")

    }

    @Test
    fun testPlayCard002() {

        val nameTest = object {}.javaClass.enclosingMethod.name

        val oldTraceLevel = traceLevel
        //traceLevel = dbgLevel.ALL

        val aCard = masterColor(heart, diamond, allCardsPlayed = listOf())

        debugPrintln(dbgLevel.DEBUG, "masterCard at $diamond is $aCard")
        traceLevel = oldTraceLevel
        assert(aCard?.value ?: seven == ace) { "$nameTest FAIL - $aCard is not accurate" }
        debugPrintln(dbgLevel.REGULAR, "$nameTest:PASS - Master is  $aCard")

    }

    @Test
    fun testPlayCard003() {

        val nameTest = object {}.javaClass.enclosingMethod.name

        val oldTraceLevel = traceLevel
        //traceLevel = dbgLevel.ALL
        val playedCards = mutableListOf<CardPlayed>()

        playedCards.add(CardPlayed(Card(jack, heart, playable = null)))
        playedCards.add(CardPlayed(Card(ace, diamond, playable = null)))
        playedCards.add(CardPlayed(Card(ace, spade, playable = null)))
        playedCards.add(CardPlayed(Card(ten, spade, playable = null)))
        playedCards.add(CardPlayed(Card(king, spade, playable = null)))
        playedCards.add(CardPlayed(Card(seven, spade, playable = null)))
        playedCards.add(CardPlayed(Card(eight, spade, playable = null)))
        playedCards.add(CardPlayed(Card(ace, club, playable = null)))
        playedCards.add(CardPlayed(Card(ten, club, playable = null)))
        playedCards.add(CardPlayed(Card(king, club, playable = null)))
        playedCards.add(CardPlayed(Card(queen, club, playable = null)))
        playedCards.add(CardPlayed(Card(jack, club, playable = null)))
        playedCards.add(CardPlayed(Card(nine, club, playable = null)))
        playedCards.add(CardPlayed(Card(eight, club, playable = null)))
        playedCards.add(CardPlayed(Card(seven, club, playable = null)))


        val atout = heart
        val color = diamond
        val color2 = spade
        val color3 = club

        val seven = seven
        val aCard = masterColor(color, atout, allCardsPlayed = playedCards)
        val bCard = masterColor(atout, atout, allCardsPlayed = playedCards)
        val cCard = masterColor(color2, atout, allCardsPlayed = playedCards)
        val dCard = masterColor(color3, atout, allCardsPlayed = playedCards)

        debugPrintln(dbgLevel.DEBUG, "masterCard at $color is $aCard")
        debugPrintln(dbgLevel.DEBUG, "masterCard at $atout is $bCard")
        debugPrintln(dbgLevel.DEBUG, "masterCard at $color2 is $cCard")
        debugPrintln(dbgLevel.DEBUG, "masterCard at $color3 is $dCard")
        traceLevel = oldTraceLevel
        assert((aCard?.value ?: seven == ten)
               && (bCard?.value ?: seven == nine)
               && (cCard?.value ?: seven == queen)
               && (dCard == null)) { "$nameTest FAIL - $aCard is not accurate" }
        debugPrintln(dbgLevel.REGULAR, "$nameTest:PASS - Master is  $aCard and $bCard")

    }

    @Test
    fun testPlayCard004() {

        val nameTest = object {}.javaClass.enclosingMethod.name

        val oldTraceLevel = traceLevel
        //traceLevel = dbgLevel.ALL
        val myCards = mutableListOf<Card>()



        myCards.add(Card(queen, spade, playable = null))
        myCards.add(Card(nine, spade, playable = null))


        val playedCards: MutableList<CardPlayed> = mutableListOf()

        playedCards.add(CardPlayed(Card(jack, heart, playable = null)))
        playedCards.add(CardPlayed(Card(ace, spade, playable = null)))
        playedCards.add(CardPlayed(Card(ten, spade, playable = null)))
        playedCards.add(CardPlayed(Card(king, spade, playable = null)))
        playedCards.add(CardPlayed(Card(seven, spade, playable = null)))
        playedCards.add(CardPlayed(Card(eight, spade, playable = null)))
        playedCards.add(CardPlayed(Card(ace, club, playable = null)))
        playedCards.add(CardPlayed(Card(ten, club, playable = null)))
        playedCards.add(CardPlayed(Card(king, club, playable = null)))
        playedCards.add(CardPlayed(Card(queen, club, playable = null)))
        playedCards.add(CardPlayed(Card(jack, club, playable = null)))
        playedCards.add(CardPlayed(Card(nine, club, playable = null)))
        playedCards.add(CardPlayed(Card(eight, club, playable = null)))
        playedCards.add(CardPlayed(Card(seven, club, playable = null)))


        val heart = heart
        val diamond = diamond
        val spade = spade
        val club = club
        val heartnb = totalRemainingNumber(heart, playedCards, myCards)
        val spadenb = totalRemainingNumber(spade, playedCards, myCards)
        val clubnb = totalRemainingNumber(club, playedCards, myCards)
        val diamondnb = totalRemainingNumber(diamond, playedCards, myCards)



        traceLevel = oldTraceLevel
        assert((heartnb == 7) && (diamondnb == 8) && (spadenb == 1) && (clubnb == 0)) { "$nameTest FAIL -  h=$heartnb d=$diamondnb s=$spadenb c=$clubnb is not accurate" }
        debugPrintln(dbgLevel.REGULAR,
                     "$nameTest:PASS - \"$nameTest FAIL -  h=$heartnb d=$diamondnb s=$spadenb c=$clubnb is OK \n")

    }

    @Test
    fun testIAPlay001() {
        val nameTest = object {}.javaClass.enclosingMethod.name

        val oldTraceLevel = traceLevel
        //traceLevel = dbgLevel.ALL
        val myCards = mutableListOf<Card>()

        myCards.add(Card(queen, spade, playable = null))
        myCards.add(Card(nine, spade, playable = null))
        myCards.add(Card(jack, diamond, playable = null))
        val plisEW: MutableMap<Int, List<CardPlayed>> = mutableMapOf()

        val plisNS: MutableMap<Int, List<CardPlayed>> = mutableMapOf()

        var nb = 0

        plisEW[nb] = listOf(
                CardPlayed(Card(jack, club, playable = null)),
                CardPlayed(Card(nine, club, playable = null)),
                CardPlayed(Card(queen, club, playable = null)),
                CardPlayed(Card(eight, club, playable = null))
        )

        nb++
        plisNS[nb] = listOf(
                CardPlayed(Card(ace, heart, playable = null)),
                CardPlayed(Card(ten, heart, playable = null)),
                CardPlayed(Card(king, heart, playable = null)),
                CardPlayed(Card(queen, heart, playable = null))
        )

        nb++
        plisEW[nb] = listOf(
                CardPlayed(Card(jack, heart, playable = null)),
                CardPlayed(Card(nine, heart, playable = null)),
                CardPlayed(Card(eight, heart, playable = null)),
                CardPlayed(Card(seven, heart, playable = null))
        )

        nb++
        plisNS[nb] = listOf(
                CardPlayed(Card(ace, diamond, playable = null)),
                CardPlayed(Card(ten, diamond, playable = null)),
                CardPlayed(Card(queen, diamond, playable = null)),
                CardPlayed(Card(eight, diamond, playable = null))
        )

        nb++
        plisNS[nb] = listOf(
                CardPlayed(Card(ace, club, playable = null)),
                CardPlayed(Card(king, spade, playable = null)),
                CardPlayed(Card(eight, spade, playable = null)),
                CardPlayed(Card(jack, spade, playable = null))
        )


        val prevBid = SimpleBid(heart, 80, east)
        val partnerBid = SimpleBid(club, 90, south)
        val listBids: MutableList<Bid> = mutableListOf(prevBid, partnerBid, Pass(west), Pass(north), Pass(east))
        val onTable: MutableList<CardPlayed> = mutableListOf()
        val card = whatToPlay(north, myCards, listBids, club, onTable, plisNS, plisEW)

        traceLevel = oldTraceLevel
        assert(card != null && card.isSimilar(Card(jack, diamond))) { "$nameTest FAIL $card is not accurate" }
        debugPrintln(dbgLevel.REGULAR,
                     "$nameTest:PASS :$card ")


    }

    @Test
    fun testIAPlay002() {
        val nameTest = object {}.javaClass.enclosingMethod.name

        val oldTraceLevel = traceLevel
        //traceLevel = dbgLevel.ALL
        val myCards = mutableListOf<Card>()

        myCards.add(Card(ace, spade, playable = null))
        myCards.add(Card(nine, spade, playable = null))
        myCards.add(Card(ace, club, playable = null))
        myCards.add(Card(ten, club, playable = null))
        myCards.add(Card(king, club, playable = null))
        myCards.add(Card(seven, club, playable = null))
        myCards.add(Card(jack, club, playable = null))

        val plisEW: MutableMap<Int, List<CardPlayed>> = mutableMapOf()

        val plisNS: MutableMap<Int, List<CardPlayed>> = mutableMapOf()

        var nb = -1
        nb++

        plisNS[nb] = listOf(
                CardPlayed(Card(jack, spade, playable = null), position = north),
                CardPlayed(Card(ten, spade, playable = null), position = east),
                CardPlayed(Card(queen, spade, playable = null), position = south),
                CardPlayed(Card(eight, spade, playable = null), position = west)
        )


        val partnerBid = SimpleBid(spade, 80, south)
        val listBids: MutableList<Bid> = mutableListOf(partnerBid, Pass(west), Pass(north), Pass(east))
        val onTable: MutableList<CardPlayed> = mutableListOf()
        val card = whatToPlay(north, myCards, listBids, spade, onTable, plisNS, plisEW)

        traceLevel = oldTraceLevel
        assert(card != null && card.isSimilar(Card(ace, spade))) { "$nameTest FAIL $card is not accurate" }
        debugPrintln(dbgLevel.REGULAR,
                     "$nameTest:PASS :$card ")


    }

    @Test
    fun testIAPlay003() {
        val nameTest = object {}.javaClass.enclosingMethod.name

        val oldTraceLevel = traceLevel
        //traceLevel = dbgLevel.ALL
        val myCards = mutableListOf<Card>()

        myCards.add(Card(ace, diamond, playable = null))
        myCards.add(Card(ace, spade, playable = null))
        myCards.add(Card(jack, spade, playable = null))

        myCards.add(Card(ace, club, playable = null))
        myCards.add(Card(ten, club, playable = null))
        myCards.add(Card(king, club, playable = null))
        myCards.add(Card(seven, club, playable = null))
        myCards.add(Card(jack, club, playable = null))

        val plisEW: MutableMap<Int, List<CardPlayed>> = mutableMapOf()

        val plisNS: MutableMap<Int, List<CardPlayed>> = mutableMapOf()


        val partnerBid = SimpleBid(spade, 90, south)
        val listBids: MutableList<Bid> = mutableListOf(partnerBid, Pass(west), Pass(north), Pass(east))
        val onTable: MutableList<CardPlayed> = mutableListOf()
        val card = whatToPlay(north, myCards, listBids, spade, onTable, plisNS, plisEW)

        traceLevel = oldTraceLevel
        assert(card != null && card.isSimilar(Card(jack, spade))) { "$nameTest FAIL $card is not accurate" }
        debugPrintln(dbgLevel.REGULAR,
                     "$nameTest:PASS :$card ")


    }

    @Test
    fun testIAPlay004() {
        val nameTest = object {}.javaClass.enclosingMethod.name

        val oldTraceLevel = traceLevel
        //traceLevel = dbgLevel.ALL
        val myCards = mutableListOf<Card>()

        myCards.add(Card(ace, diamond, playable = null))
        myCards.add(Card(ace, spade, playable = null))
        myCards.add(Card(jack, spade, playable = null))

        myCards.add(Card(ace, club, playable = null))
        myCards.add(Card(ten, club, playable = null))
        myCards.add(Card(king, club, playable = null))
        myCards.add(Card(seven, club, playable = null))
        myCards.add(Card(jack, club, playable = null))

        val plisEW: MutableMap<Int, List<CardPlayed>> = mutableMapOf()

        val plisNS: MutableMap<Int, List<CardPlayed>> = mutableMapOf()


        val partnerBid = SimpleBid(spade, 90, south)
        val listBids: MutableList<Bid> = mutableListOf(partnerBid, Pass(west), Pass(north), Pass(east))
        val onTable: MutableList<CardPlayed> = mutableListOf()
        val card = whatToPlay(north, myCards, listBids, spade, onTable, plisNS, plisEW)

        traceLevel = oldTraceLevel
        assert(card != null && card.isSimilar(Card(jack, spade))) { "$nameTest FAIL $card is not accurate" }
        debugPrintln(dbgLevel.REGULAR,
                     "$nameTest:PASS :$card ")


    }

    @Test
    fun testIAPlay005() {
        val nameTest = object {}.javaClass.enclosingMethod.name

        val oldTraceLevel = traceLevel
        // traceLevel = dbgLevel.ALL
        val myCards = mutableListOf<Card>()

        myCards.add(Card(ten, club, playable = null))
        myCards.add(Card(ten, heart, playable = null))

        myCards.add(Card(ten, diamond, playable = null))
        myCards.add(Card(queen, diamond, playable = null))


        val plisEW: MutableMap<Int, List<CardPlayed>> = mutableMapOf()

        val plisNS: MutableMap<Int, List<CardPlayed>> = mutableMapOf()

        var nb = -1
        nb++

        plisNS[nb] = listOf(
                CardPlayed(Card(ace, heart, playable = null), position = east),
                CardPlayed(Card(queen, spade, playable = null), position = south),
                CardPlayed(Card(king, heart, playable = null), position = west),
                CardPlayed(Card(nine, heart, playable = null), position = north)
        )


        nb++

        plisNS[nb] = listOf(
                CardPlayed(Card(jack, spade, playable = null), position = south),
                CardPlayed(Card(ace, spade, playable = null), position = west),
                CardPlayed(Card(ten, spade, playable = null), position = north),
                CardPlayed(Card(nine, spade, playable = null), position = east)
        )

        nb++
        plisNS[nb] = listOf(

                CardPlayed(Card(eight, spade, playable = null), position = south),
                CardPlayed(Card(seven, heart, playable = null), position = west),
                CardPlayed(Card(seven, spade, playable = null), position = north),
                CardPlayed(Card(seven, club, playable = null), position = east)
        )
        nb++
        plisNS[nb] = listOf(
                CardPlayed(Card(eight, club, playable = null), position = south),
                CardPlayed(Card(jack, club, playable = null), position = west),
                CardPlayed(Card(ace, club, playable = null), position = north),
                CardPlayed(Card(nine, club, playable = null), position = east)
        )
        nb++
        plisNS[nb] = listOf(
                CardPlayed(Card(king, club, playable = null), position = north),
                CardPlayed(Card(eight, heart, playable = null), position = east),
                CardPlayed(Card(eight, diamond, playable = null), position = south),
                CardPlayed(Card(nine, diamond, playable = null), position = west)
        )


        val partnerBid = SimpleBid(spade, 100, south)
        val myBid = SimpleBid(spade, 130, north)
        val listBids: MutableList<Bid> =
                mutableListOf(partnerBid, Pass(west), myBid, Pass(east), Pass(south), Pass(west))
        val onTable: MutableList<CardPlayed> = mutableListOf()
        val card = whatToPlay(north, myCards, listBids, spade, onTable, plisNS, plisEW)

        traceLevel = oldTraceLevel
        assert(card != null && card.isSimilar(Card(ten, club))) { "$nameTest FAIL $card is not accurate" }
        debugPrintln(dbgLevel.REGULAR,
                     "$nameTest:PASS :$card ")


    }


    /*
   Atout : S
   Moi : s
   Mon jeu : [ 7 S , 1 H , 12 H , 13 D , 1 D ]
   Pli 1 : [ n 11 S , e 8 S , s 12 S , w 7 C ]
   Pli 2 : [ n 9 C , e 1 C , s 7 C , w 10 C ]
   Pli 3 : [ e 1 D , s 8 D , w 9 H , n 7 D ]
   Table : [ e 13 H ]
   Test : w, R1C + R2C
   Resultat : w n’a pas de carreau

   */

    @Test
    fun testIAPlaynoDiamondForWest() {
        val nameTest = object {}.javaClass.enclosingMethod.name
        val oldTraceLevel = traceLevel
        val plisEW: MutableMap<Int, List<CardPlayed>> = mutableMapOf()
        val plisNS: MutableMap<Int, List<CardPlayed>> = mutableMapOf()
        var nb = 0
        val atout = spade
        val myPosition = south
        val myCards = listOf(Card(seven, spade), Card(ace, heart), Card(queen, heart), Card(king, diamond),
                             Card(ace, diamond))
        plisNS[nb++] =
                listOf(CardPlayed(Card(jack, spade), position = north), CardPlayed(Card(eight, spade), position = east),
                       CardPlayed(Card(queen, spade), position = south), CardPlayed(Card(seven, club), position = west))
        plisNS[nb++] =
                listOf(CardPlayed(Card(nine, club), position = north), CardPlayed(Card(ace, club), position = east),
                       CardPlayed(Card(seven, club), position = south), CardPlayed(Card(ten, club), position = west))
        plisNS[nb++] = listOf(CardPlayed(Card(ace, diamond), position = east),
                              CardPlayed(Card(eight, diamond), position = south),
                              CardPlayed(Card(nine, heart), position = west),
                              CardPlayed(Card(seven, diamond), position = north))
        val onTable = listOf(CardPlayed(Card(king, heart), position = east))
        val result = playersHaveColor(diamond, atout, plisNS.toList().sortedBy { it.first }.map { e -> e.second }, myPosition,
                                      myCards)
/*Resultat : west n’a pas de carreau*/
/* you need to check result here */
        assert(result[west] == false) { "$nameTest FAIL $result is not accurate West should not have diamond" }
        debugPrintln(dbgLevel.REGULAR,
                     "$nameTest:PASS :$result - no diamond for West ")

    }


    /*
  Name : noClubNoHeart
  Atout : H
  Moi : n
  Mon jeu : [ 7 H , 13 H , 10 H , 11 C , 8 C ]
  Pli 1 : [ n 11 H , e 1 H , s 12 H , w 8 H ]
  Pli 2 : [ n 9 H , e 7 D , s 7 C , w 9 C ]
  Pli 3 : [ n 1 C , e 10 C , s 13 C , w 12 C ]
  Table : [  ]
  Test : playColor
  Resultat : e , w and s have no C

  */

    @Test
    fun testnoClubNoHeart() {
        val nameTest = object {}.javaClass.enclosingMethod.name
        val oldTraceLevel = traceLevel
        val plisEW: MutableMap<Int, List<CardPlayed>> = mutableMapOf()
        val plisNS: MutableMap<Int, List<CardPlayed>> = mutableMapOf()
        var nb = 0
        val atout = heart
        val myPosition = north
        val myCards =
                listOf(Card(seven, heart), Card(king, heart), Card(ten, heart), Card(jack, club), Card(eight, club))
        plisNS[nb++] =
                listOf(CardPlayed(Card(jack, heart), position = north), CardPlayed(Card(ace, heart), position = east),
                       CardPlayed(Card(queen, heart), position = south),
                       CardPlayed(Card(eight, heart), position = west))
        plisNS[nb++] = listOf(CardPlayed(Card(nine, heart), position = north),
                              CardPlayed(Card(seven, diamond), position = east),
                              CardPlayed(Card(seven, club), position = south),
                              CardPlayed(Card(nine, club), position = west))
        plisNS[nb++] =
                listOf(CardPlayed(Card(ace, club), position = north), CardPlayed(Card(ten, club), position = east),
                       CardPlayed(Card(king, club), position = south), CardPlayed(Card(queen, club), position = west))
        val onTable: List<CardPlayed> = listOf()
        var result =
                playersHaveColor(heart, atout, plisNS.toList().sortedBy { it.first }.map { e -> e.second }, myPosition,
                                 myCards)

        /* Resultat : east , west and south have no club */
        var resultB =
                (result[south] == result[west]) and (result[east] == result[west]) and (result[west] == false) and (result[north] == true)
        result =
                playersHaveColor(club, atout, plisNS.toList().sortedBy { it.first }.map { e -> e.second }, myPosition,
                                 myCards)

        resultB =
                resultB and (result[south] == result[west]) and (result[east] == result[west]) and (result[west] == false) and (result[north] == true)

        result =
                playersHaveColor(spade, atout, plisNS.toList().sortedBy { it.first }.map { e -> e.second }, myPosition,
                                 myCards)

        resultB =
                resultB and (result[south] == result[west]) and (result[east] == result[west]) and (result[west] == true) and (result[north] == false)

        /* you need to check result here */
        assert(resultB) { "all players but north should have no more heart and no more club" }
        debugPrintln(dbgLevel.REGULAR,
                     "$nameTest:PASS :$resultB ")


    }




    /*
Name : testRule3
Atout : C
Moi : s
Mon jeu : [ 7 H , 13 H , 10 H , 12 C , 13 C , 9 D ]
Pli 1 : [ n 11 C , e 9 C , s 10 S , w 8 C ]
Pli 2 : [ n 1 S , e 13 S , s 11 S , w 9 S ]
Table : [ n 1 D ]
Test : playColor
Resultat : e has not any more S

*/

    @Test
    fun testRule3() {
        val nameTest = object {}.javaClass.enclosingMethod.name
        val oldTraceLevel = traceLevel
        val plisEW: MutableMap<Int, List<CardPlayed>> = mutableMapOf()
        val plisNS: MutableMap<Int, List<CardPlayed>> = mutableMapOf()
        var nb=0
        val atout=club
        val myPosition=west
        val myCards = listOf( Card( seven , heart ) , Card( king , heart ) , Card( ten , heart ) , Card( queen , club ) , Card( king , club ) , Card( nine , diamond ) )
        plisNS[nb++] = listOf(CardPlayed(Card( jack , club ) , position= north  ) ,CardPlayed(Card( nine , club ) , position= east  ) ,CardPlayed(Card( ten , spade ) , position= south  ) ,CardPlayed(Card( eight , club ) , position= west  ) )
        plisNS[nb++] = listOf(CardPlayed(Card( ace , spade ) , position= north  ) ,CardPlayed(Card( king , spade ) , position= east  ) ,CardPlayed(Card( jack , spade ) , position= south  ) ,CardPlayed(Card( nine , spade ) , position= west  ) )
        val onTable :List<CardPlayed> = listOf(CardPlayed(Card( ace , diamond ) , position= north  ) )
        val result =                 playersHaveColor(spade, atout, plisNS.toList().sortedBy { it.first }.map { e -> e.second }, myPosition,
                                                      myCards)
/*Resultat : east has not any more Spade */
/* you need to check result here */
        assert((result.size == 4) and !result[east]!! and result[north]!! and result[south]!! and !result[west]!!) { "good result is west and east have no more Spade but we have $result " }
        debugPrintln(dbgLevel.REGULAR,
                     "$nameTest:PASS  for Spade :$result ")
    }

/*
Name : Rule5
Atout : C
Moi : w
Mon jeu : [ 7 H , 13 H , 10 H , 12 C , 13 C , 9 D , 9 S ]
Pli 1 : [ w 1 S , n 8 C , e 10 S , s 13 S ]
Table : [ n 1 D ]
Test : ____playColor ____
Resultat : e has not any more S

*/

    @Test
    fun testRule5() {
        val nameTest = object {}.javaClass.enclosingMethod.name
        val oldTraceLevel = traceLevel
        val plisEW: MutableMap<Int, List<CardPlayed>> = mutableMapOf()
        val plisNS: MutableMap<Int, List<CardPlayed>> = mutableMapOf()
        var nb=0
        val atout=club
        val myPosition=west
        val myCards = listOf( Card( seven , heart ) , Card( king , heart ) , Card( ten , heart ) , Card( queen , club ) , Card( king , club ) , Card( nine , diamond ) , Card( nine , spade ) )
        plisNS[nb++] = listOf(CardPlayed(Card( ace , spade ) , position= west  ) ,CardPlayed(Card( eight , club ) , position= north  ) ,CardPlayed(Card( ten , spade ) , position= east  ) ,CardPlayed(Card( king , spade ) , position= south  ) )
        val onTable :List<CardPlayed> = listOf(CardPlayed(Card( ace , diamond ) , position= north  ) )
        val result =                 playersHaveColor(spade, atout, plisNS.toList().sortedBy { it.first }.map { e -> e.second }, myPosition,
                                                                           myCards)
/*Resultat : east has not any more spade */
/* you need to check result here */
        assert((result.size == 4) && !result[east]!! && result[west]!! && !result[north]!! && result[south]!!) { "$nameTest FAIL $result is not accurate" }
        debugPrintln(dbgLevel.REGULAR,
                     "$nameTest:PASS  for Spade :$result ")
    }




    /*
Name : Rule3b
Atout : C
Moi : n
Mon jeu : [ 7 D , 13 H , 8 H , 8 D , 9 C , 11 H , 11 S ]
Pli 1 : [ s 13 S , w 7 S , n 1 S , s 10 E ]
Table : [ n 11 C , e 8 C ]
Test : ____playColor ____
Resultat : e has not any more S

*/

    @Test
    fun testRule3b() {
        val nameTest = object {}.javaClass.enclosingMethod.name
        val oldTraceLevel = traceLevel
        val plisEW: MutableMap<Int, List<CardPlayed>> = mutableMapOf()
        val plisNS: MutableMap<Int, List<CardPlayed>> = mutableMapOf()
        var nb=0
        val atout=club
        val myPosition=south
        val myCards = listOf( Card( seven , diamond ) , Card( king , heart ) , Card( eight , heart ) , Card( eight , diamond ) , Card( nine , club ) , Card( jack , heart ) , Card( jack , spade ) )
        plisNS[nb++] = listOf(CardPlayed(Card( king , spade ) , position= south  ) ,CardPlayed(Card( seven , spade ) , position= west  ) ,CardPlayed(Card( ace , spade ) , position= north  ) ,CardPlayed(Card( ten , spade ) , position= east  ) )
        val onTable :List<CardPlayed> = listOf(CardPlayed(Card( jack , club ) , position= north  ) ,CardPlayed(Card( eight , club ) , position= east  ) )
        val result =                 playersHaveColor(spade, atout, plisNS.toList().sortedBy { it.first }.map { e -> e.second }, myPosition,
                                                      myCards)
/*Resultat : east has not any more spade */
/* you need to check result here */
        assert((result.size == 4) && !result[east]!! && result[west]!! && result[north]!! && result[south]!!) { "$nameTest FAIL $result is not accurate" }
        debugPrintln(dbgLevel.REGULAR,
                     "$nameTest:PASS  for Spade :$result ")    }

}

