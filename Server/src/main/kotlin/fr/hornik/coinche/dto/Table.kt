package fr.hornik.coinche.dto

import fr.hornik.coinche.model.*
import fr.hornik.coinche.model.values.PlayerPosition
import fr.hornik.coinche.model.values.TableState
import fr.hornik.coinche.serialization.JsonSerialize

data class Table(val id: String,
                 val nicknames: Nicknames,
                 val onTable: List<Card>,
                 val played: List<CardPlayed>,
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
                "onTable" to onTable,
                "played" to played,
                "state" to state.value,
                "nextPlayer" to nextPlayer.value,
                "myPosition" to myPosition.value,
                "bids" to bids.map { JsonSerialize.toJson(it) },
                "currentBid" to JsonSerialize.toJson(currentBid),
                "score" to score.toFirebase(),
                "winnerLastTrick" to winnerLastTrick?.value,
                "lastTrick" to lastTrick
        )
    }
}