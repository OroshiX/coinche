package fr.hornik.coinche

import fr.hornik.coinche.business.*
import fr.hornik.coinche.component.FireApp
import fr.hornik.coinche.exception.GameNotExistingException
import fr.hornik.coinche.exception.InvalidBidException
import fr.hornik.coinche.exception.NotYourTurnException
import fr.hornik.coinche.model.*
import fr.hornik.coinche.model.values.PlayerPosition
import fr.hornik.coinche.model.values.TableState
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class DataManagement(@Autowired private val fire: FireApp) {

    private final val sets: MutableList<SetOfGames> = mutableListOf()

    init {
        sets.addAll(fire.getAllGames())
    }

    companion object {
        const val COLLECTION_SETS = "sets"
        const val COLLECTION_PLAYERS = "players"
    }

    fun allMyGames(uid: String): List<Game> {
        fun SetOfGames.containsMe() = players.map { it.uid }.contains(uid)
        return sets.filter {
            !it.isFull() || it.containsMe()
        }.map {
            Game(it.id, it.players.size, it.players.first().nickname,
                 it.containsMe())
        }
    }

    /**
     * Create a new table with the info given, and save it to firebase,
     * and return a new table object
     */
    fun createGame(setOfGames: SetOfGames): SetOfGames {
        val id = fire.saveNewGame(setOfGames)
        val tableAdded = setOfGames.copy(id = id, lastModified = Date())
        sets.add(tableAdded)
        return tableAdded
    }

    /**
     * Precondition: the game is not full
     */
    fun joinGame(set: SetOfGames, user: User): Player {
        // Always called when it is not full
        val username = fire.getOrSetUsername(user)
        val player = set.addPlayer(user.uid, username)
        if (set.isFull()) {
            // Time to distribute
            distribute(set)
        }
        return player
    }

    private fun distribute(set: SetOfGames) {
        set.state = TableState.DISTRIBUTING
        // Random first player
        set.currentFirstPlayer = PlayerPosition.values().random()
        set.whoseTurn = set.currentFirstPlayer
        val dealer = set.currentFirstPlayer - 1

        // Distribute
        val hands =
                if (set.plisCampEW.isEmpty() && set.plisCampNS.isEmpty())
                    firstDealOfCards(dealer, allSpreads.random())
                else
                    dealCards(set.plisCampNS, set.plisCampEW, 10,
                              allSpreads.random(), dealer)
        set.players.first { it.position == PlayerPosition.NORTH }.cardsInHand =
                hands[0].toMutableList()
        set.players.first { it.position == PlayerPosition.EAST }.cardsInHand =
                hands[1].toMutableList()
        set.players.first { it.position == PlayerPosition.SOUTH }.cardsInHand =
                hands[2].toMutableList()
        set.players.first { it.position == PlayerPosition.WEST }.cardsInHand =
                hands[3].toMutableList()
        set.state = TableState.BIDDING
        fire.updateGame(set)
    }

    private fun scoreAndCleanupAfterGame(set: SetOfGames) {
        set.state = TableState.ENDED
        set.score += calculateScoreGame(set.plisCampNS, set.plisCampEW,
                                        set.whoWonLastTrick!!, set.currentBid)
        set.bids.clear()
        set.players.onEach { it.cardsInHand.clear() }
        set.whoWonLastTrick = null
        set.currentFirstPlayer = set.currentFirstPlayer + 1
//        set.onTable.clear()
        if (set.score.northSouth < 1000 && set.score.eastWest < 1000) {
            // Continue playing another game
            distribute(set)
        }
    }

    private fun getGame(setId: String): SetOfGames? =
            sets.firstOrNull { it.id == setId }

    fun getGameOrThrow(setId: String): SetOfGames =
            getGame(setId) ?: throw GameNotExistingException(setId)

    fun announceBid(gameId: String, bid: Bid, user: User) {
        val game = getGameOrThrow(gameId)
        if (game.state != TableState.BIDDING) throw IllegalStateException(
                "We are not in a bidding phase")
        val me = game.players.first { player -> player.uid == user.uid }
        if (game.whoseTurn != me.position) throw NotYourTurnException()
        if (!isValidBid(game.bids, bid)) throw InvalidBidException(bid)
        game.bids.add(bid)
        fire.updateGame(game)
    }
}