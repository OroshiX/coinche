package fr.hornik.coinche

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import fr.hornik.coinche.model.*
import fr.hornik.coinche.model.values.CardColor
import fr.hornik.coinche.model.values.PlayerPosition
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
}