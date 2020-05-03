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
        val AUTOMATEDGAMESID = "AUTOMATED"
        val AUTOMATEDPLAYERSID = "PLAYERSPLAYERS47"
    }

    val timeoutTask = object: TimerTask() {
        var timesRan = 0
        override fun run() = reviewTimer("Timeout timer passed ${++timesRan} time(s)")

    }

    fun reviewTimer(Message:String) {
        debugPrintln(dbgLevel.FUNCTION,"$Message\n")
        val millis = System.currentTimeMillis()
        var action = false
        for (setOfGames in sets) {
            if (setOfGames.name.contains(AUTOMATEDGAMESID)) {
                //The names means that we can add an automatic player ( probably will be done later through a preference

                if (IARun(setOfGames).run(this, millis))
                // in most case you dont need to save since announce/joining/bidding do the saving ... but IF we have to save something IARUN returns true
                    fire.saveGame(setOfGames)
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
                    debugPrintln(dbgLevel.DEBUG,"${setOfGames.name} was between GAME")
                    if (setOfGames.score.northSouth < 1000 && setOfGames.score.eastWest < 1000) {
                        // Continue playing another game
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
    val timer = java.util.Timer()
    init {
        sets.addAll(fire.getAllGames())

        timer.schedule(timeoutTask,0,1000)

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
                    dealCards(setOfGames.plisCampNS.map { it -> it.value}, setOfGames.plisCampEW.map {it -> it.value}, 10,
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

        setOfGames.score += calculateScoreGame(setOfGames.plisCampNS.map {it -> it.value}, setOfGames.plisCampEW.map {it -> it.value},
                setOfGames.whoWonLastTrick!!, setOfGames.currentBid)
        setOfGames.bids.clear()
        setOfGames.players.onEach { it.cardsInHand.clear() }
        setOfGames.whoWonLastTrick = null
        setOfGames.currentFirstPlayer = setOfGames.currentFirstPlayer + 1

        setOfGames.whoseTurnTimeLastChg = System.currentTimeMillis()

        setOfGames.state = TableState.BETWEEN_GAMES

    }

    private fun getGame(setId: String): SetOfGames? =
            sets.firstOrNull { it.id == setId }

    fun getGameOrThrow(setId: String): SetOfGames =
            getGame(setId) ?: throw GameNotExistingException(setId)

    fun deleteGame(setOfGames: SetOfGames, user: User):Boolean {
        val me = setOfGames.players.firstOrNull { player -> player.uid == user.uid }
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


        val IABid = IARun.enchere(me.position,setOfGames.bids, setOfGames.players.first { it.position == setOfGames.whoseTurn }.cardsInHand,0)
        if ((IABid.curColor()!=bid.curColor()) || (IABid.curPoint() != bid.curPoint())) {
            debugPrintln(dbgLevel.DEBUG,"****************Player ${setOfGames.whoseTurn} did bid $bid I'd prefer to bid $IABid ")
        }

        // We dont expect the client application to fill correctly the annonce we are coinch'ing
        // and we can compute it here so .... annonce is not checked in the function isValidBid .... so we can do it either before or after.
        if (bid is Coinche) {
            setOfGames.bids.add (Coinche(annonce = getCurrentBid(setOfGames.bids),position = bid.position, surcoinche = bid.surcoinche))
        } else {
            setOfGames.bids.add (bid)
        }
        setOfGames.whoseTurnTimeLastChg = System.currentTimeMillis()
        if (setOfGames.onTable.size == 4) {
            // the cards on the table are from last trick, we can clear them
            setOfGames.onTable.clear()
        }
        if (isLastBid(setOfGames.bids)) {
            if ((setOfGames.bids.size == 4 ) && (setOfGames.bids.filterIsInstance<Pass>().size == 4 )) {
                // 4 pass means we need to redistribute
                // First we need fill pli from camp EW and NS
                setOfGames.bids.clear()
                setOfGames.whoWonLastTrick = null

                for (i in 0..3) {
                    setOfGames.plisCampNS[i*2] = setOfGames.players[i].cardsInHand.take(4).map{ e -> CardPlayed(e)}
                    setOfGames.plisCampNS[i*2+1] = setOfGames.players[i].cardsInHand.takeLast(4).map{ e -> CardPlayed(e)}

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

    fun playCard(setOfGames: SetOfGames, card: Card, user: User) {
        // check if on table is full

        if (setOfGames.onTable.size == 4) {
            // the cards on the table are from last trick, we can clear them
            setOfGames.onTable.clear()
        }

        val myCards = setOfGames.players.first{it.uid == user.uid}.cardsInHand
        val myPosition = setOfGames.players.first { it.uid == user.uid }.position

        // get the real object in hand
        if (!isValidCard(
                        myCardsInHand = myCards,
                        bid = setOfGames.currentBid,
                        cardsOnTable = setOfGames.onTable,
                        theCardToCheck = card)) {
            throw NotAuthorizedOperation("The card $card is not valid")
        }
        val beloteValue:BeloteValue = isBelote(card,myCards,myPosition, setOfGames.currentBid,setOfGames.plisCampNS,setOfGames.plisCampEW)

        if (beloteValue != BeloteValue.NONE)  {
            debugPrintln(dbgLevel.DEBUG,"***********${user.nickname}($myPosition) is playing $beloteValue playing $card")
        }
        setOfGames.onTable.add(CardPlayed(card, beloteValue, myPosition))
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