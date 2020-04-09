package fr.hornik.coinche.component

import fr.hornik.coinche.business.*
import fr.hornik.coinche.dto.Game
import fr.hornik.coinche.exception.*
import fr.hornik.coinche.model.*
import fr.hornik.coinche.model.values.BeloteValue
import fr.hornik.coinche.model.values.PlayerPosition
import fr.hornik.coinche.model.values.TableState
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class DataManagement(@Autowired private val fire: FireApp) {

    private final val sets: MutableList<SetOfGames> = mutableListOf()

    init {
        sets.addAll(fire.getAllGames())
    }

    fun allMyGames(uid: String): List<Game> {
        fun SetOfGames.containsMe() =
                players.asSequence().map { it.uid }.contains(uid)
        return sets.filter {
            !it.isFull() || it.containsMe()
        }.map {
            Game(it.id,
                 it.players.size,
                 it.name,
                 it.players.first().nickname,
                 it.containsMe())
        }
    }

    /**
     * Create a new table with the info given, and save it to firebase,
     * and return a new table object
     */
    fun createGame(setOfGames: SetOfGames, user: User): SetOfGames {
        fire.saveGame(setOfGames, true)
        sets.add(setOfGames)
        return setOfGames
    }

    /**
     * Precondition: the game is not full
     */
    fun joinGame(set: SetOfGames,
                 user: User, nickname: String?): Player {
        // Always called when it is not full
        nickname?.let {
            if (it.isNotBlank())
                fire.setNewUsername(user.apply { this.nickname = it })
        }
        val player = set.addPlayer(user.uid, user.nickname)
        if (set.isFull()) {
            // Time to distribute
            distribute(set)
        }
        fire.saveGame(set)
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
//        fire.saveGame(set)
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
        fire.saveGame(set)
    }

    private fun getGame(setId: String): SetOfGames? =
            sets.firstOrNull { it.id == setId }

    fun getGameOrThrow(setId: String): SetOfGames =
            getGame(setId) ?: throw GameNotExistingException(setId)

    fun announceBid(game: SetOfGames, bid: Bid, user: User) {
        if (game.state != TableState.BIDDING) throw NotValidStateException(
                game.state, TableState.BIDDING)
        val me = game.players.first { player -> player.uid == user.uid }
        if (game.whoseTurn != me.position) throw NotYourTurnException()
        if (!isValidBid(game.bids, bid)) throw InvalidBidException(bid)
        game.bids.add(bid)
        if(isLastBid(game.bids)) {
            // change status to playing
            game.state = TableState.PLAYING
            game.whoseTurn = game.currentFirstPlayer
            game.currentBid = getCurrentBid(game.bids)
        } else {
            // Next player
            game.whoseTurn += 1
        }
    }

    fun changeNickname(set: SetOfGames, user: User) {
        set.players.first { it.uid == user.uid }.nickname = user.nickname
        fire.saveGame(set)
    }

    fun refresh() {
        sets.clear()
        sets.addAll(fire.getAllGames())
    }

    fun playCard(game: SetOfGames, card: Card, user: User,
                 beloteValue: BeloteValue = BeloteValue.NONE) {
        if (!isValidCard(
                    myCardsInHand = game.players.first { it.uid == user.uid }.cardsInHand,
                    bid = game.currentBid,
                    cardsOnTable = game.onTable,
                    theCardToCheck = card)) {
            throw NotAuthorizedOperation("The card $card is not valid")
        }
        game.onTable.add(CardPlayed(card, beloteValue,
                                    game.players.first { it.uid == user.uid }.position))
        // TODO check if on table is full
        // TODO Save to firebase
        // TODO clear table at a good time
    }
}