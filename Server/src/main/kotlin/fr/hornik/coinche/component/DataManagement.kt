package fr.hornik.coinche.component

import fr.hornik.coinche.business.*
import fr.hornik.coinche.dto.Game
import fr.hornik.coinche.exception.*
import fr.hornik.coinche.model.*
import fr.hornik.coinche.model.values.BeloteValue
import fr.hornik.coinche.model.values.CardColor
import fr.hornik.coinche.model.values.PlayerPosition
import fr.hornik.coinche.model.values.TableState
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class DataManagement(@Autowired private val fire: FireApp) {
    private final val sets: MutableList<SetOfGames> = mutableListOf()

    /*
        for debugging reason it can be useful to comment previous line and uncomment these 3 lines
        companion object {

            final val sets: MutableList<SetOfGames> = mutableListOf()
        }
    */
    val AUTOMATEDGAMESID = "AUTOMATED"
    val AUTOMATEDPLAYERSID = "PLAYERSPLAYERS47"

    val timeoutTask = object: TimerTask() {
        var timesRan = 0
        override fun run() = reviewTimer("Timeout timer passed ${++timesRan} time(s)")

    }

    val IAPlayerTask = object: TimerTask() {
        var timesRanIA = 0
        override fun run() = IAPlayers("IA timer passed ${++timesRanIA} time(s)")

    }

    fun IAPlayers (Message: String) {
        val millis = System.currentTimeMillis()
        for (setOfGames in sets.filter{e -> (e.name.contains(AUTOMATEDGAMESID))}) {

            //println(".\n")
            if (! setOfGames.players.filter{ e -> ((e.uid.contains(AUTOMATEDPLAYERSID)) && (setOfGames.whoseTurn == e.position))}.isEmpty() ) {
                IARun(setOfGames).run(this)
                fire.saveGame(setOfGames)

            }
        }

    }
    fun reviewTimer(Message:String) {
        //println("$Message\n")
        val millis = System.currentTimeMillis()
        var action = false
        for (setOfGames in sets) {
            // for testing purpose we delete only the games which name is TOBEDELETED
            when (setOfGames.state) {
                TableState.JOINING -> if ((millis - setOfGames.whoseTurnTimeLastChg) > setOfGames.preferences.JoiningMaxTime) {
                    // TODO we should remove the set ?
                    if (setOfGames.name.contains("TOBEDELETED")) {
                        println("${setOfGames.name}_${setOfGames.state} : JOINING TIMEOUT (id:${setOfGames.id})\n")
                        fire.deleteGame(setOfGames)
                        action = true
                    }
                    if ((setOfGames.name.contains(AUTOMATEDGAMESID)) && (setOfGames.players.size < 4 )){
                        //The names means that we can add an automatic player ( probably will be done later through a preference
                        val v:Int = Random().nextInt()
                        val w:Int = Random().nextInt()

                        joinGame(setOfGames, User("${w}$AUTOMATEDPLAYERSID${v}", "AUTOMATED PLAYER ${v%10}"),"AUTOMATED PLAYER ${v%10}")
                        setOfGames.whoseTurnTimeLastChg = millis
                    }
                }
                TableState.BIDDING -> if ((millis - setOfGames.whoseTurnTimeLastChg) > setOfGames.preferences.BiddingMaxTime) {
                    // TODO we should remove the set ?
                    if (setOfGames.name.contains("TOBEDELETED")) {
                        println("${setOfGames.name}_${setOfGames.state} : BIDDING TIMEOUT (id:${setOfGames.id})\n")
                        fire.deleteGame(setOfGames)
                        action = true
                    }
                }
                else -> {}
            }
        }
        if (action) refresh()

    }
    val timer = java.util.Timer()
    init {
        sets.addAll(fire.getAllGames())

        timer.schedule(timeoutTask,0,1000)
        timer.schedule(IAPlayerTask,0,2501)

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
        // somebody did join the game - we reset the timer.
        setOfGames.whoseTurnTimeLastChg = System.currentTimeMillis()
        val player = setOfGames.addPlayer(user.uid, user.nickname)
        if (setOfGames.isFull()) {
            // Time to distribute
            distribute(setOfGames)
        }
        fire.saveGame(setOfGames)
        return player
    }

    private fun distribute(setOfGames: SetOfGames) {
        val firstDeal = setOfGames.plisCampEW.isEmpty() && setOfGames.plisCampNS.isEmpty()
        setOfGames.state = TableState.DISTRIBUTING
        // Random first player
        if (firstDeal) {
            setOfGames.currentFirstPlayer = PlayerPosition.values().random()
        } else {
            setOfGames.currentFirstPlayer += 1
        }

        setOfGames.whoseTurn = setOfGames.currentFirstPlayer
        val dealer = setOfGames.currentFirstPlayer - 1

        // Distribute
        val hands =
                if (firstDeal)
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
            setOfGames.plisCampEW.clear()
            setOfGames.plisCampNS.clear()
        }
    }

    private fun getGame(setId: String): SetOfGames? =
            sets.firstOrNull { it.id == setId }

    fun getGameOrThrow(setId: String): SetOfGames =
            getGame(setId) ?: throw GameNotExistingException(setId)

    fun deleteGame(setOfGames: SetOfGames, user: User):Boolean {
        val me = setOfGames.players.first { player -> player.uid == user.uid }
        if (me != null ) {
            fire.deleteGame(setOfGames)
            refresh()
            return true
        }
        return false
    }
        fun announceBid(setOfGames: SetOfGames, bid: Bid, user: User) {
        if (setOfGames.state != TableState.BIDDING) throw NotValidStateException(
                setOfGames.state, TableState.BIDDING)
        val me = setOfGames.players.first { player -> player.uid == user.uid }
        if (setOfGames.whoseTurn != me.position) throw NotYourTurnException()
        // Bid needs to be valid and bid.position needs to be me !!!!
        if ((!isValidBid(setOfGames.bids, bid)) || (bid.position != me.position)) throw InvalidBidException(bid)

        setOfGames.bids.add(bid)
        setOfGames.whoseTurnTimeLastChg = System.currentTimeMillis()

        if (isLastBid(setOfGames.bids)) {
            // change status to playing
            setOfGames.state = TableState.PLAYING
            setOfGames.whoseTurn = setOfGames.currentFirstPlayer
            setOfGames.currentBid = getCurrentBid(setOfGames.bids)
            val nextPlayer: Player = setOfGames.players.first { player -> player.position == setOfGames.whoseTurn }
            for (card in nextPlayer.cardsInHand) {
                card.playable = true
            }
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
        // check if on table is full

        if (setOfGames.onTable.size == 4) {
            // the cards on the table are from last trick, we can clear them
            setOfGames.onTable.clear()
        }

        // get the real object in hand
        if (!isValidCard(
                        myCardsInHand = setOfGames.players.first { it.uid == user.uid }.cardsInHand,
                        bid = setOfGames.currentBid,
                        cardsOnTable = setOfGames.onTable,
                        theCardToCheck = card)) {
            throw NotAuthorizedOperation("The card $card is not valid")
        }



        setOfGames.onTable.add(CardPlayed(card, beloteValue,
                setOfGames.players.first { it.uid == user.uid }.position))
        setOfGames.players.first { it.uid == user.uid }.cardsInHand.removeIf { it.isSimilar(card) }
        // there was some action on the table - we reset the timer.
        setOfGames.whoseTurnTimeLastChg = System.currentTimeMillis()

        setOfGames.whoseTurn += 1
        // Evaluation of who win the trick
        if (setOfGames.onTable.size == 4) {
            val winner = calculateWinnerTrick(setOfGames.onTable, getCurrentBid(setOfGames.bids))
            setOfGames.whoWonLastTrick = winner

            // This function set the whoseturn field to winner
            setOfGames.winTrick(winner)
            if (setOfGames.players.first().cardsInHand.isEmpty()) {
                // no more cards in hand - last turn

                scoreAndCleanupAfterGame(setOfGames)
            } else {
                // Still cards in hand, but first card of trick : all cards are playable
                for (mcard in setOfGames.nextPlayer().cardsInHand) {
                    mcard.playable = true
                }
            }
        } else {
            val valid = allValidCardsToPlay(setOfGames.nextPlayer().cardsInHand, setOfGames.currentBid, setOfGames.onTable)

            // still cards to play, need to set playable to the right value for next player
            for (mcard in setOfGames.nextPlayer().cardsInHand) {
                mcard.playable = valid.contains(mcard)
            }

        }
        // Save to firebase

        fire.saveGame(setOfGames)

    }
}