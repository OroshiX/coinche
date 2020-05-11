package fr.hornik.coinche.model

import com.fasterxml.jackson.annotation.JsonIgnore
import fr.hornik.coinche.dto.Table
import fr.hornik.coinche.model.values.PlayerPosition
import fr.hornik.coinche.model.values.PrefsScore
import fr.hornik.coinche.model.values.TableState
import java.util.*

data class SetOfGames(
                      // currentBid = the Bid we are playing during TableState.PLAYING
        var currentBid: Bid = Pass(),

                      // Unique id identifying the current set of Game
        var id: String = "",

                      // Table Name
        var name: String = "",

                      // Current Score
        var score: Score = Score(0, 0),

                      // Player who won the last Trick, will be the first to play this game
        var whoWonLastTrick: PlayerPosition? = null,

                      // List of "plis" done by each camp
        val plisCampNS: MutableMap<Int,List<CardPlayed>> = mutableMapOf(),
        val plisCampEW: MutableMap<Int,List<CardPlayed>> = mutableMapOf(),

                      // History of bid of the current game
        val bids: MutableList<Bid> = mutableListOf(),

                      // the one we are expecting an action ( playing or bidding )
        var whoseTurn: PlayerPosition = PlayerPosition.NORTH,

                      // Date in seconds for last action of whose turn
                      // This variable should be set to current millis every time whoseturn do something
        var whoseTurnTimeLastChg: Long = System.currentTimeMillis(),


                      // Preferences for this game ( should be preferences for all games ? )
        @JsonIgnore
        val preferences: Preferences = Preferences(PrefsScore.POINTSANNOUNCED,JoiningMaxTime = 15000, BiddingMaxTime = 120000),

                      // list of cards present on the table
        val onTable: MutableList<CardPlayed> = mutableListOf(),

        var state: TableState = TableState.JOINING,

                      // The player who did start to play during the current game.
        var currentFirstPlayer: PlayerPosition = PlayerPosition.NORTH,

                      // list of players
        val players: MutableList<Player> = mutableListOf(),

                      // when is it updated ? at creation only or on any change of the table ?
                      // currently only at creation
        var lastModified: Date = Date()) {

    constructor(user: User, name: String) : this(name = name) {
        players.add(Player(user.uid, PlayerPosition.NORTH, user.nickname))
    }

    @JsonIgnore
    fun isFull(): Boolean {
        return players.size >= 4
    }

    fun nextPlayer():Player {
        return players.first {player -> player.position == whoseTurn}
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
                            PlayerPosition.NORTH, PlayerPosition.SOUTH -> plisCampNS[plisCampNS.toSortedMap().lastKey()]
                            PlayerPosition.WEST, PlayerPosition.EAST   -> plisCampEW[plisCampEW.toSortedMap().lastKey()]
                        }
                    }
                    return@let listOf<CardPlayed>()
                } ?: listOf(),
                myPosition = me.position,
                nicknames = Nicknames(players),
                state = state,
                onTable = onTable,
                nextPlayer = whoseTurn,
                bids = bids,
                currentBid = currentBid,
                cards = me.cardsInHand
        )
    }

    @JsonIgnore
    fun getAllTricks(): Map<Int, List<CardPlayed>> {

        return  (plisCampNS + plisCampEW).toSortedMap()

    }

    @JsonIgnore
    fun getLastTrick() : List<CardPlayed> {
        // TODO I suppose it would make sense to write this type of function here - currently implemented in gamecontroller
        return listOf()
    }
    /**
     * Return true if succeeded in winning the trick, else false
     */
    fun winTrick(who: PlayerPosition): Boolean {
        if (state != TableState.PLAYING) return false
        if (onTable.size != 4) return false

        // for IA, it's important to be able to find the order of the trick, hence we use the key of the map pliCamp...
        // to remember the order (pliCampsNS[3] = Null if NS did not made the 4th trick, or equals the list of cards done ar 4th trick
        val nbPlisDone = plisCampNS.size + plisCampEW.size
        when (who) {
            PlayerPosition.NORTH, PlayerPosition.SOUTH -> {
                plisCampNS[nbPlisDone]=onTable.toList()
            }
            PlayerPosition.EAST, PlayerPosition.WEST   -> {
                plisCampEW[nbPlisDone]=onTable.toList()
            }
        }
        whoseTurn = who
        whoWonLastTrick = who
        // The clear of table is done when playing the first card of next tour
        // onTable.clear()
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