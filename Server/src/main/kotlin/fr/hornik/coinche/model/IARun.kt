package fr.hornik.coinche.model

import fr.hornik.coinche.business.isValidBid
import fr.hornik.coinche.business.playRandom
import fr.hornik.coinche.business.whatIsTheLastSignificantBid
import fr.hornik.coinche.business.whatToPlay
import fr.hornik.coinche.component.DataManagement
import fr.hornik.coinche.model.values.CardColor
import fr.hornik.coinche.model.values.CardValue
import fr.hornik.coinche.model.values.PlayerPosition
import fr.hornik.coinche.model.values.TableState
import fr.hornik.coinche.util.dbgLevel
import fr.hornik.coinche.util.debugPrintln
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min

data class IARun(val setOfGames: SetOfGames) {
    private val robotsPlayer: List<User> = listOf(
            User("Jean-Sebastien96541826ghD", "Jean-Seb"),
            User("Marie-Jeanne R.72hdkIkhgf", "Marie-Jeanne"),
            User("Charles-Henri9854RfGD", "Charles-Henri"),
            User("Pierre-Edouard7h263h", "Pierre-Edouard"),
            User("Louis-Victor872663089hJggj", "Louis-Victor"),
            User("Bernard-Lucien782034Hgh", "Bernard-Lucien"),
            User("Francine-Heloise88273048hhnd", "Francine-Heloise"),
            User("Hugo-Linus55522234Gh", "Hugo-Linus")
    ).map { User(it.uid + DataManagement.AUTOMATEDPLAYERSID, it.nickname) }

