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


    fun validateHand(cardsInHand: MutableList<Card>,
                     bid: Bid,
                     onTable: List<CardPlayed>) {
        val valid = allValidCardsToPlay(cardsInHand, bid, onTable)

        // still cards to play, need to set playable to the right value for next player
        for (mcard in cardsInHand) {
            mcard.playable = valid.contains(mcard)
        }
    }

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
        validateHand(myCards, bid = listBids.last { (it is SimpleBid) || (it is General) || (it is Capot) },
                     onTable = onTable)
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
        validateHand(myCards, bid = listBids.last { (it is SimpleBid) || (it is General) || (it is Capot) },
                     onTable = onTable)

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
        validateHand(myCards, bid = listBids.last { (it is SimpleBid) || (it is General) || (it is Capot) },
                     onTable = onTable)

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
        validateHand(myCards, bid = listBids.last { (it is SimpleBid) || (it is General) || (it is Capot) },
                     onTable = onTable)

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
        validateHand(myCards, bid = listBids.last { (it is SimpleBid) || (it is General) || (it is Capot) },
                     onTable = onTable)

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
        val bid = SimpleBid(atout, 80)

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
        val result = playersHaveColor(diamond, atout, bid,
                                      plisNS.toList().sortedBy { it.first }.map { e -> e.second },
                                      myPosition,
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
        val bid = SimpleBid(atout, 80)

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
                playersHaveColor(heart, atout, bid, plisNS.toList().sortedBy { it.first }.map { e -> e.second },
                                 myPosition,
                                 myCards)

        /* Resultat : east , west and south have no club */
        var resultB =
                (result[south] == result[west]) and (result[east] == result[west]) and (result[west] == false) and (result[north] == true)
        result =
                playersHaveColor(club, atout, bid, plisNS.toList().sortedBy { it.first }.map { e -> e.second },
                                 myPosition,
                                 myCards)

        resultB =
                resultB and (result[south] == result[west]) and (result[east] == result[west]) and (result[west] == false) and (result[north] == true)

        result =
                playersHaveColor(spade, atout, bid, plisNS.toList().sortedBy { it.first }.map { e -> e.second },
                                 myPosition,
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
        var nb = 0
        val atout = club
        val bid = SimpleBid(atout, 80)

        val myPosition = west
        val myCards =
                listOf(Card(seven, heart), Card(king, heart), Card(ten, heart), Card(queen, club), Card(king, club),
                       Card(nine, diamond))
        plisNS[nb++] =
                listOf(CardPlayed(Card(jack, club), position = north), CardPlayed(Card(nine, club), position = east),
                       CardPlayed(Card(ten, spade), position = south), CardPlayed(Card(eight, club), position = west))
        plisNS[nb++] =
                listOf(CardPlayed(Card(ace, spade), position = north), CardPlayed(Card(king, spade), position = east),
                       CardPlayed(Card(jack, spade), position = south), CardPlayed(Card(nine, spade), position = west))
        val onTable: List<CardPlayed> = listOf(CardPlayed(Card(ace, diamond), position = north))
        val result =
                playersHaveColor(spade, atout, bid, plisNS.toList().sortedBy { it.first }.map { e -> e.second },
                                 myPosition,
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
        var nb = 0
        val atout = club
        val bid = SimpleBid(atout, 80)

        val myPosition = west
        val myCards =
                listOf(Card(seven, heart), Card(king, heart), Card(ten, heart), Card(queen, club), Card(king, club),
                       Card(nine, diamond), Card(nine, spade))
        plisNS[nb++] =
                listOf(CardPlayed(Card(ace, spade), position = west), CardPlayed(Card(eight, club), position = north),
                       CardPlayed(Card(ten, spade), position = east), CardPlayed(Card(king, spade), position = south))
        val onTable: List<CardPlayed> = listOf(CardPlayed(Card(ace, diamond), position = north))
        val result =
                playersHaveColor(spade, atout, bid, plisNS.toList().sortedBy { it.first }.map { e -> e.second },
                                 myPosition,
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
        var nb = 0
        val atout = club
        val bid = SimpleBid(atout, 80)

        val myPosition = south
        val myCards = listOf(Card(seven, diamond), Card(king, heart), Card(eight, heart), Card(eight, diamond),
                             Card(nine, club), Card(jack, heart), Card(jack, spade))
        plisNS[nb++] =
                listOf(CardPlayed(Card(king, spade), position = south), CardPlayed(Card(seven, spade), position = west),
                       CardPlayed(Card(ace, spade), position = north), CardPlayed(Card(ten, spade), position = east))
        val onTable: List<CardPlayed> =
                listOf(CardPlayed(Card(jack, club), position = north), CardPlayed(Card(eight, club), position = east))
        val result =
                playersHaveColor(spade, atout, bid, plisNS.toList().sortedBy { it.first }.map { e -> e.second },
                                 myPosition,
                                 myCards)
/*Resultat : east has not any more spade */
/* you need to check result here */
        assert((result.size == 4) && !result[east]!! && result[west]!! && result[north]!! && result[south]!!) { "$nameTest FAIL $result is not accurate" }
        debugPrintln(dbgLevel.REGULAR,
                     "$nameTest:PASS  for Spade :$result ")
    }


    /*
Name : defausseDiamondAce
Atout : C
Bid : C 100 w
Moi : e
Mon jeu : [ 1 D , 10 H , 13 H , 7 H , 9 H , 8 H ]
Pli 1 : [ w 11 C , n 9 C , e 1 C , s 10 C ]
Pli 2 : [ w 1 S , n 10 S , e 1 H , s 13 S ]
Table : [ w 13 C , n 7 D ]
Test : e , whatToPlay
Resultat : 1 of D should be played since we bid 100 S

*/

    @Test
    fun testdefausseDiamondAce() {
        val nameTest = object {}.javaClass.enclosingMethod.name
        val oldTraceLevel = traceLevel
        val plisEW: MutableMap<Int, List<CardPlayed>> = mutableMapOf()
        val plisNS: MutableMap<Int, List<CardPlayed>> = mutableMapOf()
        var nb = 0

        val atout = club
        val bid = SimpleBid(club, 100, west)
        val listBids: MutableList<Bid> = mutableListOf(bid, Pass(west), Pass(north), Pass(east), Pass(south))
        val myPosition = east
        val myCards = mutableListOf(Card(ace, diamond), Card(ten, heart), Card(king, heart), Card(seven, heart),
                                    Card(nine, heart), Card(eight, heart))
        plisEW[nb++] =
                listOf(CardPlayed(Card(jack, club), position = west), CardPlayed(Card(nine, club), position = north),
                       CardPlayed(Card(ace, club), position = east), CardPlayed(Card(ten, club), position = south))
        plisEW[nb++] =
                listOf(CardPlayed(Card(ace, spade), position = west), CardPlayed(Card(ten, spade), position = north),
                       CardPlayed(Card(ace, heart), position = east), CardPlayed(Card(king, spade), position = south))
        val onTable: List<CardPlayed> = listOf(CardPlayed(Card(king, club), position = west),
                                               CardPlayed(Card(seven, diamond), position = north))
        validateHand(myCards, bid = listBids.last { (it is SimpleBid) || (it is General) || (it is Capot) },
                     onTable = onTable)
        /*Resultat : ace of diamond should be played since we bid Card( 100 , spade ) */
        val result = whatToPlay(myPosition, myCards, listBids, atout, onTable.toMutableList(), plisNS, plisEW)

        /* you need to check result here */

        traceLevel = oldTraceLevel
        assert(result != null && result.color == diamond && result.value == ace) { "$nameTest FAIL $result is not accurate" }
        debugPrintln(dbgLevel.REGULAR, "$nameTest:PASS  we play  :$result ")

    }


    /*
Name : playKingNotQueenHeart
Atout : C
Bid : C 90 w
Moi : e
Mon jeu : [ 12 H , 13 H , 9 D , 8 D , 8 C , 11 S , 9 S , 7 S ]
Pli 1 : [  ]
Table : [ s 7 H , w 9 H , n 8 H ]
Test : e , whatToPlay
Resultat : 12 of H should be played and not 13 of H

*/

    @Test
    fun testplayKingNotQueenHeart() {
        val nameTest = object {}.javaClass.enclosingMethod.name
        val oldTraceLevel = traceLevel
        traceLevel = dbgLevel.ALL

        val plisEW: MutableMap<Int, List<CardPlayed>> = mutableMapOf()
        val plisNS: MutableMap<Int, List<CardPlayed>> = mutableMapOf()
        var nb = 0

        val atout = club
        val bid = SimpleBid(club, 90, west)
        val listBids: MutableList<Bid> = mutableListOf(bid, Pass(west), Pass(north), Pass(east), Pass(south))
        val myPosition = east
        val myCards = mutableListOf(Card(queen, heart), Card(king, heart), Card(nine, diamond), Card(eight, diamond),
                                    Card(eight, club), Card(jack, spade), Card(nine, spade), Card(seven, spade))
        val onTable: List<CardPlayed> =
                listOf(CardPlayed(Card(seven, heart), position = south), CardPlayed(Card(nine, heart), position = west),
                       CardPlayed(Card(eight, heart), position = north))
        validateHand(myCards, bid = listBids.last { (it is SimpleBid) || (it is General) || (it is Capot) },
                     onTable = onTable)
        /*Resultat : queen of heart should be played and not king of heart */
        val result = whatToPlay(myPosition, myCards, listBids, atout, onTable.toMutableList(), plisNS, plisEW)

        /* you need to check result here */

        traceLevel = oldTraceLevel
        assert(result != null && result.color == heart && result.value == king) { "$nameTest FAIL $result is not accurate" }
        debugPrintln(dbgLevel.REGULAR, "$nameTest:PASS  we play  :$result ")

    }


    /*
Name : playKingTenNotQueenClub
Atout : C
Bid : C 90 w
Moi : e
Mon jeu : [ 12 C , 13 C , 10 C , 9 C , 8 H , 11 S , 9 S , 7 S ]
Pli 1 : [  ]
Table : [ s 7 C , w 8 C , n 7 S ]
Test : e , whatToPlay
Resultat : 12 of C or 10 of C should be played and not 12 of C

*/

    @Test
    fun testplayTenNotQueenClub() {
        val nameTest = object {}.javaClass.enclosingMethod.name
        val oldTraceLevel = traceLevel
        val plisEW: MutableMap<Int, List<CardPlayed>> = mutableMapOf()
        val plisNS: MutableMap<Int, List<CardPlayed>> = mutableMapOf()
        var nb = 0

        val atout = club
        val bid = SimpleBid(club, 90, west)
        val listBids: MutableList<Bid> = mutableListOf(bid, Pass(west), Pass(north), Pass(east), Pass(south))
        val myPosition = east
        val myCards = mutableListOf(Card(queen, club), Card(king, club), Card(ten, club), Card(nine, club),
                                    Card(eight, heart), Card(jack, spade), Card(nine, spade), Card(seven, spade))
        val onTable: List<CardPlayed> =
                listOf(CardPlayed(Card(seven, club), position = south), CardPlayed(Card(eight, club), position = west),
                       CardPlayed(Card(seven, spade), position = north))
        validateHand(myCards, bid = listBids.last { (it is SimpleBid) || (it is General) || (it is Capot) },
                     onTable = onTable)
        /* Resultat : ten of club should be played and not queen of club */
        val result = whatToPlay(myPosition, myCards, listBids, atout, onTable.toMutableList(), plisNS, plisEW)

        /* you need to check result here */

        traceLevel = oldTraceLevel
        assert(result != null && result.color == club && result.value == ten) { "$nameTest FAIL $result is not accurate" }
        debugPrintln(dbgLevel.REGULAR, "$nameTest:PASS  we play  :$result ")

    }


    /*
Name : playDefausseAceofSpade
Atout : C
Bid : C 90 w
Moi : e
Mon jeu : [ 1 D , 10 H , 1 H , 1 S , 10 S , 13 S , 9 S ]
Table : [ w 11 C , n 12 C ]
Test : e , whatToPlay
Resultat : 1 of S should be played

*/

    @Test
    fun testplayDefausseAceofSpade() {
        val nameTest = object {}.javaClass.enclosingMethod.name
        val oldTraceLevel = traceLevel
        val plisEW: MutableMap<Int, List<CardPlayed>> = mutableMapOf()
        val plisNS: MutableMap<Int, List<CardPlayed>> = mutableMapOf()
        var nb = 0

        val atout = club
        val bid = SimpleBid(club, 90, west)
        val listBids: MutableList<Bid> = mutableListOf(bid, Pass(west), Pass(north), Pass(east), Pass(south))
        val myPosition = east
        val myCards = mutableListOf(Card(ace, diamond), Card(ten, heart), Card(ace, heart), Card(ace, spade),
                                    Card(ten, spade), Card(king, spade), Card(nine, spade))
        val onTable: List<CardPlayed> =
                listOf(CardPlayed(Card(jack, club), position = west), CardPlayed(Card(queen, club), position = north))
        validateHand(myCards, bid = listBids.last { (it is SimpleBid) || (it is General) || (it is Capot) },
                     onTable = onTable)
        /* Resultat : ace of spade should be played  */
        val result = whatToPlay(myPosition, myCards, listBids, atout, onTable.toMutableList(), plisNS, plisEW)

        /* you need to check result here */

        traceLevel = oldTraceLevel
        assert(result != null && result.value == ace && result.color == spade) { "$nameTest FAIL $result is not accurate" }
        debugPrintln(dbgLevel.REGULAR, "$nameTest:PASS  we play  :$result ")

    }


    /*
 Name : annonce100Club
 Atout : C
 Bid : S 80 n
 Moi : e
 Mon jeu : [ 7 H , 13 H , 11 C , 7 C , 8 C , 12 C , 10 C , 11 D ]
 Table : [ n 7 C ]
 Test : e , whatToAnnonce
 Resultat : e should annonce 100 C

 */

    @Test
    fun testannonce100Club() {
        val nameTest = object {}.javaClass.enclosingMethod.name
        val oldTraceLevel = traceLevel
        val plisEW: MutableMap<Int, List<CardPlayed>> = mutableMapOf()
        val plisNS: MutableMap<Int, List<CardPlayed>> = mutableMapOf()
        var nb = 0

        val atout = club
        val bid = SimpleBid(spade, 80, north)
        val listBids: MutableList<Bid> = mutableListOf(bid, Pass(west), Pass(north), Pass(east), Pass(south))
        val myPosition = east
        val myCards = mutableListOf(Card(seven, heart), Card(king, heart), Card(jack, club), Card(seven, club),
                                    Card(eight, club), Card(queen, club), Card(ten, club), Card(jack, diamond))
        val onTable: List<CardPlayed> = listOf(CardPlayed(Card(seven, club), position = north))
        validateHand(myCards, bid = listBids.last { (it is SimpleBid) || (it is General) || (it is Capot) },
                     onTable = onTable)
        /* Resultat :CardPlayed(should annonce Card( 100 , club ) , position= east  )  */
        // val result = whatToPlay(myPosition, myCards, listBids, atout, onTable.toMutableList(), plisNS, plisEW)
        val result = IARun.enchere(myPosition, listBids, myCards, 0)


        /* you need to check result here */

        traceLevel = oldTraceLevel
        assert(result.curColor() == club && result.curPoint() == 100) { "$nameTest FAIL $result is not accurate" }
        debugPrintln(dbgLevel.REGULAR, "$nameTest:PASS  we play  :$result ")

    }


    /*
Name : annonce130Club
Atout : C
Bid : C 100 w
Moi : e
Mon jeu : [ 1 D , 10 D , 1 H , 10 H , 11 H , 12 S , 13 S , 13 C ]
Table : [ n 7 C ]
Test : e , whatToAnnonce
Resultat : e should annonce 130 C

*/

    @Test
    fun testannonce130Club() {
        val nameTest = object {}.javaClass.enclosingMethod.name
        val oldTraceLevel = traceLevel
        val plisEW: MutableMap<Int, List<CardPlayed>> = mutableMapOf()
        val plisNS: MutableMap<Int, List<CardPlayed>> = mutableMapOf()
        var nb = 0

        val atout = club
        val bid = SimpleBid(club, 100, west)
        val listBids: MutableList<Bid> = mutableListOf(bid, Pass(west), Pass(north), Pass(east), Pass(south))
        val myPosition = east
        val myCards = mutableListOf(Card(ace, diamond), Card(ten, diamond), Card(ace, heart), Card(ten, heart),
                                    Card(jack, heart), Card(queen, spade), Card(king, spade), Card(king, club))
        /* Resultat :CardPlayed(should annonce Card( 130 , club ) , position= east  )  */
        // val result = whatToPlay(myPosition, myCards, listBids, atout, onTable.toMutableList(), plisNS, plisEW)
        val result = IARun.enchere(myPosition, listBids, myCards, 0)


        /* you need to check result here */

        traceLevel = oldTraceLevel
        assert(result.curPoint() == 130 && result.curColor() == club) { "$nameTest FAIL $result is not accurate" }
        debugPrintln(dbgLevel.REGULAR, "$nameTest:PASS  we play  :$result ")

    }

/*
Name : RuleA1_001
Atout : D
Bid : D 80 w
Moi : s
Mon jeu : [ 7 H ]
Pli 1 : [   ]
Table : [ w 1 C , n 7 D , e 8 S ]
Test : ____playColor ____ check e and n have no C
Resultat : e and n have not any more C

*/

    @Test
    fun testRuleA1_001() {
        val nameTest = object {}.javaClass.enclosingMethod.name
        val oldTraceLevel = traceLevel
        val plisEW: MutableMap<Int, List<CardPlayed>> = mutableMapOf()
        val plisNS: MutableMap<Int, List<CardPlayed>> = mutableMapOf()
        var nb = 0

        val atout = diamond
        val bid = SimpleBid(diamond, 80, west)
        val listBids: MutableList<Bid> = mutableListOf(bid, Pass(west), Pass(north), Pass(east), Pass(south))
        val myPosition = south
        val myCards = mutableListOf(Card(eight, diamond))
        val onTable: MutableList<CardPlayed> = mutableListOf(CardPlayed(Card(ace, club), position = west),
                                                             CardPlayed(Card(seven, diamond), position = north),
                                                             CardPlayed(Card(eight, spade), position = east))
        validateHand(myCards, bid = listBids.last { (it is SimpleBid) || (it is General) || (it is Capot) },
                     onTable = onTable)
        val nbPlis = plisEW.size + plisNS.size
        var currentPli: Map<Int, MutableList<CardPlayed>> = mapOf()
        if (onTable.isNotEmpty()) {
            currentPli = listOf(Pair(nbPlis, onTable)).toMap()
        }
        val allCardPli = (plisEW + plisNS + currentPli).toSortedMap().map { it.value }

        //val result = TODO("Test : ____playColor ____ check east and north have no club ")
        /* Resultat : east and north have not any more club  */
        // val result = whatToPlay(myPosition, myCards, listBids, atout, onTable, plisNS, plisEW)
        // val result = IARun.enchere(myPosition, listBids, myCards, 0)
        val result = playersHaveColor(atout, atout, bid, allCardPli, myPosition, myCards)


        /* you need to check result here */

        traceLevel = oldTraceLevel
        assert(!result[east]!! && result[south]!! && result[north]!! && result[west]!!) { "$nameTest FAIL $result is not accurate" }
        debugPrintln(dbgLevel.REGULAR, "$nameTest:PASS  we play  :$result ")

    }


/*
Name : RuleA1_002
Atout : H
Bid :  H 80 w
Moi : s
Mon jeu : [ 7 H ]
Pli 1 : [   ]
Table : [ w 1 C , n 7 D , e 8 S ]
Test : ____playColor ____ check n and s have no C
Resultat : n and s have  not any more C

*/

    @Test
    fun testRuleA1_002() {
        val nameTest = object {}.javaClass.enclosingMethod.name
        val oldTraceLevel = traceLevel
        val plisEW: MutableMap<Int, List<CardPlayed>> = mutableMapOf()
        val plisNS: MutableMap<Int, List<CardPlayed>> = mutableMapOf()
        var nb = 0

        val atout = heart
        val bid = SimpleBid(heart, 80, west)
        val listBids: MutableList<Bid> = mutableListOf(bid, Pass(west), Pass(north), Pass(east), Pass(south))
        val myPosition = south
        val myCards = mutableListOf(Card(eight, heart))
        val onTable: MutableList<CardPlayed> = mutableListOf(CardPlayed(Card(ace, club), position = west),
                                                             CardPlayed(Card(seven, diamond), position = north),
                                                             CardPlayed(Card(eight, spade), position = east))
        validateHand(myCards, bid = listBids.last { (it is SimpleBid) || (it is General) || (it is Capot) },
                     onTable = onTable)
        val nbPlis = plisEW.size + plisNS.size
        var currentPli: Map<Int, MutableList<CardPlayed>> = mapOf()
        if (onTable.isNotEmpty()) {
            currentPli = listOf(Pair(nbPlis, onTable)).toMap()
        }
        val allCardPli = (plisEW + plisNS + currentPli).toSortedMap().map { it.value }

        //val result = TODO("Test : ____playColor ____ check north and south have no club ")
        /* Resultat : north and south have  not any more club  */
        // val result = whatToPlay(myPosition, myCards, listBids, atout, onTable, plisNS, plisEW)
        // val result = IARun.enchere(myPosition, listBids, myCards, 0)
        val result = playersHaveColor(atout, atout, bid, allCardPli, myPosition, myCards)


        /* you need to check result here */

        traceLevel = oldTraceLevel
        assert(result[east]!! && result[south]!! && !result[north]!! && result[west]!!) { "$nameTest FAIL $result is not accurate" }

        debugPrintln(dbgLevel.REGULAR, "$nameTest:PASS  we play  :$result ")

    }


/*
Name : RuleA1_003
Atout : S
Bid :  S 80 w
Moi : s
Mon jeu : [ 7 H ]
Pli 1 : [ w 10 D , n 1 D , e 9 C , s 8 D  ]
Table : [  ]
Test : ____playColor ____ check e has no D
Resultat : e has not any more D


*/

    @Test
    fun testRuleA1_003() {
        val nameTest = object {}.javaClass.enclosingMethod.name
        val oldTraceLevel = traceLevel
        val plisEW: MutableMap<Int, List<CardPlayed>> = mutableMapOf()
        val plisNS: MutableMap<Int, List<CardPlayed>> = mutableMapOf()
        var nb = 0

        val atout = spade
        val bid = SimpleBid(spade, 80, west)
        val listBids: MutableList<Bid> = mutableListOf(bid, Pass(west), Pass(north), Pass(east), Pass(south))
        val myPosition = south
        val myCards = mutableListOf(Card(nine, spade))
        plisNS[nb++] = listOf(CardPlayed(Card(ten, diamond), position = west),
                              CardPlayed(Card(ace, diamond), position = north),
                              CardPlayed(Card(nine, club), position = east),
                              CardPlayed(Card(eight, diamond), position = south))
        val onTable: MutableList<CardPlayed> = mutableListOf()
        validateHand(myCards, bid = listBids.last { (it is SimpleBid) || (it is General) || (it is Capot) },
                     onTable = onTable)
        val nbPlis = plisEW.size + plisNS.size
        var currentPli: Map<Int, MutableList<CardPlayed>> = mapOf()
        if (onTable.isNotEmpty()) {
            currentPli = listOf(Pair(nbPlis, onTable)).toMap()
        }
        val allCardPli = (plisEW + plisNS + currentPli).toSortedMap().map { it.value }

        // val result = TODO("Test : ____playColor ____ check east has no diamond ")
        /* Resultat : east has not any more diamond  */

        // val result = whatToPlay(myPosition, myCards, listBids, atout, onTable, plisNS, plisEW)
        // val result = IARun.enchere(myPosition, listBids, myCards, 0)
        val result = playersHaveColor(atout, atout, bid, allCardPli, myPosition, myCards)


        /* you need to check result here */

        traceLevel = oldTraceLevel
        assert(!result[east]!! && !!result[south]!! && result[north]!! && result[west]!!) { "$nameTest FAIL $result is not accurate" }
        debugPrintln(dbgLevel.REGULAR, "$nameTest:PASS  we play  :$result ")

    }


/*
Name : RuleA1_004
Atout : C
Bid :  C 80 w
Moi : s
Mon jeu : [ 7 H ]
Pli 1 : [ e 12 D , n 11 D , e 7 S , s 13 D ]
Table : [   ]
Test : ____playColor ____ check that e has no more D
Resultat : e has not any more D


*/

    @Test
    fun testRuleA1_004() {
        val nameTest = object {}.javaClass.enclosingMethod.name
        val oldTraceLevel = traceLevel
        // traceLevel = dbgLevel.ALL
        val plisEW: MutableMap<Int, List<CardPlayed>> = mutableMapOf()
        val plisNS: MutableMap<Int, List<CardPlayed>> = mutableMapOf()
        var nb = 0

        val atout = club
        val bid = SimpleBid(club, 80, west)
        val listBids: MutableList<Bid> = mutableListOf(bid, Pass(west), Pass(north), Pass(east), Pass(south))
        val myPosition = south
        val myCards = mutableListOf(Card(seven, club))
        plisNS[nb++] = listOf(CardPlayed(Card(queen, diamond), position = east),
                              CardPlayed(Card(jack, diamond), position = north),
                              CardPlayed(Card(seven, spade), position = east),
                              CardPlayed(Card(king, diamond), position = south))
        val onTable: MutableList<CardPlayed> = mutableListOf()
        validateHand(myCards, bid = listBids.last { (it is SimpleBid) || (it is General) || (it is Capot) },
                     onTable = onTable)
        val nbPlis = plisEW.size + plisNS.size
        var currentPli: Map<Int, MutableList<CardPlayed>> = mapOf()
        if (onTable.isNotEmpty()) {
            currentPli = listOf(Pair(nbPlis, onTable)).toMap()
        }
        val allCardPli = (plisEW + plisNS + currentPli).toSortedMap().map { it.value }

        //val result = TODO("Test : ____playColor ____ check that east has no more diamond ")
        /* Resultat : east has not any more diamond  */

        // val result = whatToPlay(myPosition, myCards, listBids, atout, onTable, plisNS, plisEW)
        // val result = IARun.enchere(myPosition, listBids, myCards, 0)
        val result = playersHaveColor(atout, atout, bid, allCardPli, myPosition, myCards)


        /* you need to check result here */

        traceLevel = oldTraceLevel
        assert(result[east]!! && !!result[south]!! && result[north]!! && result[west]!!) { "$nameTest FAIL $result is not accurate" }

        debugPrintln(dbgLevel.REGULAR, "$nameTest:PASS  we play  :$result ")

    }


/*
Atout : H
Fonction : testAtoutRule3
Moi : n
Bid : H 80 n
Mon jeu : [ 13 H ]
Pli  1 : [ n 10 H , e 8 H , s 11 H , w 7 H ]
Pli  2 : [ s 7 S , w 1 S , n 12 H , e 9 S ]
Table : [  ]
Test : call playersHaveColors and check East has no heart
Resultat : : e has no H

*/

    @Test
    fun testtestAtoutRule3() {
        val nameTest = object {}.javaClass.enclosingMethod.name
        val oldTraceLevel = traceLevel
        val plisEW: MutableMap<Int, List<CardPlayed>> = mutableMapOf()
        val plisNS: MutableMap<Int, List<CardPlayed>> = mutableMapOf()
        var nb = 0

        val atout = heart
        val myPosition = north
        val bid = SimpleBid(heart, 80, north)
        val listBids: MutableList<Bid> = mutableListOf(bid, Pass(west), Pass(north), Pass(east), Pass(south))
        val myCards = mutableListOf(Card(king, heart))
        plisNS[nb++] =
                listOf(CardPlayed(Card(ten, heart), position = north), CardPlayed(Card(eight, heart), position = east),
                       CardPlayed(Card(jack, heart), position = south), CardPlayed(Card(seven, heart), position = west))
        plisNS[nb++] =
                listOf(CardPlayed(Card(seven, spade), position = south), CardPlayed(Card(ace, spade), position = west),
                       CardPlayed(Card(queen, heart), position = north), CardPlayed(Card(nine, spade), position = east))
        val onTable: MutableList<CardPlayed> = mutableListOf()
        validateHand(myCards, bid = listBids.last { (it is SimpleBid) || (it is General) || (it is Capot) },
                     onTable = onTable)
        val nbPlis = plisEW.size + plisNS.size
        var currentPli: Map<Int, MutableList<CardPlayed>> = mapOf()
        if (onTable.isNotEmpty()) {
            currentPli = listOf(Pair(nbPlis, onTable)).toMap()
        }
        val allCardPli = (plisEW + plisNS + currentPli).toSortedMap().map { it.value }

        /* Resultat : : east has no heart  */


        // val result = whatToPlay(myPosition, myCards, listBids, atout, onTable, plisNS, plisEW)
        // val result = IARun.enchere(myPosition, listBids, myCards, 0)
        val result = playersHaveColor(atout, atout, bid, allCardPli, myPosition, myCards)


        /* you need to check result here */

        traceLevel = oldTraceLevel
        assert(!result[east]!! and result[west]!! and result[north]!! and result[south]!!) { "$nameTest FAIL $result is not accurate" }
        debugPrintln(dbgLevel.REGULAR, "$nameTest:PASS  we play  :$result ")

    }

/*

Atout : H
Fonction : testAtoutRule4
Moi : s
Bid : H 80 w
Mon jeu : [ 7 S ]
Pli  1 : [ w 1 H , n 7 H , e 8 H , s 10 H ]
Table : [  ]
Test : call playersHaveColors and check rules R4 Atout
Resultat : : s has no trump H



*/

    /*

 Atout : H
 Fonction : testAtoutRule4
 Moi : s
 Bid : H 80 w
 Mon jeu : [ 7 S ]
 Pli  1 : [ w 1 H , n 7 H , e 10 H , s 8 H ]
 Table : [  ]
 Test : call playersHaveColors and check rules R4 Atout
 Resultat : : s and e have no trump H



 */

    @Test
    fun testtestAtoutRule4() {
        val nameTest = object {}.javaClass.enclosingMethod.name
        val oldTraceLevel = traceLevel
        val plisEW: MutableMap<Int, List<CardPlayed>> = mutableMapOf()
        val plisNS: MutableMap<Int, List<CardPlayed>> = mutableMapOf()
        var nb = 0


        val atout = heart
        val myPosition = south
        val bid = SimpleBid(heart, 80, west)
        val listBids: MutableList<Bid> = mutableListOf(bid, Pass(west), Pass(north), Pass(east), Pass(south))
        val myCards = mutableListOf(Card(seven, spade))
        plisNS[nb++] =
                listOf(CardPlayed(Card(ace, heart), position = west), CardPlayed(Card(seven, heart), position = north),
                       CardPlayed(Card(ten, heart), position = east), CardPlayed(Card(eight, heart), position = south))
        val onTable: MutableList<CardPlayed> = mutableListOf()
        validateHand(myCards, bid = listBids.last { (it is SimpleBid) || (it is General) || (it is Capot) },
                     onTable = onTable)
        val nbPlis = plisEW.size + plisNS.size
        var currentPli: Map<Int, MutableList<CardPlayed>> = mapOf()
        if (onTable.isNotEmpty()) {
            currentPli = listOf(Pair(nbPlis, onTable)).toMap()
        }
        val allCardPli = (plisEW + plisNS + currentPli).toSortedMap().map { it.value }

        /* Resultat : : south and east have no trump heart   */


        // val result = whatToPlay(myPosition, myCards, listBids, atout, onTable, plisNS, plisEW)
        // val result = IARun.enchere(myPosition, listBids, myCards, 0)
        val result = playersHaveColor(atout, atout, bid, allCardPli, myPosition, myCards)


        /* you need to check result here */

        traceLevel = oldTraceLevel
        assert(result[east]!! && result[north]!! && result[west]!! && !result[south]!!) { "$nameTest FAIL $result is not accurate" }
        debugPrintln(dbgLevel.REGULAR, "$nameTest:PASS  we play  :$result ")

    }

    /*

  Atout : H
  Fonction : testAtoutRule4
  Moi : s
  Bid : H 80 w
  Mon jeu : [ 7 S ]
  Pli  1 : [ w 1 H , n 7 H , e 10 H , s 8 H ]
  Table : [  ]
  Test : call playersHaveColors and check rules R4 Atout
  Resultat : : s and e have no trump H



  */

    @Test
    fun testtestAtoutRule4b() {
        val nameTest = object {}.javaClass.enclosingMethod.name
        val oldTraceLevel = traceLevel
        val plisEW: MutableMap<Int, List<CardPlayed>> = mutableMapOf()
        val plisNS: MutableMap<Int, List<CardPlayed>> = mutableMapOf()
        var nb = 0


        val atout = heart
        val myPosition = south
        val bid = SimpleBid(heart, 80, west)
        val listBids: MutableList<Bid> = mutableListOf(bid, Pass(west), Pass(north), Pass(east), Pass(south))
        val myCards = mutableListOf(Card(seven, spade))
        plisNS[nb++] =
                listOf(CardPlayed(Card(ace, heart), position = west), CardPlayed(Card(ten, heart), position = north),
                       CardPlayed(Card(seven, heart), position = east),
                       CardPlayed(Card(eight, heart), position = south))
        val onTable: MutableList<CardPlayed> = mutableListOf()
        validateHand(myCards, bid = listBids.last { (it is SimpleBid) || (it is General) || (it is Capot) },
                     onTable = onTable)
        val nbPlis = plisEW.size + plisNS.size
        var currentPli: Map<Int, MutableList<CardPlayed>> = mapOf()
        if (onTable.isNotEmpty()) {
            currentPli = listOf(Pair(nbPlis, onTable)).toMap()
        }
        val allCardPli = (plisEW + plisNS + currentPli).toSortedMap().map { it.value }

        /* Resultat : : south and east have no trump heart   */


        // val result = whatToPlay(myPosition, myCards, listBids, atout, onTable, plisNS, plisEW)
        // val result = IARun.enchere(myPosition, listBids, myCards, 0)
        val result = playersHaveColor(atout, atout, bid, allCardPli, myPosition, myCards)


        /* you need to check result here */

        traceLevel = oldTraceLevel
        assert(result[east]!! && !result[north]!! && result[west]!! && !result[south]!!) { "$nameTest FAIL $result is not accurate" }
        debugPrintln(dbgLevel.REGULAR, "$nameTest:PASS  we play  :$result ")

    }

    @Test
    fun testtestAtoutRule4c() {
        val nameTest = object {}.javaClass.enclosingMethod.name
        val oldTraceLevel = traceLevel
        val plisEW: MutableMap<Int, List<CardPlayed>> = mutableMapOf()
        val plisNS: MutableMap<Int, List<CardPlayed>> = mutableMapOf()
        var nb = 0


        val atout = heart
        val myPosition = west
        val bid = SimpleBid(heart, 80, west)
        val listBids: MutableList<Bid> = mutableListOf(bid, Pass(west), Pass(north), Pass(east), Pass(south))
        val myCards = mutableListOf(Card(ace, heart))
        plisNS[nb++] =
                listOf(CardPlayed(Card(nine, heart), position = west), CardPlayed(Card(ten, heart), position = north),
                       CardPlayed(Card(seven, heart), position = east),
                       CardPlayed(Card(eight, heart), position = south))
        val onTable: MutableList<CardPlayed> = mutableListOf()
        validateHand(myCards, bid = listBids.last { (it is SimpleBid) || (it is General) || (it is Capot) },
                     onTable = onTable)
        val nbPlis = plisEW.size + plisNS.size
        var currentPli: Map<Int, MutableList<CardPlayed>> = mapOf()
        if (onTable.isNotEmpty()) {
            currentPli = listOf(Pair(nbPlis, onTable)).toMap()
        }
        val allCardPli = (plisEW + plisNS + currentPli).toSortedMap().map { it.value }

        /* Resultat : : south and east have no trump heart   */


        // val result = whatToPlay(myPosition, myCards, listBids, atout, onTable, plisNS, plisEW)
        // val result = IARun.enchere(myPosition, listBids, myCards, 0)
        val result = playersHaveColor(atout, atout, bid, allCardPli, myPosition, myCards)


        /* you need to check result here */

        traceLevel = oldTraceLevel
        assert(result[east]!! && !result[north]!! && result[west]!! && result[south]!!) { "$nameTest FAIL $result is not accurate" }
        debugPrintln(dbgLevel.REGULAR, "$nameTest:PASS  we play  :$result ")

    }


    @Test
    fun testtestAtoutRule4d() {
        val nameTest = object {}.javaClass.enclosingMethod.name
        val oldTraceLevel = traceLevel
        val plisEW: MutableMap<Int, List<CardPlayed>> = mutableMapOf()
        val plisNS: MutableMap<Int, List<CardPlayed>> = mutableMapOf()
        var nb = 0


        val atout = heart
        val myPosition = west
        val bid = SimpleBid(heart, 80, west)
        val listBids: MutableList<Bid> = mutableListOf(bid, Pass(west), Pass(north), Pass(east), Pass(south))
        val myCards = mutableListOf(Card(ace, spade))
        plisNS[nb++] =
                listOf(CardPlayed(Card(nine, heart), position = west), CardPlayed(Card(ten, heart), position = north),
                       CardPlayed(Card(seven, heart), position = east),
                       CardPlayed(Card(eight, heart), position = south))
        val onTable: MutableList<CardPlayed> = mutableListOf()
        validateHand(myCards, bid = listBids.last { (it is SimpleBid) || (it is General) || (it is Capot) },
                     onTable = onTable)
        val nbPlis = plisEW.size + plisNS.size
        var currentPli: Map<Int, MutableList<CardPlayed>> = mapOf()
        if (onTable.isNotEmpty()) {
            currentPli = listOf(Pair(nbPlis, onTable)).toMap()
        }
        val allCardPli = (plisEW + plisNS + currentPli).toSortedMap().map { it.value }

        /* Resultat : : south and east have no trump heart   */


        // val result = whatToPlay(myPosition, myCards, listBids, atout, onTable, plisNS, plisEW)
        // val result = IARun.enchere(myPosition, listBids, myCards, 0)
        val result = playersHaveColor(atout, atout, bid, allCardPli, myPosition, myCards)


        /* you need to check result here */

        traceLevel = oldTraceLevel
        assert(result[east]!! && result[north]!! && !result[west]!! && result[south]!!) { "$nameTest FAIL $result is not accurate" }
        debugPrintln(dbgLevel.REGULAR, "$nameTest:PASS  we play  :$result ")

    }


    @Test
    fun testtestAtoutRule4e() {
        val nameTest = object {}.javaClass.enclosingMethod.name
        val oldTraceLevel = traceLevel
        val plisEW: MutableMap<Int, List<CardPlayed>> = mutableMapOf()
        val plisNS: MutableMap<Int, List<CardPlayed>> = mutableMapOf()
        var nb = 0


        val atout = heart
        val myPosition = west
        val bid = SimpleBid(heart, 80, west)
        val listBids: MutableList<Bid> = mutableListOf(bid, Pass(west), Pass(north), Pass(east), Pass(south))
        val myCards = mutableListOf(Card(ace, spade))
        plisNS[nb++] =
                listOf(CardPlayed(Card(nine, heart), position = west), CardPlayed(Card(ten, heart), position = north),
                       CardPlayed(Card(eight, heart), position = east), CardPlayed(Card(ace, heart), position = south))
        val onTable: MutableList<CardPlayed> = mutableListOf()
        validateHand(myCards, bid = listBids.last { (it is SimpleBid) || (it is General) || (it is Capot) },
                     onTable = onTable)
        val nbPlis = plisEW.size + plisNS.size
        var currentPli: Map<Int, MutableList<CardPlayed>> = mapOf()
        if (onTable.isNotEmpty()) {
            currentPli = listOf(Pair(nbPlis, onTable)).toMap()
        }
        val allCardPli = (plisEW + plisNS + currentPli).toSortedMap().map { it.value }

        /* Resultat : : south and east have no trump heart   */


        // val result = whatToPlay(myPosition, myCards, listBids, atout, onTable, plisNS, plisEW)
        // val result = IARun.enchere(myPosition, listBids, myCards, 0)
        val result = playersHaveColor(atout, atout, bid, allCardPli, myPosition, myCards)


        /* you need to check result here */

        traceLevel = oldTraceLevel
        assert(result[east]!! && !result[north]!! && !result[west]!! && !result[south]!!) { "$nameTest FAIL $result is not accurate" }
        debugPrintln(dbgLevel.REGULAR, "$nameTest:PASS  we play  :$result ")

    }

/*
Atout : S
Fonction : testRule4Sacha1
Moi : s
Bid : S 80 w
Mon jeu : [ 7 S , 7 C , 9 C , 7 D , 13 D ]
Pli 1 : [ s 1 H , w 13 H , n 7 H , e 8 H ]
Pli 2 : [ s 10 H , w 1 S , n 9 S , e 11 H ]
Pli 3 : [ n 11 S , e 10 S , s 12 S , w 13 S ]
Table : [ n 13 C , e 11 C ]
Test : Atout , R4A
Resultat : est et ouest n’ont plus d’atout

*/

    @Test
    fun testRule4Sacha1() {
        val nameTest = object {}.javaClass.enclosingMethod.name
        val oldTraceLevel = traceLevel
        val plisEW: MutableMap<Int, List<CardPlayed>> = mutableMapOf()
        val plisNS: MutableMap<Int, List<CardPlayed>> = mutableMapOf()
        var nb = 0

        val atout = spade
        val myPosition = south
        val bid = SimpleBid(spade, 80, west)
        val listBids: MutableList<Bid> = mutableListOf(bid, Pass(west), Pass(north), Pass(east), Pass(south))
        val myCards = mutableListOf(Card(seven, spade), Card(seven, club), Card(nine, club), Card(seven, diamond),
                                    Card(king, diamond))
        plisNS[nb++] =
                listOf(CardPlayed(Card(ace, heart), position = south), CardPlayed(Card(king, heart), position = west),
                       CardPlayed(Card(seven, heart), position = north),
                       CardPlayed(Card(eight, heart), position = east))
        plisNS[nb++] =
                listOf(CardPlayed(Card(ten, heart), position = south), CardPlayed(Card(ace, spade), position = west),
                       CardPlayed(Card(nine, spade), position = north), CardPlayed(Card(jack, heart), position = east))
        plisNS[nb++] =
                listOf(CardPlayed(Card(jack, spade), position = north), CardPlayed(Card(ten, spade), position = east),
                       CardPlayed(Card(queen, spade), position = south), CardPlayed(Card(king, spade), position = west))
        val onTable: MutableList<CardPlayed> = mutableListOf(CardPlayed(Card(king, club), position = north),
                                                             CardPlayed(Card(jack, club), position = east))
        validateHand(myCards, bid = listBids.last { (it is SimpleBid) || (it is General) || (it is Capot) },
                     onTable = onTable)
        val nbPlis = plisEW.size + plisNS.size
        var currentPli: Map<Int, MutableList<CardPlayed>> = mapOf()
        if (onTable.isNotEmpty()) {
            currentPli = listOf(Pair(nbPlis, onTable)).toMap()
        }
        val allCardPli = (plisEW + plisNS + currentPli).toSortedMap().map { it.value }

        /* Resultat : est et ouest n’ont plus d’atout */
        // val result = whatToPlay(myPosition, myCards, listBids, atout, onTable, plisNS, plisEW)
        // val result = IARun.enchere(myPosition, listBids, myCards, 0)
        val result = playersHaveColor(atout, atout, bid, allCardPli, myPosition, myCards)


        /* you need to check result here */

        traceLevel = oldTraceLevel
        assert(!result[east]!! && result[north]!! && !result[west]!! && result[south]!!) { "$nameTest FAIL $result is not accurate" }
        debugPrintln(dbgLevel.REGULAR, "$nameTest:PASS  we play  :$result ")

    }


/*
Atout : H
Fonction : testRule4Sacha2
Moi : s
Bid : H 80 w
Mon jeu : [ 1 H , 9 D , 8 D , 8 S , 12 S , 9 S ]
Pli 1 : [ w 8 C , n 11 C , e 1 C , s 12 H ]
Pli 2 : [ s 13 H , w 9 H , n 11 H , e 10 H ]
Table : [ n 1 S , e 10 S ]
Test : Atout, R4A
Resultat : est n’a plus d’atout


*/

    @Test
    fun testRule4Sacha2() {
        val nameTest = object {}.javaClass.enclosingMethod.name
        val oldTraceLevel = traceLevel
        val plisEW: MutableMap<Int, List<CardPlayed>> = mutableMapOf()
        val plisNS: MutableMap<Int, List<CardPlayed>> = mutableMapOf()
        var nb = 0

        val atout = heart
        val myPosition = south
        val bid = SimpleBid(heart, 80, west)
        val listBids: MutableList<Bid> = mutableListOf(bid, Pass(west), Pass(north), Pass(east), Pass(south))
        val myCards = mutableListOf(Card(ace, heart), Card(nine, diamond), Card(eight, diamond), Card(eight, spade),
                                    Card(queen, spade), Card(nine, spade))
        plisNS[nb++] =
                listOf(CardPlayed(Card(eight, club), position = west), CardPlayed(Card(jack, club), position = north),
                       CardPlayed(Card(ace, club), position = east), CardPlayed(Card(queen, heart), position = south))
        plisNS[nb++] =
                listOf(CardPlayed(Card(king, heart), position = south), CardPlayed(Card(nine, heart), position = west),
                       CardPlayed(Card(jack, heart), position = north), CardPlayed(Card(ten, heart), position = east))
        val onTable: MutableList<CardPlayed> = mutableListOf(CardPlayed(Card(ace, spade), position = north),
                                                             CardPlayed(Card(ten, spade), position = east))
        validateHand(myCards, bid = listBids.last { (it is SimpleBid) || (it is General) || (it is Capot) },
                     onTable = onTable)
        val nbPlis = plisEW.size + plisNS.size
        var currentPli: Map<Int, MutableList<CardPlayed>> = mapOf()
        if (onTable.isNotEmpty()) {
            currentPli = listOf(Pair(nbPlis, onTable)).toMap()
        }
        val allCardPli = (plisEW + plisNS + currentPli).toSortedMap().map { it.value }

        /* Resultat : est n’a plus d’atout */

        //val result = whatToPlay(myPosition, myCards, listBids, atout, onTable, plisNS, plisEW)
        // val result = IARun.enchere(myPosition, listBids, myCards, 0)
        val result = playersHaveColor(atout, atout, bid, allCardPli, myPosition, myCards)


        /* you need to check result here */

        traceLevel = oldTraceLevel
        assert(!result[east]!! && result[north]!! && result[west]!! && result[south]!!) { "$nameTest FAIL $result is not accurate" }
        debugPrintln(dbgLevel.REGULAR, "$nameTest:PASS  we play  :$result ")

    }


/*
Atout : C
Fonction : testRule4Sacha3
Moi : s
Bid : C 80 w
Mon jeu : [ 7 H , 8 H , 10 D , 8 C , 8 S ]
Pli 1 : [ s 13 D , w 1 D , n 7 D , e 8 D ]
Pli 2 : [ w 1 H , n 10 H , e 11 H , s 9 H ]
Pli 3 : [ w 13 H , n 9 C , e 1 C , s 12 H ]
Table : [ n 1 S , e 7 S ]
Test : Atout, R4A
Resultat : est n’a plus d’atout


*/

    @Test
    fun testRule4Sacha3() {
        val nameTest = object {}.javaClass.enclosingMethod.name
        val oldTraceLevel = traceLevel
        val plisEW: MutableMap<Int, List<CardPlayed>> = mutableMapOf()
        val plisNS: MutableMap<Int, List<CardPlayed>> = mutableMapOf()
        var nb = 0

        val atout = club
        val myPosition = south
        val bid = SimpleBid(club, 80, west)
        val listBids: MutableList<Bid> = mutableListOf(bid, Pass(west), Pass(north), Pass(east), Pass(south))
        val myCards = mutableListOf(Card(seven, heart), Card(eight, heart), Card(ten, diamond), Card(eight, club),
                                    Card(eight, spade))
        plisNS[nb++] = listOf(CardPlayed(Card(king, diamond), position = south),
                              CardPlayed(Card(ace, diamond), position = west),
                              CardPlayed(Card(seven, diamond), position = north),
                              CardPlayed(Card(eight, diamond), position = east))
        plisNS[nb++] =
                listOf(CardPlayed(Card(ace, heart), position = west), CardPlayed(Card(ten, heart), position = north),
                       CardPlayed(Card(jack, heart), position = east), CardPlayed(Card(nine, heart), position = south))
        plisNS[nb++] =
                listOf(CardPlayed(Card(king, heart), position = west), CardPlayed(Card(nine, club), position = north),
                       CardPlayed(Card(ace, club), position = east), CardPlayed(Card(queen, heart), position = south))
        val onTable: MutableList<CardPlayed> = mutableListOf(CardPlayed(Card(ace, spade), position = north),
                                                             CardPlayed(Card(seven, spade), position = east))
        validateHand(myCards, bid = listBids.last { (it is SimpleBid) || (it is General) || (it is Capot) },
                     onTable = onTable)
        val nbPlis = plisEW.size + plisNS.size
        var currentPli: Map<Int, MutableList<CardPlayed>> = mapOf()
        if (onTable.isNotEmpty()) {
            currentPli = listOf(Pair(nbPlis, onTable)).toMap()
        }
        val allCardPli = (plisEW + plisNS + currentPli).toSortedMap().map { it.value }

        /* Resultat : est n’a plus d’atout */

        // val result = whatToPlay(myPosition, myCards, listBids, atout, onTable, plisNS, plisEW)
        // val result = IARun.enchere(myPosition, listBids, myCards, 0)
        val result = playersHaveColor(atout, atout, bid, allCardPli, myPosition, myCards)


        /* you need to check result here */

        traceLevel = oldTraceLevel
        assert(!result[east]!! && result[north]!! && result[west]!! && result[south]!!) { "$nameTest FAIL $result is not accurate" }
        debugPrintln(dbgLevel.REGULAR, "$nameTest:PASS  we play  :$result ")

    }


/*
Atout C
Fonction : testRule4Sacha4
Moi : s
Bid : C 80 w
Mon jeu : [ 10 H , 12 H , 9 C ]
Pli 1 : [ w 7 S , n 13 S , e 12 S , s 11 S ]
Pli 2 : [ n 1 D , e 12 D , s 9 S , w 13 D ]
Pli 3 : [ n 13 C , e 10 C , s 7 C , w 8 C ]
Pli 4 : [ n 8 S , e 10 S , s 1 S , w 1 C ]
Table : [ w 1 H , n 9 H , e 11 H ]
Test : Atout, R4A (futur
Resultat : est n’a plus da’tout

*/

    @Test
    fun testRule4Sacha4() {
        val nameTest = object {}.javaClass.enclosingMethod.name
        val oldTraceLevel = traceLevel
        val plisEW: MutableMap<Int, List<CardPlayed>> = mutableMapOf()
        val plisNS: MutableMap<Int, List<CardPlayed>> = mutableMapOf()
        var nb = 0

        val atout = club
        val myPosition = south
        val bid = SimpleBid(club, 80, west)
        val listBids: MutableList<Bid> = mutableListOf(bid, Pass(west), Pass(north), Pass(east), Pass(south))
        val myCards = mutableListOf(Card(ten, heart), Card(queen, heart), Card(nine, club))
        plisNS[nb++] =
                listOf(CardPlayed(Card(seven, spade), position = west),
                       CardPlayed(Card(king, spade), position = north),
                       CardPlayed(Card(queen, spade), position = east),
                       CardPlayed(Card(jack, spade), position = south))
        plisNS[nb++] = listOf(CardPlayed(Card(ace, diamond), position = north),
                              CardPlayed(Card(queen, diamond), position = east),
                              CardPlayed(Card(nine, spade), position = south),
                              CardPlayed(Card(king, diamond), position = west))
        plisNS[nb++] =
                listOf(CardPlayed(Card(jack, club), position = north),
                       CardPlayed(Card(ten, club), position = east),
                       CardPlayed(Card(seven, club), position = south),
                       CardPlayed(Card(eight, club), position = west))
        plisNS[nb++] =
                listOf(CardPlayed(Card(eight, spade), position = north),
                       CardPlayed(Card(ten, spade), position = east),
                       CardPlayed(Card(ace, spade), position = south),
                       CardPlayed(Card(ace, club), position = west))
        val onTable: MutableList<CardPlayed> =
                mutableListOf(CardPlayed(Card(ace, heart), position = west),
                              CardPlayed(Card(nine, heart), position = north),
                              CardPlayed(Card(jack, heart), position = east))
        validateHand(myCards, bid = listBids.last { (it is SimpleBid) || (it is General) || (it is Capot) },
                     onTable = onTable)
        val nbPlis = plisEW.size + plisNS.size
        var currentPli: Map<Int, MutableList<CardPlayed>> = mapOf()
        if (onTable.isNotEmpty()) {
            currentPli = listOf(Pair(nbPlis, onTable)).toMap()
        }
        val allCardPli = (plisEW + plisNS + currentPli).toSortedMap().map { it.value }

        /* Resultat : est n’a plus da’tout */
        // val result = whatToPlay(myPosition, myCards, listBids, atout, onTable, plisNS, plisEW)
        // val result = IARun.enchere(myPosition, listBids, myCards, 0)
        val result = playersHaveColor(atout, atout, bid, allCardPli, myPosition, myCards)


        /* you need to check result here */

        traceLevel = oldTraceLevel
        assert(!result[east]!! && result[north]!! && result[west]!! && result[south]!!) { "$nameTest FAIL $result is not accurate\n This test is complex to implement see explanation in test code" }
        // to pass this test you need to look back to trick 3 while analyzing trick 4
        // since we gave east gave 10 to north , that means that player east does not have 7, 8, Q or K
        // so it means that Ace is the last trump
        debugPrintln(dbgLevel.REGULAR, "$nameTest:PASS  we play  :$result ")

    }


    /*
Atout : H
Fonction : testRule5Atout
Moi : s
Bid : H 80 s
Mon jeu : [ 7 S , 7 C , 9 C , 13 H , 13 D ]
Pli 1 : [ w 7 H , n 12 H , e 9 H , s 11 H ]
Table : [ s 8 H ]
Test : Atout , R5A
Resultat : East n'a plus d'atout - on ne met pas un 9 comme ca ....

*/

    @Test
    fun testtestRule5Atout() {
        val nameTest = object {}.javaClass.enclosingMethod.name
        val oldTraceLevel = traceLevel
        val plisEW: MutableMap<Int, List<CardPlayed>> = mutableMapOf()
        val plisNS: MutableMap<Int, List<CardPlayed>> = mutableMapOf()
        var nb = 0

        val atout = heart
        val myPosition = south
        val bid = SimpleBid(heart, 80, south)
        val listBids: MutableList<Bid> = mutableListOf(bid, Pass(west), Pass(north), Pass(east), Pass(south))
        val myCards = mutableListOf(Card(seven, spade), Card(seven, club), Card(nine, club), Card(king, heart),
                                    Card(king, diamond))
        plisNS[nb++] = listOf(CardPlayed(Card(seven, heart), position = west),
                              CardPlayed(Card(queen, heart), position = north),
                              CardPlayed(Card(nine, heart), position = east),
                              CardPlayed(Card(jack, heart), position = south))
        val onTable: MutableList<CardPlayed> = mutableListOf(CardPlayed(Card(eight, heart), position = south))
        validateHand(myCards, bid = listBids.last { (it is SimpleBid) || (it is General) || (it is Capot) },
                     onTable = onTable)
        val nbPlis = plisEW.size + plisNS.size
        var currentPli: Map<Int, MutableList<CardPlayed>> = mapOf()
        if (onTable.isNotEmpty()) {
            currentPli = listOf(Pair(nbPlis, onTable)).toMap()
        }
        val allCardPli = (plisEW + plisNS + currentPli).toSortedMap().map { it.value }

        /* Resultat : East n'a plus d'atout - on ne met pas un nine comme ca .... */
        // val result = whatToPlay(myPosition, myCards, listBids, atout, onTable, plisNS, plisEW)
        // val result = IARun.enchere(myPosition, listBids, myCards, 0)
        val result = playersHaveColor(atout, atout, bid, allCardPli, myPosition, myCards)


        /* you need to check result here */

        traceLevel = oldTraceLevel
        assert(!result[east]!! && result[north]!! && result[west]!! && result[south]!!) { "$nameTest FAIL $result is not accurate" }
        debugPrintln(dbgLevel.REGULAR, "$nameTest:PASS  we play  :$result ")

    }


    /*
  Atout : H
  Fonction : Rule5Atout
  Moi : s
  Bid : H 80 s
  Mon jeu : [ 7 S , 7 C , 9 C , 13 H , 11 H ]
  Pli 1 : [ w 7 H , n 12 H , e 9 H , s 13 H ]
  Pli 2 : [ e 1 C , s 8 H , w 9 C , n 8 C ]
  Table : [  ]
  Test : Atout , R5A
  Resultat : East n'a plus d'atout - on ne met pas un 9 comme ca ....

  */

    @Test
    fun testRule5Atout() {
        val nameTest = object {}.javaClass.enclosingMethod.name
        val oldTraceLevel = traceLevel
        val plisEW: MutableMap<Int, List<CardPlayed>> = mutableMapOf()
        val plisNS: MutableMap<Int, List<CardPlayed>> = mutableMapOf()
        var nb=0

        val atout=heart
        val myPosition=south
        val bid = SimpleBid( heart ,80, south )
        val listBids: MutableList<Bid> = mutableListOf(bid, Pass(west), Pass(north), Pass(east), Pass(south) )
        val myCards = mutableListOf( Card( seven , spade ) , Card( seven , club ) , Card( nine , club ) , Card( king , heart ) , Card( jack , heart ) )
        plisNS[nb++] = listOf(CardPlayed(Card( seven , heart ) , position= west  ) ,CardPlayed(Card( queen , heart ) , position= north  ) ,CardPlayed(Card( nine , heart ) , position= east  ) ,CardPlayed(Card( king , heart ) , position= south  ) )
        plisNS[nb++] = listOf(CardPlayed(Card( ace , club ) , position= east  ) ,CardPlayed(Card( eight , heart ) , position= south  ) ,CardPlayed(Card( nine , club ) , position= west  ) ,CardPlayed(Card( eight , club ) , position= north  ) )
        val onTable :MutableList<CardPlayed> = mutableListOf(  )
        validateHand(myCards,bid = listBids.last{ (it is SimpleBid) ||  (it is General) || (it is Capot)},onTable = onTable)
        val nbPlis = plisEW.size + plisNS.size
        var currentPli: Map<Int, MutableList<CardPlayed>> = mapOf()
        if (onTable.isNotEmpty()) {
            currentPli = listOf(Pair(nbPlis, onTable)).toMap()
        }
        val allCardPli = (plisEW + plisNS + currentPli).toSortedMap().map { it.value }

        //val result = TODO("Test : Atout , R5A")
        /* Resultat : East n'a plus d'atout - on ne met pas un nine comme ca .... */
        // val result = whatToPlay(myPosition, myCards, listBids, atout, onTable, plisNS, plisEW)
        // val result = IARun.enchere(myPosition, listBids, myCards, 0)
        val result = playersHaveColor(atout, atout, bid, allCardPli, myPosition, myCards)


        /* you need to check result here */

        traceLevel = oldTraceLevel
        assert(!result[east]!! && result[north]!! && result[west]!! && result[south]!!) { "$nameTest FAIL $result is not accurate" }
//assert(result.value == heart && result.color == king ) { "$nameTest FAIL $result is not accurate" }
//assert(result.curcolor()  == heart && result.curPoints == 80 ) { "$nameTest FAIL $result is not accurate" }

        debugPrintln(dbgLevel.REGULAR, "$nameTest:PASS  we play  :$result ")

    }


    /*
Atout : S
Fonction : enchere100Heart
Moi : s
Bid : S 80 e
Mon jeu : [ 11 H , 9 H , 1 D , 1 C , 10 C , 1 S , 10 S , 13 S ]
Pli  1 : [  ]
Table : [  ]
Test : enchere 100 H ?
Resultat : : check Enchere



*/

    @Test
    fun testenchere100Heart() {
        val nameTest = object {}.javaClass.enclosingMethod.name
        val oldTraceLevel = traceLevel
        val plisEW: MutableMap<Int, List<CardPlayed>> = mutableMapOf()
        val plisNS: MutableMap<Int, List<CardPlayed>> = mutableMapOf()
        var nb = 0

        val atout = spade
        val myPosition = south
        val bid = SimpleBid(spade, 80, east)
        val listBids: MutableList<Bid> = mutableListOf(Pass(west), Pass(north), bid)
        val myCards = mutableListOf(Card(jack, heart), Card(nine, heart), Card(ace, diamond), Card(ace, club),
                                    Card(ten, club), Card(ace, spade), Card(ten, spade), Card(king, spade))
        plisNS[nb++] = listOf()
        val onTable: MutableList<CardPlayed> = mutableListOf()
        validateHand(myCards, bid = listBids.last { (it is SimpleBid) || (it is General) || (it is Capot) },
                     onTable = onTable)
        val nbPlis = plisEW.size + plisNS.size
        var currentPli: Map<Int, MutableList<CardPlayed>> = mapOf()
        if (onTable.isNotEmpty()) {
            currentPli = listOf(Pair(nbPlis, onTable)).toMap()
        }
        val allCardPli = (plisEW + plisNS + currentPli).toSortedMap().map { it.value }

        //val result = TODO("Test : enchere Card( 100 , heart ) ? ")
        /* Resultat : : check Enchere */


        // val result = whatToPlay(myPosition, myCards, listBids, atout, onTable, plisNS, plisEW)
        val result = IARun.enchere(myPosition, listBids, myCards, 0)
        // val result = playersHaveColor(atout, atout, bid, allCardPli, myPosition, myCards)


        /* you need to check result here */

        traceLevel = oldTraceLevel
//assert(result[east]!! && result[north]!! && result[west]!! && result[south]!! ) { "$nameTest FAIL $result is not accurate" }
//assert(result.value == heart && result.color == king ) { "$nameTest FAIL $result is not accurate" }
        assert((result.curColor() == heart) && (result.curPoint() == 130)) { "$nameTest FAIL $result is not accurate" }
//        assert(TODO()) {"$nameTes tFAIL : $result is not OK ")

        debugPrintln(dbgLevel.REGULAR, "$nameTest:PASS  we play  :$result ")

    }



    /*

Atout : H
Fonction : ARule6
Moi : n
Bid : S 120 e
Mon jeu : [ 8 H , 13 H , 9 C , 12 C , 7 C , 7 D ]
Pli  1 : [ s 9 H , w 1 H , n 12 H , e 7 H ]
Pli  2 : [ s 12 S , w 10 S , n 1 S , e 13 S ]
Table : [  ]
Test : call playersHaveColors and check rules R6
Resultat : : e and w have no trump



*/

    @Test
    fun testARule6() {
        val nameTest = object {}.javaClass.enclosingMethod.name
        val oldTraceLevel = traceLevel
        val plisEW: MutableMap<Int, List<CardPlayed>> = mutableMapOf()
        val plisNS: MutableMap<Int, List<CardPlayed>> = mutableMapOf()
        var nb=0


        val atout=heart
        val myPosition=north
        val bid = SimpleBid( spade ,120, south )
        val listBids: MutableList<Bid> = mutableListOf(bid, Pass(west), Pass(north), Pass(east), Pass(south) )
        val myCards = mutableListOf( Card( eight , heart ) , Card( king , heart ) , Card( nine , club ) , Card( queen , club ) , Card( seven , club ) , Card( seven , diamond ) )
        plisNS[nb++] = listOf(CardPlayed(Card( nine , heart ) , position= south  ) ,CardPlayed(Card( ace , heart ) , position= west  ) ,CardPlayed(Card( queen , heart ) , position= north  ) ,CardPlayed(Card( seven , heart ) , position= east  ) )
        plisNS[nb++] = listOf(CardPlayed(Card( queen , spade ) , position= south  ) ,CardPlayed(Card( ten , spade ) , position= west  ) ,CardPlayed(Card( ace , spade ) , position= north  ) ,CardPlayed(Card( king , spade ) , position= east  ) )
        val onTable :MutableList<CardPlayed> = mutableListOf(  )
        validateHand(myCards,bid = listBids.last{ (it is SimpleBid) ||  (it is General) || (it is Capot)},onTable = onTable)
        val nbPlis = plisEW.size + plisNS.size
        var currentPli: Map<Int, MutableList<CardPlayed>> = mapOf()
        if (onTable.isNotEmpty()) {
            currentPli = listOf(Pair(nbPlis, onTable)).toMap()
        }
        val allCardPli = (plisEW + plisNS + currentPli).toSortedMap().map { it.value }

       // val result = TODO("Test : call playersHaveColors and check rules R6")
        /* Resultat : : east and west have no trump */


        // val result = whatToPlay(myPosition, myCards, listBids, atout, onTable, plisNS, plisEW)
        // val result = IARun.enchere(myPosition, listBids, myCards, 0)
         val result = playersHaveColor(atout, atout, bid, allCardPli, myPosition, myCards)

        //same test but this time we are the partner we cannot use the same heuristics
        val result2 = playersHaveColor(atout, atout, bid, allCardPli, myPosition+2, myCards)



        /* you need to check result here */

        traceLevel = oldTraceLevel
        assert(!result[east]!! && result[north]!! && !result[west]!! && result[south]!!) { "$nameTest FAIL $result is not accurate" }
        assert(result2[east]!! && result2[north]!! && !result2[west]!! && result2[south]!!) { "$nameTest FAIL $result is not accurate" }
//assert(result.value == heart && result.color == king ) { "$nameTest FAIL $result is not accurate" }
//assert((result.curColor() == heart) && (result.curPoint() == 80)) { " FAIL  is not accurate" }

        // assert(TODO()) {"$nameTest FAIL : $result is not OK ")

        debugPrintln(dbgLevel.REGULAR, "$nameTest:PASS  $result for north \n and $result2 for south ")

    }

/*

Atout : H
Fonction : Belote001
Moi : e
Bid : H 80 s
Mon jeu : [ 8 D , 9 D ]
Pli  1 : [ s 11 H , w 8 H , n 7 H , e 10 H ]
Pli  2 : [ s 9 H , w 12 H , n 1 H , e 7 D ]
Table : [ e 13 D ]
Test : call playersHaveColors and check rules R7
Resultat : : n , s and east have no more trump ( H )



*/

    @Test
    fun testBelote001() {
        val nameTest = object {}.javaClass.enclosingMethod.name
        val oldTraceLevel = traceLevel
        val plisEW: MutableMap<Int, List<CardPlayed>> = mutableMapOf()
        val plisNS: MutableMap<Int, List<CardPlayed>> = mutableMapOf()
        var nb = 0


        val atout = heart
        val myPosition = east
        val bid = SimpleBid(heart, 80, south)
        val listBids: MutableList<Bid> = mutableListOf(bid, Pass(west), Pass(north), Pass(east), Pass(south))
        val myCards = mutableListOf(Card(eight, diamond), Card(nine, diamond))
        plisNS[nb++] =
                listOf(CardPlayed(Card(jack, heart), position = south), CardPlayed(Card(eight, heart), position = west),
                       CardPlayed(Card(seven, heart), position = north), CardPlayed(Card(ten, heart), position = east))
        plisNS[nb++] =
                listOf(CardPlayed(Card(nine, heart), position = south),
                       CardPlayed(Card(queen, heart), position = west, belote = BeloteValue.BELOTE),
                       CardPlayed(Card(ace, heart), position = north),
                       CardPlayed(Card(seven, diamond), position = east))
        val onTable: MutableList<CardPlayed> = mutableListOf(CardPlayed(Card(king, diamond), position = east))
        validateHand(myCards, bid = listBids.last { (it is SimpleBid) || (it is General) || (it is Capot) },
                     onTable = onTable)
        val nbPlis = plisEW.size + plisNS.size
        var currentPli: Map<Int, MutableList<CardPlayed>> = mapOf()
        if (onTable.isNotEmpty()) {
            currentPli = listOf(Pair(nbPlis, onTable)).toMap()
        }
        val allCardPli = (plisEW + plisNS + currentPli).toSortedMap().map { it.value }

        // val result = TODO("Test : call playersHaveColors and check rules R7")
        /* Resultat : :CardPlayed(, south and east have no more trump ( heart ) , position= north  ) */


        // val result = whatToPlay(myPosition, myCards, listBids, atout, onTable, plisNS, plisEW)
        // val result = IARun.enchere(myPosition, listBids, myCards, 0)
        val result = playersHaveColor(atout, atout, bid, allCardPli, myPosition, myCards)


        /* you need to check result here */

        traceLevel = oldTraceLevel
        assert((!result[east]!! && !result[north]!! && result[west]!! && !result[south]!!)) { "$nameTest FAIL $result is not accurate" }
//assert(result.value == heart && result.color == king ) { "$nameTest FAIL $result is not accurate" }
//assert((result.curColor() == heart) && (result.curPoint() == 80)) { " FAIL  is not accurate" }

        // assert(TODO()) {"$nameTest FAIL : $result is not OK ")

        debugPrintln(dbgLevel.REGULAR, "$nameTest:PASS  we play  :$result ")

    }


    /*
Atout : H
Fonction : SacharuleAtout7
Moi : s
Bid : H 110 n
Mon jeu : [ 1 D , 7 D , 7 C , 10 C ]
Pli 1 : [ e 8 C , s 13 C , w 13 H , n 12 C ]
Pli 2 : [ w 1 S , n 8 S , e 13 S , s 7 S ]
Pli 3 : [ w 9 S , n 12 S , e 10 S , s 8 H ]
Pli 4 : [ s 10 H , w 1 H , n 11 H , e 7 H ]
Table : [ n 1 C , e 9 C ]
Test : Atout , R7A
Resultat : e et w n’ont plus de H




*/

    @Test
    fun testRule7Sacha5() {
        val nameTest = object {}.javaClass.enclosingMethod.name
        val oldTraceLevel = traceLevel
        val plisEW: MutableMap<Int, List<CardPlayed>> = mutableMapOf()
        val plisNS: MutableMap<Int, List<CardPlayed>> = mutableMapOf()
        var nb = 0

        val atout = heart
        val myPosition = south
        val bid = SimpleBid(heart, 110, north)
        val listBids: MutableList<Bid> = mutableListOf(bid, Pass(west), Pass(north), Pass(east), Pass(south))
        val myCards = mutableListOf(Card(ace, diamond), Card(seven, diamond), Card(seven, club), Card(ten, club))
        plisNS[nb++] =
                listOf(CardPlayed(Card(eight, club), position = east), CardPlayed(Card(king, club), position = south),
                       CardPlayed(Card(king, heart), position = west), CardPlayed(Card(queen, club), position = north))
        plisNS[nb++] =
                listOf(CardPlayed(Card(ace, spade), position = west), CardPlayed(Card(eight, spade), position = north),
                       CardPlayed(Card(king, spade), position = east), CardPlayed(Card(seven, spade), position = south))
        plisNS[nb++] =
                listOf(CardPlayed(Card(nine, spade), position = west), CardPlayed(Card(queen, spade), position = north),
                       CardPlayed(Card(ten, spade), position = east), CardPlayed(Card(eight, heart), position = south))
        plisNS[nb++] =
                listOf(CardPlayed(Card(ten, heart), position = south), CardPlayed(Card(ace, heart), position = west),
                       CardPlayed(Card(jack, heart), position = north), CardPlayed(Card(seven, heart), position = east))
        val onTable: MutableList<CardPlayed> = mutableListOf(CardPlayed(Card(ace, club), position = north),
                                                             CardPlayed(Card(nine, club), position = east))
        validateHand(myCards, bid = listBids.last { (it is SimpleBid) || (it is General) || (it is Capot) },
                     onTable = onTable)
        val nbPlis = plisEW.size + plisNS.size
        var currentPli: Map<Int, MutableList<CardPlayed>> = mapOf()
        if (onTable.isNotEmpty()) {
            currentPli = listOf(Pair(nbPlis, onTable)).toMap()
        }
        val allCardPli = (plisEW + plisNS + currentPli).toSortedMap().map { it.value }

        /* Resultat : east et west n’ont plus de heart  */


        // val result = whatToPlay(myPosition, myCards, listBids, atout, onTable, plisNS, plisEW)
        // val result = IARun.enchere(myPosition, listBids, myCards, 0)
        val result = playersHaveColor(atout, atout, bid, allCardPli, myPosition, myCards)


        /* you need to check result here */

        traceLevel = oldTraceLevel
        assert(!result[east]!! && result[north]!! && !result[west]!! && !result[south]!!) { "$nameTest FAIL $result is not accurate" }
        //assert(result.value == heart && result.color == king ) { "$nameTest FAIL $result is not accurate" }
        //assert((result.curColor() == heart) && (result.curPoint() == 80)) { " FAIL  is not accurate" }


        debugPrintln(dbgLevel.REGULAR, "$nameTest:PASS  we play  :$result ")

    }


    /*

Atout : H
Fonction : DefausseReelle
Moi : n
Bid : H 100 n
Mon jeu : [ 10 C , 8 C , 9 C , 10 C , 7 S , 1 C , 12 S ]
Pli  1 : [ n 1 D , e 8 D , s 7 C , w 12 D ]
Pli  2 : [ n 11 H , e 10 H , s 9 H , w 13 H ]
Table : [ n 8 H , w 11 S ]
Test : call what to play check we don't play 10 of C
Resultat : : result should be different from 10 of C



*/

    @Test
    fun testDefausseReelle() {
        val nameTest = object {}.javaClass.enclosingMethod.name
        val oldTraceLevel = traceLevel
        val plisEW: MutableMap<Int, List<CardPlayed>> = mutableMapOf()
        val plisNS: MutableMap<Int, List<CardPlayed>> = mutableMapOf()
        var nb = 0


        val atout = heart
        val myPosition = north
        val bid = SimpleBid(heart, 100, south)
        val bid2 = SimpleBid(heart, 110, north)
        val listBids: MutableList<Bid> =
                mutableListOf(Pass(east), bid, Pass(west), bid2, Pass(east), Pass(south), Pass(west))
        val myCards =
                mutableListOf(Card(ten, club), Card(eight, club), Card(nine, club), Card(seven, spade), Card(ace, club),
                              Card(queen, spade))
        plisNS[nb++] = listOf(CardPlayed(Card(ace, diamond), position = north),
                              CardPlayed(Card(eight, diamond), position = east),
                              CardPlayed(Card(seven, club), position = south),
                              CardPlayed(Card(queen, diamond), position = west))
        plisNS[nb++] =
                listOf(CardPlayed(Card(jack, heart), position = north), CardPlayed(Card(ten, heart), position = east),
                       CardPlayed(Card(nine, heart), position = south), CardPlayed(Card(king, heart), position = west))
        val onTable: MutableList<CardPlayed> = mutableListOf(CardPlayed(Card(eight, heart), position = north),
                                                             CardPlayed(Card(jack, spade), position = west))
        validateHand(myCards, bid = listBids.last { (it is SimpleBid) || (it is General) || (it is Capot) },
                     onTable = onTable)
        val nbPlis = plisEW.size + plisNS.size
        var currentPli: Map<Int, MutableList<CardPlayed>> = mapOf()
        if (onTable.isNotEmpty()) {
            currentPli = listOf(Pair(nbPlis, onTable)).toMap()
        }
        val allCardPli = (plisEW + plisNS + currentPli).toSortedMap().map { it.value }

        // val result = TODO("Test : call what to play check we don't play ten of club ")
        /* Resultat : : result should be different from ten of club  */


        val result = whatToPlay(myPosition, myCards, listBids, atout, onTable, plisNS, plisEW)
        // val result = IARun.enchere(myPosition, listBids, myCards, 0)
        // val result = playersHaveColor(atout, atout, bid, allCardPli, myPosition, myCards)


        /* you need to check result here */

        traceLevel = oldTraceLevel
        //assert(result[east]!! && result[north]!! && result[west]!! && result[south]!! ) { "$nameTest FAIL $result is not accurate" }
        assert(result != null && result.value == seven && result.color == spade) { "$nameTest FAIL $result is not accurate" }
        //assert((result.curColor() == heart) && (result.curPoint() == 80)) { " FAIL  is not accurate" }

        //assert(TODO()) {"$nameTest FAIL : $result is not OK ")

        debugPrintln(dbgLevel.REGULAR, "$nameTest:PASS  we play  :$result ")

    }

}