package fr.hornik.coinche.model

import fr.hornik.coinche.model.values.PlayerPosition
import fr.hornik.coinche.model.values.TableState
import java.util.*

data class SetOfGames(var currentBid: Bid = Pass(),
                      var id: String = "",
                      var score: Score = Score(0, 0),
                      val plisCampNS: List<List<CardPlayed>> = mutableListOf(),
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

    fun isFull(): Boolean {
        return players.size >= 4
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