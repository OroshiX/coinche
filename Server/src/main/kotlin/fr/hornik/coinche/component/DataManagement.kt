package fr.hornik.coinche.component

import fr.hornik.coinche.business.*
import fr.hornik.coinche.dto.Game
import fr.hornik.coinche.exception.*
import fr.hornik.coinche.model.*
import fr.hornik.coinche.model.values.BeloteValue
import fr.hornik.coinche.model.values.PlayerPosition
import fr.hornik.coinche.model.values.TableState
import fr.hornik.coinche.util.dbgLevel
import fr.hornik.coinche.util.debugPrintln
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class DataManagement(@Autowired private val fire: FireApp) {
    final val sets: MutableList<SetOfGames> = mutableListOf()

    /*
        for debugging reason it can be useful to comment previous line and uncomment these 3 lines
        companion object {

            final val sets: MutableList<SetOfGames> = mutableListOf()
        }
    */
    companion object {
        const val AUTOMATEDGAMESID = "AUTOMATED"
        const val AUTOMATEDPLAYERSID = "PLAYERSPLAYERS47"

        // This variable permits to do some testing without saving data on the server
        // this type of testing is very limited , you cannot interact with a third party client
        // reserved for server internal testing

        // warning this variable does not prevent to log / or set a new nickname,
        // does not prevent to play or to join a game.
        // it just prevent to save on firebase, and to inform other applications

        // typically used when the automated players have an issue  / or to test a 4 automated player table

        // you should keep its value to true for normal behaviour

        const val productionAction = true

    }

    private final val timeoutTask = object : TimerTask() {
        var timesRan = 0
        override fun run() = reviewTimer("Timeout timer passed ${++timesRan} time(s)")

    }

    fun reviewTimer(Message: String) {
        debugPrintln(dbgLevel.FUNCTION, "$Message\n")
        val millis = System.currentTimeMillis()
        var action = false
        for (setOfGames in sets) {
            if (setOfGames.name.contains(AUTOMATEDGAMESID)) {
                //The names means that we can add an automatic player ( probably will be done later through a preference

                if (IARun(setOfGames).run(this, millis)) {
                    // in most case you dont need to save since announce/joining/bidding do the saving ... but IF we have to save something IARUN returns true
                    fire.saveGame(setOfGames)
                }
            }
            // TODO this sequence treat the timeout case like late bidding / leaving etc ...
            when (setOfGames.state) {
                TableState.JOINING -> if ((millis - setOfGames.whoseTurnTimeLastChg) > setOfGames.preferences.JoiningMaxTime) {
                    // TODO What if it is not an automated game ?
                }
                TableState.BIDDING -> if ((millis - setOfGames.whoseTurnTimeLastChg) > setOfGames.preferences.BiddingMaxTime) {
                    // TODO Player took too much time to bet .... should we do something ?
                }
                TableState.BETWEEN_GAMES -> if ((millis - setOfGames.whoseTurnTimeLastChg) > setOfGames.preferences.betweenGameMaxTime) {

                    //Going back to BIDDING
                    debugPrintln(dbgLevel.DEBUG, "${setOfGames.name} was between GAME")
                    if (setOfGames.score.northSouth < 1000 && setOfGames.score.eastWest < 1000) {
                        // Continue playing another game
                        setOfGames.whoWonLastTrick = null
                        distribute(setOfGames)
                        setOfGames.plisCampEW.clear()
                        setOfGames.plisCampNS.clear()


                    } else {
                        setOfGames.state = TableState.ENDED
                    }
                    setOfGames.whoseTurnTimeLastChg = millis
                    setOfGames.onTable.clear()
                    fire.saveGame(setOfGames)
                }
                else -> {
                }
            }
            // TODO Currently Action is never true - will be true if we delete a game on timer ....
            if (action) refresh()
        }

    }

    final val timer = java.util.Timer()

    init {
        sets.addAll(fire.getAllGames())
        // This is to disable automatic games in test environement and not mess up with prod DB due to conflict with automatic players
        if (productionAction)
            timer.schedule(timeoutTask, 0, 1000)

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

    fun updateStat(setOfGames: SetOfGames) {
        fire.saveStatistics(setOfGames)
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
                    dealCards(setOfGames.plisCampNS.map { it.value }, setOfGames.plisCampEW.map { it.value }, 10,
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
    }

    private fun scoreAndCleanupAfterGame(setOfGames: SetOfGames) {

        setOfGames.score += calculateScoreGame(setOfGames.plisCampNS.map { it.value }, setOfGames.plisCampEW.map { it.value },
                setOfGames.whoWonLastTrick!!, setOfGames.currentBid)
        setOfGames.bids.clear()
        setOfGames.currentBid = Pass()
        setOfGames.players.onEach { it.cardsInHand.clear() }

        setOfGames.whoseTurnTimeLastChg = System.currentTimeMillis()

        setOfGames.state = TableState.BETWEEN_GAMES

    }

    private fun getGame(setId: String): SetOfGames? =
            sets.firstOrNull { it.id == setId }

    fun getGameOrThrow(setId: String): SetOfGames =
            getGame(setId) ?: throw GameNotExistingException(setId)

    fun deleteGame(setOfGames: SetOfGames, user: User): Boolean {
        val me = setOfGames.players.firstOrNull { player -> player.uid == user.uid }
        if (me != null) {
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


        val iaBid = IARun.enchere(me.position, setOfGames.bids, setOfGames.players.first { it.position == setOfGames.whoseTurn }.cardsInHand, 0)
        if ((iaBid.curColor() != bid.curColor()) || (iaBid.curPoint() != bid.curPoint())) {
            debugPrintln(dbgLevel.DEBUG, "****************Player ${setOfGames.whoseTurn} did bid $bid I'd prefer to bid $iaBid ")
        }

        // We dont expect the client application to fill correctly the annonce we are coinch'ing
        // and we can compute it here so .... annonce is not checked in the function isValidBid .... so we can do it either before or after.
        if (bid is Coinche) {
            setOfGames.bids.add(Coinche(annonce = getCurrentBid(setOfGames.bids), position = bid.position, surcoinche = bid.surcoinche))
        } else {
            setOfGames.bids.add(bid)
        }
        setOfGames.whoseTurnTimeLastChg = System.currentTimeMillis()
        if (setOfGames.onTable.size == 4) {
            // the cards on the table are from last trick, we can clear them
            setOfGames.onTable.clear()
        }
        if (isLastBid(setOfGames.bids)) {
            if ((setOfGames.bids.size == 4) && (setOfGames.bids.filterIsInstance<Pass>().size == 4)) {
                // 4 pass means we need to redistribute
                // First we need fill pli from camp EW and NS
                setOfGames.bids.clear()
                setOfGames.whoWonLastTrick = null

                // Everybody did pass - we need to put all cards in plisCamp before a new deal
                for (i in 0..3) {
                    setOfGames.plisCampNS[i * 2] = setOfGames.players[i].cardsInHand.take(4).map { e -> CardPlayed(e) }
                    setOfGames.plisCampNS[i * 2 + 1] = setOfGames.players[i].cardsInHand.takeLast(4).map { e -> CardPlayed(e) }

                    setOfGames.players[i].cardsInHand.clear()
                }
                distribute(setOfGames)
                setOfGames.plisCampEW.clear()
                setOfGames.plisCampNS.clear()
                fire.saveGame(setOfGames)
                return
            }
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
        // This helps UI in case they missed one of the announce of other player to still know what is the last significant Bid


        setOfGames.currentBid = whatIsTheLastSignificantBid(setOfGames.bids)
        fire.saveGame(setOfGames)

    }

    fun changeNickname(setOfGames: SetOfGames, user: User) {
        setOfGames.players.first { it.uid == user.uid }.nickname = user.nickname
        fire.saveGame(setOfGames)
    }

    fun refresh() {
        sets.clear()
        sets.addAll(fire.getAllGames())
    }

    // This function is designed to help debug the server
    // we should add in it anything which can help to check a game is OK
    // if the function detect an incoherency it trace it.


    fun checkCoherency(setOfGames: SetOfGames): Boolean {
        var returnValue = true

        when (setOfGames.state) {

            TableState.ENDED -> {
            }
            TableState.JOINING -> {
            }
            TableState.BETWEEN_GAMES -> {
            }
            TableState.DISTRIBUTING -> {
            }
            TableState.BIDDING,
            TableState.PLAYING -> {
                // First check there are 32 cards ( pli / hands ) 8 belongs to each players
                val nbCards: MutableMap<PlayerPosition, Int> = PlayerPosition.values().map { Pair(it, 0) }.toMap().toMutableMap()

                for (position in PlayerPosition.values()) {
                    val player = setOfGames.players.firstOrNull { it.position == position }
                    if (player == null) {
                        debugPrintln(dbgLevel.REGULAR, "COHERENCY DID FAIL FOR ${setOfGames.id} null player at position $position")
                        returnValue = false
                    }
                    // there are several more efficient way to do it , but we really want to check coherency and potential bug
                    // so we dont take any shortcut, to be able to detect if 2 cards of a same players were put in the same pli for example.
                    nbCards[position] = setOfGames.players.first { it.position == position }.cardsInHand.size
                    for (pli in setOfGames.plisCampNS.values) {
                        nbCards[position] = nbCards[position]!! + pli.filter { it.position == position }.size
                    }
                    for (pli in setOfGames.plisCampEW.values) {
                        nbCards[position] = nbCards[position]!! + pli.filter { it.position == position }.size
                    }
                    if (setOfGames.onTable.size != 4 ) {
                        // if there are 4 cards on table , they are already in plisCampNS or plisCampEW
                        nbCards[position] = nbCards[position]!! + setOfGames.onTable.filter { it.position == position }.size
                    }


                }
                if (nbCards.any { it.value != 8 }) {
                    debugPrintln(dbgLevel.REGULAR, "COHERENCY DID FAIL FOR ${setOfGames.id} \n Players dont have 8 cards $nbCards")
                    returnValue = false
                }
            }
        }
        return returnValue
    }

    fun allCheckCoherency() : String? {
        for (setOfGames in sets)
            if (!checkCoherency(setOfGames)) {
                debugPrintln(dbgLevel.REGULAR,"Coherency error on $setOfGames detected stopping check")
                return setOfGames.id
            }
        return ""
    }

    fun playCard(setOfGames: SetOfGames, card: Card, user: User): CardPlayed {
        // check if on table is full

        if (setOfGames.onTable.size == 4) {
            // the cards on the table are from last trick, we can clear them
            setOfGames.onTable.clear()
        }

        val myCards = setOfGames.players.first { it.uid == user.uid }.cardsInHand
        val myPosition = setOfGames.players.first { it.uid == user.uid }.position

        // get the real object in hand
        if (!isValidCard(
                        myCardsInHand = myCards,
                        bid = setOfGames.currentBid,
                        cardsOnTable = setOfGames.onTable,
                        theCardToCheck = card)) {
            throw NotAuthorizedOperation("The card $card is not valid")
        }
        val beloteValue: BeloteValue = isBelote(card, myCards, myPosition, setOfGames.currentBid, setOfGames.plisCampNS, setOfGames.plisCampEW)

        if (beloteValue != BeloteValue.NONE) {
            debugPrintln(dbgLevel.DEBUG, "***********${user.nickname}($myPosition) is playing $beloteValue playing $card")
        }
        val returnValue = CardPlayed(card, beloteValue, myPosition)
        setOfGames.onTable.add(returnValue)
        setOfGames.players.first { it.uid == user.uid }.cardsInHand.removeIf { it.isSimilar(card) }
        // there was some action on the table - we reset the timer.
        setOfGames.whoseTurnTimeLastChg = System.currentTimeMillis()

        setOfGames.whoseTurn += 1
        // Evaluation of who win the trick
        if (setOfGames.onTable.size == 4) {
            val winner = calculateWinnerTrick(setOfGames.onTable, getCurrentBid(setOfGames.bids).curColor())
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
        return returnValue

    }
}