    private val MINIMUM_BETWEEN_2_ACTIONS:Long = 1500
    fun run(data: DataManagement, millis: Long): Boolean {
        //return true if saving is needed.

        // Player is rge first one automated and

        val listCandidatePlayer = setOfGames.players.filter { (setOfGames.whoseTurn == it.position) && (it.uid.contains(DataManagement.AUTOMATEDPLAYERSID)) }
        // We need to call here the function written by Sacha

        when (setOfGames.state) {
            TableState.BIDDING -> {
                // to avoid actions too fast we force interval to be at list 1,5s between 2 bids or playcard
                if ((millis - setOfGames.whoseTurnTimeLastChg) < MINIMUM_BETWEEN_2_ACTIONS ) return false
                if (listCandidatePlayer.isNotEmpty()) {
                    setOfGames.whoseTurnTimeLastChg = millis
                    val candidate = listCandidatePlayer.first()
                    val userCandidate = User(candidate.uid, candidate.nickname)
                    val aBid = enchere(setOfGames.whoseTurn, setOfGames.bids, setOfGames.players.first { it.position == setOfGames.whoseTurn }.cardsInHand, 0)
                    debugPrintln(dbgLevel.DEBUG, "ANNOUNCE AUTOMATED $aBid for player ${setOfGames.whoseTurn}")
                    data.announceBid(setOfGames, aBid, userCandidate)
                    return false
                    // no need to save after data.announceBid
                }
            }

            TableState.PLAYING -> {
                // to avoid actions too fast we force interval to be at list 1,5s between 2 bids or playcard
                if ((millis - setOfGames.whoseTurnTimeLastChg) < MINIMUM_BETWEEN_2_ACTIONS ) return false
                if (listCandidatePlayer.isNotEmpty()) {
                    val candidate = listCandidatePlayer.first()
                    val userCandidate = User(candidate.uid, candidate.nickname)

                    // TODO this will be the call to decide what card to play - Work In Progress
                    var aCard: Card? = whatToPlay(candidate.position, candidate.cardsInHand, setOfGames.bids,
                                                 setOfGames.currentBid.curColor(), setOfGames.onTable,
                                                 setOfGames.plisCampNS, setOfGames.plisCampEW)
                    if (aCard == null)  { aCard = playRandom(candidate.cardsInHand) }
                    val myCard = Card(value = aCard.value, color = aCard.color)
                    setOfGames.whoseTurnTimeLastChg = millis
                    debugPrintln(dbgLevel.DEBUG, "${setOfGames.id}:${setOfGames.whoseTurn} is playing $myCard")
                    data.playCard(setOfGames, myCard, user = userCandidate)
                    return false
                }
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

        private const val STARTINGBONUS = 2
        private const val NOTSTARTINGMALUS = -3
        private const val TOTALPOINT = 162
        private const val TRUMPJACKGAIN = 35
        private const val TRUMPNINEGAIN = 20
        private const val TRUMPACEGAIN = 15
        private const val TRUMPTENGAIN = 10
        private const val COLORACEGAIN = 13
        private const val COLORTENGAIN = 11
        private const val COLORKINGGAIN = 2
        private const val OTHERGAIN = 0


        fun enchere(myPosition: PlayerPosition = PlayerPosition.NORTH, allBids: List<Bid>, myCards: List<Card>, behaviour: Int): Bid {
            val lastBid: Bid = whatIsTheLastSignificantBid(allBids)

            val iStart:Boolean = if (allBids.isEmpty()) {
                true
            } else {
                (myPosition == allBids[0].position)
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
                    valEnchere = reversePoints(myCards, trump, iStart)
                }
                enchereColor[trump] = valEnchere
            }


            // Pour un mode prudent on retire 10 points à l'enchere proposee
            // Apres avoir estime chaque couleur, on estime la couleur du partenaire si elle existe
            // (ne faire cette estimation que si le partenaire a originellement pris
            // Pas si le partenaire est remonte sur nous

            val myPartner = surEncherePartner(allBids, myPosition, myCards)
            debugPrintln(dbgLevel.DEBUG, "Enchere Origine $enchereColor")
            debugPrintln(dbgLevel.DEBUG, "surEnchere for my partner $myPartner")


            // Apres cette estimation on compare le meilleur my bid et le bid couleur partenaire
            // Pour que my bid prevale sur le bid couleur partenaire il faut au moins + 20
            // Ensuite si le gagnant des 2 orecedents est superieur au lastbid +10 c'est bon
            // Actuellement on prends juste le max TODO("Implementer le +20")

            for (color in CardColor.values()) {
                enchereColor[color] = max(enchereColor[color]!!, myPartner[color]!!)
            }
            debugPrintln(dbgLevel.DEBUG, "Resultat $enchereColor")

            val atout = enchereColor.toList().sortedBy { (_, value) -> value }.toMap().keys.last()


            //val atout = colorTrump.toSortedMap().keys.first()
            val myEnchere: Int = (enchereColor[atout]!!.absoluteValue / 10) * 10
            val myBid: Bid

            if ((myEnchere >= 80) && (myEnchere > minPoints)) {
                myBid = when {
                    myEnchere >= 500 -> {
                        General(color = atout, position = myPosition, belote = (myEnchere > 500))
                    }
                    myEnchere >= 250 -> {
                        Capot(color = atout, position = myPosition, belote = (myEnchere > 250))
                    }
                    else -> SimpleBid(atout, myEnchere, myPosition)
                }

                // This test permits to eliminate case where somebody did coinche etc .....
                if (isValidBid(allBids, myBid))
                    return myBid
            }
            return Pass(myPosition)


        }
        //let find the first time where our partner choose a color
        //This is the only time we will take this value into account, and only if we did not bid it first

        private fun surEncherePartner(listBid: List<Bid>, myPosition: PlayerPosition, myCards: List<Card>): MutableMap<CardColor, Int> {
            val partner = myPosition + 2
            val bidPartner = listBid.filter { it.position == partner }
            val myBids = listBid.filter { it.position == myPosition }

            val tableBids: MutableMap<CardColor, Int> = CardColor.values().map { Pair(it, -30) }.toMap().toMutableMap()

            for (color in CardColor.values()) {
                val bids = bidPartner.filter { e -> (e.curColor() == color) && (myBids.none { f -> (f.curColor() == color) && (f.curPoint() < e.curPoint()) }) }
                if (bids.any()) {
                    tableBids[color] = bids.first().curPoint()
                }
            }
            val nbColors: MutableMap<CardColor, Int> = mutableMapOf()

            for (color in CardColor.values()) {
                nbColors[color] = myCards.filter { it.color == color }.size
            }
            /*
                        "Si valet -> + 20"
                        "Si 9 + 1 atout -> + 10"
                        "Si 3 atouts -> + 10 sauf si lannonce est audedsus ou egale a 120"
                        "si belote -> + 20"
                        "si As de moins de 3 cartes ou moins dans la couleur + 10 (As + 10 = 20)"
            */
            for (color in tableBids.keys) {
                val atouts = myCards.filter { it.color == color }
                val aces = myCards.filter { (it.value == CardValue.ACE) && (it.color != color) }
                if (atouts.any { it.value == CardValue.JACK }) tableBids[color] = tableBids[color]!! + 20
                if (atouts.any { it.value == CardValue.NINE } && nbColors[color]!! >= 1) tableBids[color] = tableBids[color]!! + 10
                if (tableBids[color]!! < 120 && nbColors[color]!! >= 3) tableBids[color] = tableBids[color]!! + 10
                for (ace in aces) {
                    if (nbColors[ace.color]!! <= 3) {
                        tableBids[color] = tableBids[color]!! + 10
                        if (myCards.any { (it.color == ace.color && it.value == CardValue.TEN) })
                            tableBids[color] = tableBids[color]!! + 10
                    }

                }
                debugPrintln(dbgLevel.DEBUG,"bid at $color is ${tableBids[color]} before belote and minoration ")
                var maxValue = 130
                if (atouts.any { it.value == CardValue.QUEEN } ||  atouts.any { it.value == CardValue.KING }) {
                    //it means that our partner has no belote .... so his numbers are "real"
                    maxValue = 250
                }
                // Make sure we dont have a bid impossible to do ....
                // if this is higher than 130 we limit to 130 except if it's higher than 152
                if ((tableBids[color]!! >= 130) && (tableBids[color]!! <150)) {
                    tableBids[color] = 130
                }
                if (tableBids[color]!! >= 150)  tableBids[color] = maxValue

                if (atouts.any { it.value == CardValue.QUEEN } && atouts.any { it.value == CardValue.KING }) {
                    tableBids[color] = tableBids[color]!! + 20

                }

            }
            return tableBids
        }
        private fun reversePoints(myCards: List<Card>, color: CardColor, IStart:Boolean): Int {
            var value = TOTALPOINT
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
            var belote =false

            debugPrintln(dbgLevel.DEBUG, "Trump Evaluated is $color")

            for (curColor in CardColor.values()) {
                val trump: Boolean = (color == curColor)
                var myPoints: MutableMap<CardValue, Pair<Boolean, Int>>
                val curColorCards = myCards.filter { it.color == curColor }
                // Maximum number of cards in current color we take into account
                // if Trump : 8 - nb of trump in my hands, but no more than 4
                // if regular color : nb of cards of the color in my hans, but no more than 4 ( perhaps we should use 3 ? )

                var nbMax: Int
                if (trump) {
                    myPoints = CardValue.values().map { e -> Pair(e, Pair(false, pointAtoutRev.getValue(e))) }.toMap().toMutableMap()
                    nbMax = min(8 - curColorCards.size, 4)
                    // If we have less than (or equal to) 3 trumps, starting can be a big deal : BONUS/MALUS
                    if (curColorCards.size <=3) {
                        value += if (IStart) NOTSTARTINGMALUS else STARTINGBONUS
                    }
                } else {
                    myPoints = CardValue.values().map { e -> Pair(e, Pair(false, pointCouleurRev.getValue(e))) }.toMap().toMutableMap()
                    nbMax = min(curColorCards.size, 3)

                    // look if 10 is alone
                    if ((curColorCards.size == 1) && (curColorCards[0].value == CardValue.TEN)) {
                        // remove 7 points ( no ACE is already minus 13)
                        value -= 10
                    }
                }
                for (card in curColorCards) {
                    myPoints[card.value] = myPoints[card.value]!!.copy(first = true)
                }
                if (trump && myPoints[CardValue.QUEEN]!!.first && myPoints[CardValue.KING]!!.first) {
                    belote = true
                }
                var i = 0

                // Potential Bug : need to check order of map iteration - was it changed by assignment ?
                for (obj in myPoints.toSortedMap(Comparator { c1, c2 ->
                    when (trump) {
                        true -> c2.dominanceAtout - c1.dominanceAtout
                        else -> c2.dominanceCouleur - c1.dominanceCouleur
                    }
                })) {
                    if (i++ >= nbMax) break


                    if (! obj.value.first) {
                        value -= obj.value.second
                    }
                    debugPrintln(dbgLevel.DEBUG, "Checking ${obj.key} at $curColor")

                }
            }
            if (value >= 151) value = 500

            if (belote) {
                value += 20
            } else {
                if ((value >= 140) && (value < 150)) {
                    value -=10
                }
            }
            return value
        }
    }
}