package fr.hornik.coinche

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import fr.hornik.coinche.dto.Table
import fr.hornik.coinche.model.*
import fr.hornik.coinche.model.values.*
import fr.hornik.coinche.serialization.JsonSerialize
import org.junit.jupiter.api.Test

class SerializationTest {

    @Test
    fun serialize() {
        val mapper = ObjectMapper()

        val bid = SimpleBid(CardColor.SPADE, 80, PlayerPosition.EAST)
        val st = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(bid)
        println(st)
        println(JsonSerialize.toJson(General()))
        println(JsonSerialize.toJson(Capot()))
        println(JsonSerialize.toJson(Pass()))
        println(JsonSerialize.toJson(Coinche()))
        val deser = mapper.readValue<Bid>(st)
        println(bid)
        println(deser)
//        assert(container == containerDeserialize)
        println(mapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(deser))
        val setOfGames = SetOfGames()
        setOfGames.currentBid = bid
        print(JsonSerialize.toJson(setOfGames))
    }

    @Test
    fun serializeCard() {
        val mapper = ObjectMapper()
        val card = Card(CardValue.KING, CardColor.CLUB, true)
        val st =
                mapper.writerWithDefaultPrettyPrinter().writeValueAsString(card)
        println(st)

        println(JsonSerialize.toJson(card))
        val deserialize = JsonSerialize.fromJson<Card>(st)
        print(deserialize)
    }

    @Test
    fun serializeTable() {
        val table = Table(
                id = "toto",
                bids = listOf(),
                cards = listOf(Card()),
                onTable = listOf(CardPlayed(Card(), BeloteValue.BELOTE, PlayerPosition.SOUTH)),
                currentBid = SimpleBid(),
                lastTrick = listOf(CardPlayed()),
                myPosition = PlayerPosition.NORTH,
                nextPlayer = PlayerPosition.EAST,
                nicknames = Nicknames("toto", "tat", "titi", "tutu"),
                score = Score(),
                state = TableState.PLAYING,
                winnerLastTrick = null
        )
        println(table)
        val map = table.toFirebase();

        println(map)
    }
}