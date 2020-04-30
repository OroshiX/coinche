package fr.hornik.coinche.model

import fr.hornik.coinche.business.whatIsTheLastSignificantBid
import fr.hornik.coinche.component.DataManagement
import fr.hornik.coinche.model.values.*
import java.util.*
import kotlin.math.absoluteValue

data class IARun(val setOfGames: SetOfGames) {

    fun run(data: DataManagement, millis: Long): Boolean {
        //return true if saving is needed.

        // Player is rge first one automated and

        val listCandidatePlayer = setOfGames.players.filter { (setOfGames.whoseTurn == it.position) && (it.uid.contains(data.AUTOMATEDPLAYERSID)) }
        // We need to call here the function written by Sacha
        when (setOfGames.state) {
            TableState.BIDDING -> if (!listCandidatePlayer.isEmpty()) {
                //println("${set.id} : BIDDING ${set.whoseTurn} in ${set.name}\n")
                setOfGames.whoseTurnTimeLastChg = millis
                println("${setOfGames.id}:${setOfGames.whoseTurn} is passing")
                val p = listCandidatePlayer.first()
                val u = User(p.uid, p.nickname)
                val aBid = enchere(setOfGames.whoseTurn, setOfGames.bids, setOfGames.players.first { it.position == setOfGames.whoseTurn }.cardsInHand, 0)
                println(message = "ANNOUNCE AUTOMATED $aBid for player ${setOfGames.whoseTurn}")
                data.announceBid(setOfGames, aBid, u)
                return false
                // no need to save after data.announceBid
            }

            TableState.PLAYING -> if (!listCandidatePlayer.isEmpty()) {
                val p = listCandidatePlayer.first()
                val u = User(p.uid, p.nickname)
                //println("${set.id} : PLAYING ${set.whoseTurn} in ${set.name}\n")
                var Acard = p.cardsInHand.filter { it.playable == true }.random()
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
            else -> {
            }
        }
        return false
    }

    companion object {
        fun enchere(myPosition: PlayerPosition = PlayerPosition.NORTH, allBids: List<Bid>, myCards: List<Card>, behaviour: Int): Bid {
            var lastBid: Bid = whatIsTheLastSignificantBid(allBids)
            var bidPartner: Bid
            when {
                // Pas d'enchere precedente
                allBids.isEmpty() -> {
                    bidPartner = Pass()
                }
                // 1 seule enchere precedente
                allBids.size == 1 -> {
                    bidPartner = Pass()
                }
                // cas classique plus d'1 encheres
                else -> {
                    bidPartner = allBids[allBids.size - 2]
                }
            }
            val minPoints = lastBid.curPoint()
            val mapColor = mapOf(CardColor.HEART to 0,
                    CardColor.DIAMOND to 1,
                    CardColor.CLUB to 2,
                    CardColor.SPADE to 3)
            val listColor = listOf(CardColor.HEART, CardColor.DIAMOND, CardColor.CLUB, CardColor.SPADE)
            val valueNbTrump = listOf(-18, -18, 4, 10, 22, 34, 45, 45, 45)      // valeurs heuristiques pour le nombre d'atouts 0 atouts -> 8 atouts, a du sens pour 1 - 6 atouts
            var enchereColor: MutableMap<CardColor, Int> = mutableMapOf() // L'enchere prevue pour chaque couleur = atout

            // On estime pour chaque couleur
            for (trump in listColor) {
                val dominance = mutableListOf(0, 0, 0, 0)          // Pour voir si l'As a laotut est maitre et le 10 a la couleur est maitre et non sec
                var belote = 0
                var nbTrump = 0
                var valEnchere = 0                           // le cumul des estimations pour cette couleu d'atout donnee
                val nbOfColor = mutableListOf(0, 0, 0, 0)              // nb carte pour chaque couleur a atout fixe
                // On passe par toutes les cartes
                for (card in myCards) {
                    nbOfColor[mapColor.getValue(card.color)] += 1                   // On compte le nombre de cartes par couleur
                    // Ici les atouts
                    if (card.color == trump) {
                        nbTrump++                                                  // On compte les atouts
                        when (card.value) {
                            CardValue.JACK -> {
                                valEnchere += 45                                    // Chaque carte a une valeur (valet = 45 par exemple)
                                dominance[mapColor.getValue(card.color)] += 1       // On sait qu'on le valet a latout
                            }
                            CardValue.NINE -> {
                                valEnchere += 25
                                dominance[mapColor.getValue(card.color)] += 1       // On sait qu'on le neuf a latout
                            }
                            CardValue.ACE -> {
                                valEnchere += 6
                                dominance[mapColor.getValue(card.color)] += 1       // On sait qu'on l as a latout (si on a les 3 dominants -> +10)
                            }
                            CardValue.KING -> {
                                valEnchere += 6
                                belote++
                            }
                            CardValue.QUEEN -> {
                                valEnchere += 6
                                belote++                                           // Si belote = 2 -> +20
                            }
                            else -> valEnchere += 6
                        }
                    } else {
                        // Ici les non atouts
                        when (card.value) {
                            CardValue.ACE -> {
                                valEnchere += 14
                                dominance[mapColor.getValue(card.color)] += 100       // On sait qu'on a l as
                            }
                            CardValue.TEN -> {
                                valEnchere += 7
                                dominance[mapColor.getValue(card.color)] += 10       // On sait qu'on a le dix
                            }
                            else -> {
                                dominance[mapColor.getValue(card.color)] += 1       // On sait que le potentiel dix n'est pas sec
                            }
                        }
                    }
                }
                if (belote == 2) valEnchere += 20                                   // si on a la belote -> + 20
                for (i in 0..3) {
                    if (listColor[i] == trump) {
                        if (dominance[i] == 3) valEnchere += 10                       // Si on a Valet + 9 + As a l'atout on rajoute 10 points
                    } else when {
                        (dominance[i] > 109) -> valEnchere += 3                     // si on a As et 10 -> on rajoute 3
                        (dominance[i] == 10) -> valEnchere += -7                    // si on a le 10 sec on ne le compte pas
                        else -> valEnchere += 0
                    }
                }
                nbTrump = nbOfColor[mapColor.getValue(trump)]                   // nb d'atouts
                valEnchere += valueNbTrump[nbTrump]                                 // on rajoute des points selon le nombre d'atouts

                // fin du classique, maintentant si on obtuent plus de 120 on compte a l'envers
                // Le plus pratique ici est de trier la main (a faire)
                if (valEnchere > 120) {
                    val pointAtoutReverse = listOf(35, 20, 15, 10)                     // si on a pas le valet -35, si pas 9 -20 pas as -15 pas 10 -10
                    val pointColorReverse = listOf(13, 11, 2)                         // si on a pas un as -13, pas 10 -11 pas roi -2
                    var newEnchere = 162
                    if (belote == 2) newEnchere = 182
                    /*

                        TODO(
                            "On considère qu'on a x atouts, on réalise alors 8-x fois l'étape suivante :" +
                                    "On retire les points assocciés à pointAtoutReverse si on a pas cette carte" +
                                    "Exemple : j'ai 6 atouts mais pas le 9 ni l'as, je ne retire pas les points correspondants au valet car je l'ai" +
                                    "Je retire les 20 points correspondant au 9 car je ne l'ai pas j'avais 8-6 = 2 etapes a faire, je m'arrete"+
                                    "Puis pour chaque couleur, considerant que j'ai x cartes de ladite couleur je fais max(3,x) etapes :" +
                                    "On retire les points associés à pointColorReverse si on a pas la carte" +
                                    "Exemple : j'ai 2 trèfles As, Valet je ne retire pas les 13 points de l'As, car je l'ai" +
                                    "je retire les 11 points du 10 car je ne l'ai pas, je ne fais que 2 etapes car j'ai 2 trefles " )


                     */
                }
                enchereColor[trump] = valEnchere
            }

            val atout = enchereColor.toList().sortedBy { (_, value) -> value }.toMap().keys.last()

            //val atout = colorTrump.toSortedMap().keys.first()
            val myEnchere: Int = (enchereColor[atout]!!.absoluteValue / 10) * 10

            if ((myEnchere >= 80) && (myEnchere > minPoints)) {

                return SimpleBid(atout, myEnchere, myPosition)
            } else {
                return Pass(myPosition)
            }
            // Pour un mode prudent on retire 10 points à l'enchere proposee
            // Apres avoir estime chaque couleur, on estime la couleur du partenaire si elle existe (ne faire cette estimation que si le partenaire a originellement pris
            // Pas si le partenaire est remonte sur nous
            /*
            TODO(
                    "Si valet -> + 20" +
                            "Si 9 + 1 atout -> + 10" +
                            "Si 3 atouts -> + 10 sauf si lannonce est audedsus ou egale a 120" +
                            "si belote -> + 20" +
                            "si As de moins de 3 cartes ou moins dans la couleur + 10 (As + 10 = 20)"

            )

             */

            // Apres cette estimation on compare le meilleur my bid et le bid couleur partenaire
            // Pour que my bid prevale sur le bid couleur partenaire il faut au moins + 20
            // Ensuite si le gagnant des 2 orecedents est superieur au lastbid +10 c'est bon

        }
    }
}