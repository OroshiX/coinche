package fr.hornik.coinche.model

import fr.hornik.coinche.business.isValidBid
import fr.hornik.coinche.business.whatIsTheLastSignificantBid
import fr.hornik.coinche.component.DataManagement
import fr.hornik.coinche.model.values.CardColor
import fr.hornik.coinche.model.values.CardValue
import fr.hornik.coinche.model.values.PlayerPosition
import fr.hornik.coinche.model.values.TableState
import fr.hornik.coinche.util.dbgLevel
import fr.hornik.coinche.util.debugPrintln
import kotlin.math.absoluteValue
import kotlin.math.min

data class IARun(val setOfGames: SetOfGames) {
    val robotsPlayer: List<User> = listOf(
            User("Jean-Sebastien96541826ghD", "Jean-Seb"),
            User("Marie-Jeanne R.72hdkIkhgf", "Marie-Jeanne"),
            User("Charles-Henri9854RfGD", "Charles-Henri"),
            User("Pierre-Edouard7h263h", "Pierre-Edouard"),
            User("Louis-Victor872663089hJggj", "Louis-Victor"),
            User("Bernard-Lucien782034Hgh", "Bernard-Lucien"),
            User("Francine-Heloise88273048hhnd", "Francine-Heloise"),
            User("Hugo-Linus55522234Gh", "Hugo-Linus")
    ).map { User(it.uid + DataManagement.AUTOMATEDPLAYERSID, it.nickname) }

