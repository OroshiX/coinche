package fr.hornik.coinche.dto

import com.google.firebase.database.util.JsonMapper
import fr.hornik.coinche.model.*
import fr.hornik.coinche.model.values.PlayerPosition
import fr.hornik.coinche.model.values.TableState
import fr.hornik.coinche.serialization.JsonSerialize

data class Table(val id: String,
                 val nicknames: Nicknames,
                 val cards: List<Card>,
                 val onTable: List<CardPlayed>,
                 var state: TableState,
                 val nextPlayer: PlayerPosition,
                 val myPosition: PlayerPosition,
                 val bids: List<Bid>,
                 val currentBid: Bid = Pass(),
                 val score: Score,
                 val winnerLastTrick: PlayerPosition?,
                 val lastTrick: List<CardPlayed> = listOf()) {
    fun toFirebase(): Map<String, Any?> {
        return mapOf(
                "id" to id,
                "nicknames" to nicknames.toFirebase(),
                "cards" to cards,
                "onTable" to onTable,
                "state" to state.value,
                "nextPlayer" to nextPlayer.value,
                "myPosition" to myPosition.value,
                "bids" to bids.map { JsonMapper.parseJson(JsonSerialize.toJson(it)) },
                "currentBid" to JsonMapper.parseJson(JsonSerialize.toJson(currentBid)),
                "score" to score.toFirebase(),
                "winnerLastTrick" to winnerLastTrick?.value,
                "lastTrick" to lastTrick
        )
    }
}