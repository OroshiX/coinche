package fr.hornik.coinche

import fr.hornik.coinche.model.*
import fr.hornik.coinche.model.values.CardColor
import fr.hornik.coinche.model.values.PlayerPosition
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CalculusTest {
    private lateinit var bids: List<Bid>

    @BeforeEach
    fun initBids() {
        bids = listOf(
                SimpleBid(CardColor.HEART, 80, PlayerPosition.NORTH),
                Pass,
                Capot(CardColor.SPADE, PlayerPosition.NORTH, true),
                General(CardColor.DIAMOND, PlayerPosition.SOUTH, false),
                Coinche(SimpleBid(CardColor.CLUB, 90, PlayerPosition.WEST), true, PlayerPosition.NORTH))
    }

    @Test
    fun testBid() {
        for (bid in bids) {
            println(bid)
        }
    }
}