    fun run(data: DataManagement, millis: Long): Boolean {
        //return true if saving is needed.

        // Player is rge first one automated and

        val listCandidatePlayer = setOfGames.players.filter { (setOfGames.whoseTurn == it.position) && (it.uid.contains(DataManagement.AUTOMATEDPLAYERSID)) }
        // We need to call here the function written by Sacha

        when (setOfGames.state) {
            TableState.BIDDING -> if (!listCandidatePlayer.isEmpty()) {
                setOfGames.whoseTurnTimeLastChg = millis
                val p = listCandidatePlayer.first()
                val u = User(p.uid, p.nickname)
                val aBid = enchere(setOfGames.whoseTurn, setOfGames.bids, setOfGames.players.first { it.position == setOfGames.whoseTurn }.cardsInHand, 0)
                debugPrintln(dbgLevel.DEBUG, "ANNOUNCE AUTOMATED $aBid for player ${setOfGames.whoseTurn}")
                data.announceBid(setOfGames, aBid, u)
                return false
                // no need to save after data.announceBid
            }

            TableState.PLAYING -> if (!listCandidatePlayer.isEmpty()) {
                val p = listCandidatePlayer.first()
                val u = User(p.uid, p.nickname)
                val Acard = p.cardsInHand.filter { it.playable == true }.random()
                val myCard = Card(value = Acard.value, color = Acard.color)
                setOfGames.whoseTurnTimeLastChg = millis
                debugPrintln(dbgLevel.DEBUG, "${setOfGames.id}:${setOfGames.whoseTurn} is playing $myCard")
                data.playCard(setOfGames, myCard, user = u)
                return false
            }

            TableState.ENDED -> {
                // TODO probably to clean the game since it's an automatic one. (call data.deleteGame() + a refresh?
            }
            TableState.JOINING -> {
                if ((millis - setOfGames.whoseTurnTimeLastChg) > setOfGames.preferences.JoiningMaxTime) {

                    if (setOfGames.players.size < 4) {
                        //The names means that we can add an automatic player ( probably will be done later through a preference
                        val autoPlayer = robotsPlayer.filterNot { e -> setOfGames.players.any { it.uid == e.uid } }.random()

                        data.joinGame(setOfGames, autoPlayer, autoPlayer.nickname)
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

        // Parameters to compute points evaluations for Announces

        val STARTINGBONUS = 2
        val NOTSTARTINGMALUS = -3
        val TOTALPOINT = 162
        val TRUMPJACKGAIN = 35
        val TRUMPNINEGAIN = 20
        val TRUMPACEGAIN = 15
        val TRUMPTENGAIN = 10
        val COLORACEGAIN = 13
        val COLORTENGAIN = 11
        val COLORKINGGAIN = 2
        val OTHERGAIN = 0


        fun enchere(myPosition: PlayerPosition = PlayerPosition.NORTH, allBids: List<Bid>, myCards: List<Card>, behaviour: Int): Bid {
            val mapTrump: Map<CardValue, Int>
            val lastBid: Bid = whatIsTheLastSignificantBid(allBids)
            var IStart:Boolean

            if (allBids.isEmpty()) {
                IStart = true
            } else {
                IStart = (myPosition == allBids[0].position)
            }
            val bidPartner: Bid = when {
                // Pas d'enchere precedente
                allBids.isEmpty() -> {
                    Pass()
                }
                // 1 seule enchere precedente
                allBids.size == 1 -> {
                    Pass()
                }
                // cas classique plus d'1 encheres
                else -> {
                    allBids[allBids.size - 2]
                }
            }
            val minPoints = lastBid.curPoint()
            val mapColor = mapOf(CardColor.HEART to 0,
                    CardColor.DIAMOND to 1,
                    CardColor.CLUB to 2,
                    CardColor.SPADE to 3)
            val listColor = listOf(CardColor.HEART, CardColor.DIAMOND, CardColor.CLUB, CardColor.SPADE)
            // valeurs heuristiques pour le nombre d'atouts 0 atouts -> 8 atouts, a du sens pour 1 - 6 atouts
            val valueNbTrump = listOf(-18, -18, 4, 10, 22, 34, 45, 45, 45)
            // L'enchere prevue pour chaque couleur = atout
            val enchereColor: MutableMap<CardColor, Int> = mutableMapOf()

            // On estime pour chaque couleur
            for (trump in listColor) {

                // Pour voir si l'As a l'atout est maitre et le 10 a la couleur est maitre et non sec
                val dominance = mutableListOf(0, 0, 0, 0)
                var belote = 0
                var nbTrump = 0

                // le cumul des estimations pour cette couleur d'atout donnee
                var valEnchere = 0

                // nb carte pour chaque couleur a atout fixe
                val nbOfColor = mutableListOf(0, 0, 0, 0)
                // On passe par toutes les cartes
                for (card in myCards) {
                    // On compte le nombre de cartes par couleur
                    nbOfColor[mapColor.getValue(card.color)] += 1
                    // Ici les atouts
                    if (card.color == trump) {

                        // On compte les atouts
                        nbTrump++
                        when (card.value) {
                            CardValue.JACK -> {
                                // Chaque carte a une valeur (valet = 45 par exemple)
                                valEnchere += 45
                                // On sait qu'on a le valet a latout
                                dominance[mapColor.getValue(card.color)] += 1
                            }
                            CardValue.NINE -> {
                                valEnchere += 25

                                // On sait qu'on a le neuf a latout
                                dominance[mapColor.getValue(card.color)] += 1
                            }
                            CardValue.ACE -> {
                                valEnchere += 6
                                // On sait qu'on l as a latout (si on a les 3 dominants -> +10)
                                dominance[mapColor.getValue(card.color)] += 1
                            }
                            CardValue.KING -> {
                                valEnchere += 6
                                belote++
                            }
                            CardValue.QUEEN -> {
                                valEnchere += 6
                                // Si belote = 2 -> +20
                                belote++
                            }
                            else -> valEnchere += 6
                        }
                    } else {
                        // Ici les non atouts
                        when (card.value) {
                            CardValue.ACE -> {
                                valEnchere += 14
                                // On sait qu'on a l as
                                dominance[mapColor.getValue(card.color)] += 100
                            }
                            CardValue.TEN -> {
                                valEnchere += 7
                                // On sait qu'on a le dix
                                dominance[mapColor.getValue(card.color)] += 10
                            }
                            else -> {
                                // On sait que le potentiel dix n'est pas sec
                                dominance[mapColor.getValue(card.color)] += 1
                            }
                        }
                    }
                }
                // si on a la belote -> + 20
                if (belote == 2) valEnchere += 20
                for (i in 0..3) {
                    if (listColor[i] == trump) {
                        // Si on a Valet + 9 + As a l'atout on rajoute 10 points
                        if (dominance[i] == 3) valEnchere += 10
                    } else when {
                        // si on a As et 10 -> on rajoute 3
                        (dominance[i] > 109) -> valEnchere += 3
                        // si on a le 10 sec on ne le compte pas
                        (dominance[i] == 10) -> valEnchere += -7
                        else -> valEnchere += 0
                    }
                }
                // nb d'atouts
                nbTrump = nbOfColor[mapColor.getValue(trump)]
                // on rajoute des points selon le nombre d'atouts
                valEnchere += valueNbTrump[nbTrump]

                // fin du classique, maintenant si on obtient plus de 120 on compte a l'envers
                // Le plus pratique ici est de trier la main (a faire)
                if (valEnchere > 120) {

                    /*

                           "On considère qu'on a x atouts, on réalise alors 8-x fois l'étape suivante :" +
                                   "On retire les points assocciés à pointAtoutReverse si on a pas cette carte" +
                                   "Exemple : j'ai 6 atouts mais pas le 9 ni l'as, je ne retire pas les points correspondants au valet car je l'ai" +
                                   "Je retire les 20 points correspondant au 9 car je ne l'ai pas j'avais 8-6 = 2 etapes a faire, je m'arrete"+
                                   "Puis pour chaque couleur, considerant que j'ai x cartes de ladite couleur je fais max(3,x) etapes :" +
                                   "On retire les points associés à pointColorReverse si on a pas la carte" +
                                   "Exemple : j'ai 2 trèfles As, Valet je ne retire pas les 13 points de l'As, car je l'ai" +
                                   "je retire les 11 points du 10 car je ne l'ai pas, je ne fais que 2 etapes car j'ai 2 trefles "


                    */
                    valEnchere = reversePoints(myCards, trump, IStart)
                }
                enchereColor[trump] = valEnchere
            }

            val atout = enchereColor.toList().sortedBy { (_, value) -> value }.toMap().keys.last()

            //val atout = colorTrump.toSortedMap().keys.first()
            val myEnchere: Int = (enchereColor[atout]!!.absoluteValue / 10) * 10
            val myBid: Bid

            if ((myEnchere >= 80) && (myEnchere > minPoints)) {
                if (myEnchere >= 500) {
                    myBid = General(color = atout, position = myPosition, belote = (myEnchere >= 520))
                } else if (myEnchere >= 250) {
                    myBid = Capot(color = atout, position = myPosition, belote = (myEnchere >= 250))
                } else myBid = SimpleBid(atout, myEnchere, myPosition)

                if (isValidBid(allBids, myBid))
                    return myBid
            }
            return Pass(myPosition)


            // Pour un mode prudent on retire 10 points à l'enchere proposee
            // Apres avoir estime chaque couleur, on estime la couleur du partenaire si elle existe
            // (ne faire cette estimation que si le partenaire a originellement pris
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

        fun reversePoints(myCards: List<Card>, color: CardColor, IStart:Boolean): Int {
            var Value = TOTALPOINT
            val pointAtoutRev = mapOf<CardValue, Int>(
                    Pair(CardValue.JACK, TRUMPJACKGAIN),
                    Pair(CardValue.NINE, TRUMPNINEGAIN),
                    Pair(CardValue.ACE, TRUMPACEGAIN),
                    Pair(CardValue.TEN, TRUMPTENGAIN),
                    Pair(CardValue.KING, OTHERGAIN),
                    Pair(CardValue.QUEEN, OTHERGAIN),
                    Pair(CardValue.EIGHT, OTHERGAIN),
                    Pair(CardValue.SEVEN, OTHERGAIN))
            val pointCouleurRev = mapOf<CardValue, Int>(
                    Pair(CardValue.ACE, COLORACEGAIN),
                    Pair(CardValue.TEN, COLORTENGAIN),
                    Pair(CardValue.KING, COLORKINGGAIN),
                    Pair(CardValue.QUEEN, OTHERGAIN),
                    Pair(CardValue.JACK, OTHERGAIN),
                    Pair(CardValue.NINE, OTHERGAIN),
                    Pair(CardValue.EIGHT, OTHERGAIN),
                    Pair(CardValue.SEVEN, OTHERGAIN))
            var Belote:Boolean=false

            debugPrintln(dbgLevel.DEBUG, "Trump Evaluated is $color")

            for (curColor in CardColor.values()) {
                val trump: Boolean = (color == curColor)
                var myPoints: MutableMap<CardValue, Pair<Boolean, Int>>
                val curColorCards = myCards.filter { it.color == curColor }
                // Maximum number of cards in current color we take into account
                // if Trump : 8 - nb of trump in my hands, but no more than 4
                // if regular color : nb of cards of the color in my hans, but no more than 4 ( perhaps we should use 3 ? )

                var nbMax = 0
                if (trump) {
                    myPoints = CardValue.values().map { e -> Pair(e, Pair(false, pointAtoutRev[e]!!)) }.toMap().toMutableMap()
                    nbMax = min(8 - curColorCards.size, 4)
                    // If we have less than (or equal to) 3 trumps, starting can be a big deal : BONUS/MALUS
                    if (curColorCards.size <=3) {
                        if (IStart) Value += NOTSTARTINGMALUS else Value+= STARTINGBONUS
                    }
                } else {
                    myPoints = CardValue.values().map { e -> Pair(e, Pair(false, pointCouleurRev[e]!!)) }.toMap().toMutableMap()
                    nbMax = min(curColorCards.size, 3)

                    // look if 10 is alone
                    if ((curColorCards.size == 1) && (curColorCards[0].value == CardValue.TEN)) {
                        // remove 7 points ( no ACE is already minus 13)
                        Value = Value - 10
                    }
                }
                for (card in curColorCards) {
                    myPoints[card.value] = myPoints[card.value]!!.copy(first = true)
                }
                if (trump && myPoints[CardValue.QUEEN]!!.first && myPoints[CardValue.KING]!!.first) {
                    Belote = true
                }
                var i = 0

                // Potential Bug : need to check order of map iteration - was it changed by assignment ?
                for (obj in myPoints.toSortedMap(object : Comparator<CardValue> {
                    override fun compare(c1: CardValue, c2: CardValue): Int = when (trump) {
                        true -> c2.dominanceAtout - c1.dominanceAtout
                        else -> c2.dominanceCouleur - c1.dominanceCouleur
                    }
                })) {
                    if (i++ >= nbMax) break


                    if (! obj.value.first) {
                        Value -= obj.value.second
                    }
                    debugPrintln(dbgLevel.DEBUG, "Checking ${obj.key} at $curColor")

                }
            }
            if (Value >= 151) Value = 500

            if (Belote) {
                Value += 20
            } else {
                if ((Value >= 140) && (Value < 150)) {
                    Value -=10
                }
            }
            return Value
        }
    }
}