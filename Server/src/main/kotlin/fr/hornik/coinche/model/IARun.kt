package fr.hornik.coinche.model

import fr.hornik.coinche.component.DataManagement
import fr.hornik.coinche.model.values.BeloteValue
import fr.hornik.coinche.model.values.TableState
import java.util.*

data class IARun(val setOfGames: SetOfGames) {

    fun run(data: DataManagement, millis: Long): Boolean {
        //return true if saving is needed.

        // Player is rge first one automated and

        val listCandidatePlayer = setOfGames.players.filter { (setOfGames.whoseTurn == it.position) && (it.uid.contains(data.AUTOMATEDPLAYERSID))}
        // We need to call here the function written by Sacha
        when (setOfGames.state) {
            TableState.BIDDING -> if (!listCandidatePlayer.isEmpty() ) {
                //println("${set.id} : BIDDING ${set.whoseTurn} in ${set.name}\n")
                val aPass = Pass(setOfGames.whoseTurn)
                setOfGames.whoseTurnTimeLastChg = millis
                println("${setOfGames.id}:${setOfGames.whoseTurn} is passing")
                val p = listCandidatePlayer.first()
                val u = User(p.uid, p.nickname)
                data.announceBid(setOfGames, aPass, u)
                return false
                // no need to save after data.announceBid
            }

            TableState.PLAYING -> if (!listCandidatePlayer.isEmpty()) {
                val p = listCandidatePlayer.first()
                val u = User(p.uid, p.nickname)
                //println("${set.id} : PLAYING ${set.whoseTurn} in ${set.name}\n")
                var Acard = p.cardsInHand.filter{ it.playable == true }.random()
                var myCard = Card(value = Acard.value, color = Acard.color)
                setOfGames.whoseTurnTimeLastChg = millis
                println("${setOfGames.id}:${setOfGames.whoseTurn} is playing $myCard")
                data.playCard(setOfGames, myCard, beloteValue = BeloteValue.NONE, user = u)
                return false
            }

            TableState.ENDED -> {
            // TODO probably to clean the game since it's an automatic one. (call data.deleteGame() + a refresh?
            }
            TableState.JOINING -> {
                if ((millis - setOfGames.whoseTurnTimeLastChg) > setOfGames.preferences.JoiningMaxTime) {

                    if (setOfGames.players.size < 4) {
                        //The names means that we can add an automatic player ( probably will be done later through a preference
                        val v: Int = Random().nextInt()
                        val w: Int = Random().nextInt()
                        data.joinGame(setOfGames, User("${w}${data.AUTOMATEDPLAYERSID}${v}", "AUTOMATED PLAYER ${v % 10}"), "AUTOMATED PLAYER ${v % 10}")
                        setOfGames.whoseTurnTimeLastChg = millis
                    }
                }
            }
            else -> {}
        }
        return false
    }
}