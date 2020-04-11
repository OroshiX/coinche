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
            Game(it, it.containsMe())
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
    fun joinGame(setOfGames: SetOfGames,
                 user: User, nickname: String?): Player {
        // Always called when it is not full
        nickname?.let {
            if (it.isNotBlank())
                fire.setNewUsername(user.apply { this.nickname = it })
        }
        val player = setOfGames.addPlayer(user.uid, user.nickname)
        if (setOfGames.isFull()) {
            // Time to distribute
            distribute(setOfGames)
        }
        fire.saveGame(setOfGames)
        return player
    }

    private fun distribute(setOfGames: SetOfGames) {
        setOfGames.state = TableState.DISTRIBUTING
        // Random first player
        setOfGames.currentFirstPlayer = PlayerPosition.values().random()
        setOfGames.whoseTurn = setOfGames.currentFirstPlayer
        val dealer = setOfGames.currentFirstPlayer - 1

        // Distribute
        val hands =
                if (setOfGames.plisCampEW.isEmpty() && setOfGames.plisCampNS.isEmpty())
                    firstDealOfCards(dealer, allSpreads.random())
                else
                    dealCards(setOfGames.plisCampNS, setOfGames.plisCampEW, 10,
                              allSpreads.random(), dealer)
        setOfGames.players.first { it.position == PlayerPosition.NORTH }.cardsInHand =
                hands[0].toMutableList()
        setOfGames.players.first { it.position == PlayerPosition.EAST }.cardsInHand =
                hands[1].toMutableList()
        setOfGames.players.first { it.position == PlayerPosition.SOUTH }.cardsInHand =
                hands[2].toMutableList()
        setOfGames.players.first { it.position == PlayerPosition.WEST }.cardsInHand =
                hands[3].toMutableList()
        setOfGames.state = TableState.BIDDING
//        fire.saveGame(set)
    }

    private fun scoreAndCleanupAfterGame(setOfGames: SetOfGames) {
        setOfGames.state = TableState.ENDED
        setOfGames.score += calculateScoreGame(setOfGames.plisCampNS, setOfGames.plisCampEW,
                                        setOfGames.whoWonLastTrick!!, setOfGames.currentBid)
        setOfGames.bids.clear()
        setOfGames.players.onEach { it.cardsInHand.clear() }
        setOfGames.whoWonLastTrick = null
        setOfGames.currentFirstPlayer = setOfGames.currentFirstPlayer + 1
//        set.onTable.clear()
        if (setOfGames.score.northSouth < 1000 && setOfGames.score.eastWest < 1000) {
            // Continue playing another game
            distribute(setOfGames)
        }
        fire.saveGame(setOfGames)
    }

    private fun getGame(setId: String): SetOfGames? =
            sets.firstOrNull { it.id == setId }

    fun getGameOrThrow(setId: String): SetOfGames =
            getGame(setId) ?: throw GameNotExistingException(setId)

    fun announceBid(setOfGames: SetOfGames, bid: Bid, user: User) {
        if (setOfGames.state != TableState.BIDDING) throw NotValidStateException(
                setOfGames.state, TableState.BIDDING)
        val me = setOfGames.players.first { player -> player.uid == user.uid }
        if (setOfGames.whoseTurn != me.position) throw NotYourTurnException()
        if (!isValidBid(setOfGames.bids, bid)) throw InvalidBidException(bid)
        setOfGames.bids.add(bid)
        if (isLastBid(setOfGames.bids)) {
            // change status to playing
            setOfGames.state = TableState.PLAYING
            setOfGames.whoseTurn = setOfGames.currentFirstPlayer
            setOfGames.currentBid = getCurrentBid(setOfGames.bids)
        } else {
            // Next player
            setOfGames.whoseTurn += 1
        }
    }

    fun changeNickname(setOfGames: SetOfGames, user: User) {
        setOfGames.players.first { it.uid == user.uid }.nickname = user.nickname
        fire.saveGame(setOfGames)
    }

    fun refresh() {
        sets.clear()
        sets.addAll(fire.getAllGames())
    }

    fun playCard(setOfGames: SetOfGames, card: Card, user: User,
                 beloteValue: BeloteValue = BeloteValue.NONE) {
        if (!isValidCard(
                    myCardsInHand = setOfGames.players.first { it.uid == user.uid }.cardsInHand,
                    bid = setOfGames.currentBid,
                    cardsOnTable = setOfGames.onTable,
                    theCardToCheck = card)) {
            throw NotAuthorizedOperation("The card $card is not valid")
        }

        // check if on table is full

        if (setOfGames.onTable.size == 4) {
            // the cards on the table are from last trick, we can clear them
            setOfGames.onTable.clear()
        }

        setOfGames.onTable.add(CardPlayed(card, beloteValue,
                                    setOfGames.players.first { it.uid == user.uid }.position))
        setOfGames.players.first{it.uid == user.uid}.cardsInHand.remove(card)


        // Evaluation of who win the trick
        if (setOfGames.onTable.size == 4 ) {
            val winner = calculateWinnerTrick(setOfGames.onTable, getCurrentBid(setOfGames.bids))
            setOfGames.winTrick(winner)
            if (setOfGames.players.size == 0) {
                // We could calculate the score - no cards remain in hands
                // but probably we wont call scoreAndCleanupAfterGame from here ????
                // if we do, we will have to remove the save in firebase which is already don e in the function.
            }

        }
        // Save to firebase

        fire.saveGame(setOfGames, true)

    }
}