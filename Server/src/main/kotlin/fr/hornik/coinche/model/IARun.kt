package fr.hornik.coinche.model

import fr.hornik.coinche.component.DataManagement
import fr.hornik.coinche.model.values.BeloteValue
import fr.hornik.coinche.model.values.PlayerPosition
import fr.hornik.coinche.model.values.TableState

data class IARun(val set:SetOfGames) {

    fun run(data:DataManagement) {
        val p = set.players.first{ it.position == set.whoseTurn}
        val u = User(p.uid,p.nickname)

        // We need to call here the function wroten by Sacha
        when (set.state) {
            TableState.BIDDING -> {
                //println("${set.id} : BIDDING ${set.whoseTurn} in ${set.name}\n")
                val aPass = Pass(set.whoseTurn)
                set.whoseTurnTimeLastChg = System.currentTimeMillis()

                data.announceBid(set, aPass,u)
        }
            TableState.PLAYING -> {
                //println("${set.id} : PLAYING ${set.whoseTurn} in ${set.name}\n")
                var Acard = p.cardsInHand.first { it.playable == true }
                var myCard = Card(value = Acard.value, color = Acard.color)
                println("${set.id}:${set.whoseTurn} is playing $myCard")
                data.playCard(set, myCard , beloteValue = BeloteValue.NONE,user = u)

            }
        }
    }
}