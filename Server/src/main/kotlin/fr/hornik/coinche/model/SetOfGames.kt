package fr.hornik.coinche.model

import com.fasterxml.jackson.annotation.JsonIgnore
import fr.hornik.coinche.dto.Table
import fr.hornik.coinche.model.values.PlayerPosition
import fr.hornik.coinche.model.values.TableState
import java.util.*

data class SetOfGames(var currentBid: Bid = Pass(),
                      var id: String = "",
                      var score: Score = Score(0, 0),
                      var whoWonLastTrick: PlayerPosition? = null,
                      val plisCampNS: MutableList<List<CardPlayed>> = mutableListOf(),
                      val plisCampEW: MutableList<List<CardPlayed>> = mutableListOf(),
                      val bids: MutableList<Bid> = mutableListOf(),
                      var whoseTurn: PlayerPosition = PlayerPosition.NORTH,
                      val onTable: MutableList<CardPlayed> = mutableListOf(),
                      var state: TableState = TableState.JOINING,
                      var currentFirstPlayer: PlayerPosition = PlayerPosition.NORTH,
                      val players: MutableList<Player> = mutableListOf(),
                      var lastModified: Date = Date()) {

    constructor(user: User) : this() {
        players.add(Player(user.uid, PlayerPosition.NORTH, user.nickname))
    }

    @JsonIgnore
    fun isFull(): Boolean {
        return players.size >= 4
    }

    fun toTable(uidUser: String): Table {
        val me = players.first { it.uid == uidUser }
        return Table(
                id = id,
                score = score,
                winnerLastTrick = whoWonLastTrick,
                lastTrick = whoWonLastTrick?.let {
                    if (state == TableState.PLAYING) {
                        return@let when (it) {
                            PlayerPosition.NORTH, PlayerPosition.SOUTH -> plisCampNS.last()
                            PlayerPosition.WEST, PlayerPosition.EAST   -> plisCampEW.last()
                        }
                    }
                    return@let listOf<CardPlayed>()
                } ?: listOf(),
                myPosition = me.position,
                nicknames = Nicknames(players),
                state = state,
                played = onTable,
                nextPlayer = whoseTurn,
                bids = bids,
                currentBid = currentBid,
                onTable = me.cardsInHand
        )
    }

    /**
     * Return true if succeeded in winning the trick, else false
     */
    fun winTrick(who: PlayerPosition): Boolean {
        if (state != TableState.PLAYING) return false
        if (onTable.size != 4) return false

        when (who) {
            PlayerPosition.NORTH, PlayerPosition.SOUTH ->
                plisCampNS.add(onTable.toList())
            PlayerPosition.EAST, PlayerPosition.WEST   ->
                plisCampEW.add(onTable.toList())
        }
        whoWonLastTrick = who
        onTable.clear()
        return true
    }

    private fun getNextPlayerToAddPosition(): PlayerPosition {
        var positionOfPlayer: PlayerPosition = PlayerPosition.NORTH
        for (position in PlayerPosition.values()) {
            if (!players.map { player -> player.position }.contains(position)) {
                positionOfPlayer = position
                break
            }
        }
        return positionOfPlayer
    }

    fun addPlayer(uidPlayer: String, username: String): Player {
        val player = Player(uidPlayer, getNextPlayerToAddPosition(), username)
        players.add(player)
        return player
    }

}