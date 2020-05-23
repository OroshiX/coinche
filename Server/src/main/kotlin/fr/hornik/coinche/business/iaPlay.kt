package fr.hornik.coinche.business

import fr.hornik.coinche.model.Bid
import fr.hornik.coinche.model.Card
import fr.hornik.coinche.model.CardPlayed
import fr.hornik.coinche.model.Pass
import fr.hornik.coinche.model.values.BeloteValue
import fr.hornik.coinche.model.values.CardColor
import fr.hornik.coinche.model.values.CardValue
import fr.hornik.coinche.model.values.PlayerPosition
import fr.hornik.coinche.util.dbgLevel
import fr.hornik.coinche.util.debugPrintln

// Class used to compute remaining cards in hand following rules and heuristics

enum class CardInPlayerHand { YES, UNK, NO }

fun getLineNumber(): Int {
    return Thread.currentThread().stackTrace[2].lineNumber
}
/*
Fun master(color) :
 Pour une couleur donnée, selon les cartes qui sont tombées dans les plis précédents,
 renvoie la carte dominante encore restante (si elles n’ont pas toutes déjà été jouées)
 Note : this function should not use the cards on Table
*/

fun masterColor(color: CardColor, atout: CardColor, allCardsPlayed: List<CardPlayed>): Card? {
    val colorCardsPlayed = allCardsPlayed.filter { it.card.color == color }
    val remainColorCards = CardValue.values().filterNot { e -> colorCardsPlayed.any { it.card.value == e } }.sortedBy {
        when (atout == color) {
            true -> 8 - it.dominanceAtout
            else -> 8 - it.dominanceCouleur
        }
    }
    return if (remainColorCards.isEmpty()) {
        null
    } else {
        Card(remainColorCards.first(), color)
    }
}


fun totalRemainingNumber(color: CardColor, allCardsPlayed: List<CardPlayed>, cardsInHand: List<Card>): Int {
    /*
        Pour une couleur donnée, selon les cartes qui sont tombées dans les plis précédents,
        renvoie le nombre de cartes restantes (inclut nos cartes) de la couleur
     */
    val colorCardsPlayed = allCardsPlayed.filter { it.card.color == color }.size
    val colorCardsHand = cardsInHand.filter { it.color == color }.size

    return 8 - colorCardsHand - colorCardsPlayed
}

// return a map with the number of each color remaining in my hand
fun colorInMyGame(cardsInHand: List<Card>): Map<CardColor, Int> {
    val mapColor = CardColor.values().map { Pair(it, 0) }.toMap().toMutableMap()
    CardColor.values().forEach { mapColor[it] = cardsInHand.filter { e -> e.color == it }.size }
    return mapColor
}

/*

    Pour un une couleur donnée, renvoie pour l’ensemble des 3 autres joueurs s’il possèdent la couleur ou non. Possède des éléments heuristiques. Fonctionne différemment pour les couleurs ou pour l’atout.
Couleur :
cR1 - Pour une couleur c, si un joueur X a joué d’une couleur différente de c alors que c’était la première carte jouée à un tour précédent, le joueur X ne possède pas la couleur c.
cR2 - Si toutes les cartes (en comptant celles présentes dans mon jeu) de la couleur c sont tombées, le joueur X en particulier ne possède pas la couleur c.
cR3 - (règle heuristique) Si le joueur X a mis un 10 ou un Roi de la couleur c alors que c’était une carte adjacente à la carte maîtresse, et que cette carte maîtresse était à l’adversaire alors on considère que le joueur X ne possède plus de la couleur c Exemple : Au tour 2, Ouest s’est défaussé de son 10 de pique. Au tour 4, Nord met l’As de pique, puis Est met le roi de pique. Puisque, le Roi et l’As de pique sont adjacents (le 10 de pique avait été défaussé), et que Nord est maître, on considère que Est n’a dorénavant plus de pique. N.B. : On ne prend (arbitrairement) en compte cette règle que pour le 10 et le Roi, Dame et Valet ne valent pas assez de points pour que ce soit significatif. N.B. 2 : L’exemple ci-dessus fonctionnerait aussi si le 10 de pique n’était pas tombé mais qu’il se trouvait dans la main du joueur qui est en train de calculer.
*R4 - (règle heuristique), Si le joueur X a mis un 10 de la couleur c sans que l’As ne soit tombé au préalable dans le pli actuel ou les plis précédents, on considère que le joueur X ne possède plus de la couleur c.
cR5 - (règle heuristique), Si le joueur X a mis un 10 ou un As de la couleur c alors qu’un de ces adversaires a mis préalablement dans ce pli un atout (et que cet atout était le plus fort à ce moment) alors on considère que le joueur X ne possède plus de la couleur c.
Atout :
    aR1 - Si le joueur X n’a pas joué de l’atout alors que l’atout était demandé ou qu’il a joué d’une couleur différente de celle demandée alors que son partenaire n’était pas maître alors le joueur X n’a plus d’atout.
    R2 - DUPLICATE WITH R2 Si tous les atouts sont tombés (ceux que j’ai en main sont considérés tombés) alors le joueur X n’a plus d’atout.
    aR3 - Si le joueur X a joué un atout de valeur inférieure à l’atout noté a le plus fort du pli (a doit uniquement être parmi les cartes jouées avant le joueur X lors de ce pli), alors on sait qu’il n’a pas d’atout supérieur à a, on peut donc appliquer la règle R2 avec un plus petit nombre d’atouts. Exemple : C’est atout Coeur. Au tour 1 Nord a joué 10 de Coeur, Est a joué 8 de Coeur, Sud a joué Valet de Coeur, Ouest a joué 7 de Coeur. Plus tard au tour 3, la Dame de Coeur est jouée. De plus Nord (= le joueur qui calcule) possède dans son jeu le roi de Coeur. En parcourant les plis on voit qu’au tour 1, Est rentre dans les conditions de R3, on applique donc R2 avec les atouts inférieurs au 10 de Coeur. Or tous les coeurs inférieurs au 10 sont tombés (Roi dans la main, 8 et 7 au tour 1, Dame au tour 3), on en conclut que Est n’a plus d’atout.
    aR4 - (règle heuristique similaire à R3 couleur) Si le joueur X a mis un 9, un As ou un 10 d’atout alors que c’était une carte adjacente à la carte maîtresse et que cette carte maîtresse est à l’adversaire alors on considère que le joueur X n’a plus d’atout. Vraiment la même règle que R3 couleur, simplement elle s’applique pour 9, As et 10.
    aR5 - (règle heuristique légèrement similaire à R4 couleur) À n’appliquer que pour les adversaires, par pour le partenaire. Si le joueur X a mis un 9 d’atout, que la première carte du pli était de l’atout (ne pas faire si la première carte était de la couleur et que le joueur X devait couper) et que le valet d’atout n’est pas préalablement tombé, alors on considère que le joueur X ne possède plus que des atouts inférieurs au seuil auquel il était tenu. On applique alors R2 sur ces atouts inférieurs. Exemple : L’atout est Coeur. Au tour 1 Nord joue la Dame de Coeur, Est joue le 9 de Coeur, Sud joue le Valet de Coeur, Ouest joue le 7 de Coeur. Au tour 2 le 8 de Coeur est joué. En parcourant les plis on voit qu’au tour 1 Est rentre dans les conditions de R5, on applique donc R2 avec les atouts inférieurs à la Dame. Or tous les coeurs inférieurs à la Dame sont tombés (7 au tour 1 et 8 au tour 2), on en conclut que Est n’a plus d’atout.
    aR6 - (règle heuristique) Si l’annonce initiale du partenaire est supérieure ou égale à 100, qu’il ne reste plus qu’un seul atout dehors (en comptant comme tombés les atouts dans notre main) et que l’on a pas estimé avec les règles précédentes que le partenaire n’a plus d’atout (on estime qu’il lui reste de l’atout donc), alors on considère que c’est le partenaire qui possède ce dernier atout, on dit que les deux adversaires n’ont plus d’atouts. R6’ avec 2 atouts dehors et l’annonce initiale du partenaire >= 120.
    aR7 - (règle heuristique) Si l’annonce initiale du partenaire est supérieure ou égale à 110, que le partenaire (ou moi) a déjà lancé un tour d’atout (joué un atout en première carte d’un pli) et que 2 atouts ou plus ne provenant pas du partenaire sont tombés hors de ce tour d’atout (mes atouts en main font partie des cartes tombées), alors on considère que seul le partenaire possède les atouts restants, on estime que les deux adversaires n’ont plus d’atout. R7’ avec une annonce >= 120 et 1 atout non partenaire.



 */



//Rule 1 - valid color and Atout
fun colorRule1(color: CardColor, allPlayerColor: MutableMap<PlayerPosition, Boolean>, pli: List<CardPlayed>,
               pliColor: CardColor): MutableMap<PlayerPosition, Boolean> {
    //R1 - For a color C, if it was the first color of the trick and a player X played a different color
    //     it means that X is missing C

    if (pliColor == color) {
        for (position in pli.filter { it.card.color != color }.map { it.position }) {
            allPlayerColor[position] = false
        }
    }
    return allPlayerColor
}


//Rule 2 - valid color and Atout
fun colorRule2(color: CardColor, allPlayerColor: MutableMap<PlayerPosition, Boolean>, listPlis: List<List<CardPlayed>>,
               cardsInHand: List<Card>)
        : MutableMap<PlayerPosition, Boolean> {

    // R2 - If all cards ( including mine) of color C were played, than all X player are missing the color
    val allCardsPlayed = listPlis.toList().flatten()
    val nbCardPlayed = allCardsPlayed.filter { it.card.color == color }.size
    val nbMyCard = cardsInHand.filter { it.color == color }.size

    if ((nbCardPlayed + nbMyCard) == 8) {
        // here we change also my position , but this will be put to real value at the end of caller since it s a special case
        PlayerPosition.values().forEach { e -> allPlayerColor[e] = false }
    }
    return allPlayerColor
}


// Exemple : Au tour 2, Ouest s’est défaussé de son 10 de pique. Au tour 4, Nord met l’As de pique, puis Est met le roi de pique.
// Puisque, le Roi et l’As de pique sont adjacents (le 10 de pique avait été défaussé), et que Nord est maître,
// on considère que Est n’a dorénavant plus de pique. N.B. : On ne prend (arbitrairement) en compte cette règle
// que pour le 10 et le Roi, Dame et Valet ne valent pas assez de points pour que ce soit significatif.
// N.B. 2 : L’exemple ci-dessus fonctionnerait aussi si le 10 de pique n’était pas tombé mais qu’il se trouvait dans la main du joueur qui est en train de calculer.


// Rule 3 only valid for color - Heuristic
fun colorRule3(color: CardColor,
               allPlayerColor: MutableMap<PlayerPosition, Boolean>,
               pli: List<CardPlayed>,
               currentMaster: CardValue?,
               nextMaster: CardValue?,
               winnerTrick: PlayerPosition): MutableMap<PlayerPosition, Boolean> {
    //R3  - if player X played a 10 or 1 King of color C and master card was in the trick belonging to an opponent X has no more color C
    if ((currentMaster != null) && (nextMaster != null)) {
        // somebody put the master on the table

        if (pli.any { it.card.value == currentMaster && it.card.color == color }) {
            // it was not the last card of the trick
            val nb = pli.indexOf(pli.first { it.card.value == currentMaster && it.card.color == color })

            if (nb < pli.size - 1) {

                //check if next opponent put a 10 or a King of the same color and it was the next master
                // if so, it was probably the last card of the color for this player

                // pPosition is the position of the player putting the master card
                val pPosition = pli[nb].position

                //for all folling cards in the trick
                for (index in (nb + 1) until pli.size) {

                    //if somebody put king or 10 and it was the next master
                    if ((pli[index].card.value == CardValue.TEN || pli[index].card.value == CardValue.KING) &&
                        (pli[index].card.value == nextMaster) &&
                        (pli[index].card.color == color) &&
                        ((pli[index].position == pPosition + 1) || (pli[index].position == pPosition + 3)) &&
                        // we should also check it was not a gift for the partner
                        winnerTrick != (pli[index].position + 2)) {
                        //Here we can reasonably say this player has no more of this color or is it bluff ???
                        allPlayerColor[pli[index].position] = false
                    }
                }
            }
        }
    }

    return allPlayerColor
}


/*
  R5 - heuristic
     If the Player X played a 10 or Ace of Color C when one of his opponent
     had put before, in the same trick a trump, and this trump was the stronger at this point
     then : we consider that player X has no more C
 */
fun colorRule5(color: CardColor, atout: CardColor,
               allPlayerColor: MutableMap<PlayerPosition, Boolean>,
               pli: List<CardPlayed>,
               pliColor: CardColor): MutableMap<PlayerPosition, Boolean> {

    if (pliColor != color) {
        // this rule is not applicable if the pli did not start with our color
        return allPlayerColor
    }
    val listBigValue =
            pli.filter { (it.card.value == CardValue.ACE || it.card.value == CardValue.TEN) && it.card.color == color }

    for (bigCard in listBigValue) {

        // it was not the first card of the trick
        val nb = pli.indexOf(pli.first { it.card.value == bigCard.card.value && it.card.color == color })

        // not applicable if this is the first card of the trick
        if (nb == 0) continue

        // pPosition is the position of the player putting the big card
        val pPosition = pli[nb].position

        //for all previous cards in the trick chcek if there is a trump and that master of all trumps is not our partner
        val listTrumpInTricks: MutableList<CardPlayed> = mutableListOf()
        for (index in 0 until nb) {
            if (pli[index].card.color == atout) {
                listTrumpInTricks.add(pli[index])
            }
        }
        if (listTrumpInTricks.isNotEmpty() && listTrumpInTricks.maxBy { it.card.value.dominanceAtout }!!.position != (pPosition + 2)) {

            // somebody before pposition did play trump and we still play 10 or Ace ???
            //Here we can reasonably say this player has no more of this color or is it bluff ???
            allPlayerColor[pPosition] = false
        }
    }



    return allPlayerColor
}

/*
    Rule 1 Trump
        if player X did not play Trump when atout was requested or if he played a different color from requested, but not trump when partner was not master
         then player has not more trump

 */
fun trumpRule1(atout: CardColor,
               allPlayerColor: MutableMap<PlayerPosition, Boolean>,
               pli: List<CardPlayed>,
               pliColor: CardColor,
               winnerTrick: PlayerPosition): MutableMap<PlayerPosition, Boolean> {

    pli.filter { it.card.color != pliColor && it.card.color != atout }.forEach { card ->
        when (pli.indexOf(card)) {
            // partner play after , player did not play trump it means that he doesn't have trump
            1 -> allPlayerColor[card.position] = false
            // check that partner was not
            3 -> if ((winnerTrick != card.position + 2) || (pliColor == atout)) {
                allPlayerColor[card.position] = false
            }
            2 -> if ((pliColor == atout) ||
                     (pli[1].card.color == atout) ||
                     ((pli[1].card.color == pliColor) && (pli[1].card.value.dominanceCouleur > pli[0].card.value.dominanceCouleur))) {
                allPlayerColor[card.position] = false
            }
        }
    }

    return allPlayerColor
}

/*
    Rule 3 Trump
        If player X played an trump with a value lower than stronger trump of the trick (A) (from cards played before X)
        then X has no trump with higer value than A
        In which case we have less value to apply rule 2

    Example : Heart is trump
      at Tour 1 North play   10 H
                East  play    8 H
                South play Jack H
                West play     7 H
      Later in the game (tour 3)
                           Queen H is played
       In addition North (player doing evaluation) has the King H in his game

       North can conclude that East has no more trump

    This rule is globale on all trick, so it needs to be evaluated on all tricks, not only trick per trick (similar to rule 2)

 */



fun trumpRule3(atout: CardColor,
               allPlayerColor: MutableMap<PlayerPosition, Boolean>,
               listPlis: List<List<CardPlayed>>,
               myPosition: PlayerPosition,
               cardsInHand: List<Card>): MutableMap<PlayerPosition, Boolean> {

    val nameFunction = object {}.javaClass.enclosingMethod.name

    // lets make a map for each player which indicates which cards are not in their hands
    val allPlayerTrump = PlayerPosition.values()
            .map { Pair(it, CardValue.values().map { value -> Pair(value, true) }.toMap().toMutableMap()) }.toMap()

    // Cards in my hands are not in other players hand !!!
    for (card in cardsInHand.filter { it.color == atout }) {
        for (position in PlayerPosition.values().filter { it != myPosition }) {
            (allPlayerTrump[position] ?: error("$nameFunction:${getLineNumber()} Impossible"))[card.value] = false
        }
    }
    // iterate over all plis
    for (pli in listPlis.filter { aPli -> aPli.any { it.card.color == atout } }) {
        // select card which does not respect order - max Pli will be the current max value
        var maxPli = -1
        // iterate over the card in the pli which are trump
        for (card in pli.filter { it.card.color == atout }) {
            if (card.card.value.dominanceAtout < maxPli) {
                // here we did not respect atout order
                for (value in CardValue.values().filter { it.dominanceAtout >= maxPli }) {
                    (allPlayerTrump[card.position] ?: error("$nameFunction:${getLineNumber()} Impossible"))[value] =
                            false
                }
            } else {
                // we have a new max :
                maxPli = card.card.value.dominanceAtout
            }
            for (position in PlayerPosition.values()) {
                (allPlayerTrump[position] ?: error("$nameFunction:${getLineNumber()} Impossible"))[card.card.value] =
                        false
            }
        }
    }
    for (position in PlayerPosition.values()) {
        if ((allPlayerTrump[position] ?: error("$nameFunction:${getLineNumber()} Impossible")).none { it.value }) {
            allPlayerColor[position] = false
        }
    }
    return allPlayerColor
}

/*
    Rule 4 Trump - Heuristic

    (This rule is similar to the rule 3 for Color)
     If player X played a 9 or As or 10 of trump and it was a card close to the master card , and this card belong to adversary
     we consider player X has no more trump
     Same rule as rule 3 for color, but applied to 9, ace and 10

 */

fun trumpRule4(atout: CardColor,
               allPlayerColor: MutableMap<PlayerPosition, Boolean>,
               realGamePlayer: Map<PlayerPosition, MutableMap<CardValue, CardInPlayerHand>>,
               myCards: List<Card>,
               pli: List<CardPlayed>,
               remainingBigTrump: List<CardValue>): MutableMap<PlayerPosition, Boolean> {
    val nameFunction = object {}.javaClass.enclosingMethod.name

    // if there are no trump in this pli, this function is not relevant
    if (pli.none { it.card.color == atout }) return allPlayerColor

    // we have trump  in the pli so we can compute the max
    val maxAtoutPli = pli.filter { it.card.color == atout }.maxBy { it.card.value.dominanceAtout }!!.card.value
    val iterationAtout =
            remainingBigTrump.filter { it.dominanceAtout <= maxAtoutPli.dominanceAtout && myCards.none { card -> card.color == atout && card.value == it } }
                    .sortedByDescending { it.dominanceAtout }
    var maxCurValue = CardValue.SEVEN
    var maxPosition: PlayerPosition? = null

    val maxPotCardInPlayerHand = PlayerPosition.values()
            .map {
                Pair(it,
                     realGamePlayer[it]!!.filter { e -> e.value != CardInPlayerHand.NO && remainingBigTrump.any { bigAtout -> bigAtout == e.key } }
                             .toList().map { hand -> hand.first }.maxBy { cardValue -> cardValue.dominanceAtout })
            }.toMap()
    for (card in pli.filter { it.card.color == atout }) {
        if (maxCurValue.dominanceAtout <= card.card.value.dominanceAtout) {
            maxCurValue = card.card.value
            maxPosition = card.position
        }
        if ((card.card.value.dominanceAtout < maxCurValue.dominanceAtout) &&
            (card.position != maxPosition!! + 2) &&
            (maxPotCardInPlayerHand[card.position] != null) &&
            (card.card.value == maxPotCardInPlayerHand[card.position])) {
            for (value in CardValue.values()
                    .filter { it.dominanceAtout < maxPotCardInPlayerHand[card.position]!!.dominanceAtout }) {
                (realGamePlayer[card.position] ?: error("$nameFunction:${getLineNumber()} Impossible"))[value] =
                        CardInPlayerHand.NO
            }
        }
    }
    // new methods to compute a;;PlayerColor using realFamePlayer map


    for (index in 0..iterationAtout.size - 2) {
        if (pli.any { it.card.value == iterationAtout[index] && it.card.color == atout } && pli.any { it.card.value == iterationAtout[index + 1] && it.card.color == atout }) {
            val cCard = pli.first { it.card.value == iterationAtout[index + 1] && it.card.color == atout }
            val cIndex = pli.indexOf(cCard)
            val curMax = pli.filter { it.card.color == atout && pli.indexOf(it) <= cIndex }
                    .maxBy { e -> e.card.value.dominanceAtout }!!
            if ((pli.indexOf(curMax) < pli.indexOf(cCard)) && (curMax.position != cCard.position + 2)) {

                allPlayerColor[cCard.position] = false
            }
        }

    }



    return allPlayerColor
}

/*
    Rule 5 Trump - Heuristic
    similar to R4 color - to apply only if myPosition+ 2 took the bid, and only for opponent players
     If player X (an opponent) played the 9 of trump and firs card of trick was trump and jack was not already played then consider that plyer  X has no trump lower than 9 and higher than minimu cvard to play.
      Example : Trump is Heart
                        Tour 1 : North play Queen Heart
                                 East play 9 Heart
                                 South play Jack Heart
                                 West play 7 Heart
                        Tour 2 8 Heart is played

                         hence no trump lower than Queen exists in X game ... and there is no existing trump lower tha Quee (7 and 8 are down )
                         so East has no longer trump
 */
fun trumpRule5(atout: CardColor,
               allPlayerColor: MutableMap<PlayerPosition, Boolean>,
               preneur: PlayerPosition,
               realGamePlayer: Map<PlayerPosition, MutableMap<CardValue, CardInPlayerHand>>,
               cardsInHand: List<Card>,
               pli: List<CardPlayed>,
               allRemainingTrump: List<CardValue>): MutableMap<PlayerPosition, Boolean> {

    // if the pli was not about atout we cannot apply the rule
    // Rule apply only if jack is still in game hence at least one of the players can have the nine

    if ((pli[0].card.color != atout) || ((allRemainingTrump.none { it == CardValue.JACK }) && (cardsInHand.none { it.value == CardValue.JACK })))
        return allPlayerColor

    // this rule is relevant only to evaluate the defense team.
    val nineCard =
            pli.firstOrNull {
                it.card.color == atout && it.card.value == CardValue.NINE &&
                (it.position == preneur + 1 || it.position == preneur + 3)
            }
            ?: return allPlayerColor
    val ninePos = pli.indexOf(nineCard)


    if (ninePos == 3) {
        // no conclusion to take, it was the last card of the trick
        return allPlayerColor
    }
    var maxAtoutBeforeNine = CardValue.SEVEN

    for (index in 0 until ninePos) {
        if (pli[index].card.color == atout && pli[index].card.value.dominanceAtout >= maxAtoutBeforeNine.dominanceAtout) {
            maxAtoutBeforeNine = pli[index].card.value

        }
    }
    for (value in CardValue.values()
            .filter {
                (it.dominanceAtout > maxAtoutBeforeNine.dominanceAtout) and
                        (it.dominanceAtout < CardValue.NINE.dominanceAtout)
            }) {
        realGamePlayer[nineCard.position]!![value] = CardInPlayerHand.NO
    }
    return allPlayerColor

}

/*
    aR6 - Rule 6 Trump - Heuristic
    if the initial bid is >= 100  and only 1 trump is outside (including those in cardsInHand )
    we consider that remaining trump are in the attack team and most probably in partner of taker
    and defense team has not anymore trump
    same thing if there are 2 remaining trumps and initial bid was >= 120 .
    To be evaluated only once, after all other rules
 */
fun trumpRule6(allPlayerColor: MutableMap<PlayerPosition, Boolean>,
               preneur: PlayerPosition,
               points: Int,
               realGamePlayer: Map<PlayerPosition, MutableMap<CardValue, CardInPlayerHand>>,
               allRemainingTrump: List<CardValue>): MutableMap<PlayerPosition, Boolean> {

    if ((allRemainingTrump.size == 1) || (allRemainingTrump.size == 2 && points >= 120)) {
        CardValue.values().forEach { value ->
            realGamePlayer[preneur + 1]!![value] = CardInPlayerHand.NO
            realGamePlayer[preneur + 3]!![value] = CardInPlayerHand.NO
        }

        allPlayerColor[preneur + 1] = false
        allPlayerColor[preneur + 3] = false
    }
    return allPlayerColor

}

/*
    aR7 - Rule 7 Trump - Heuristic
     If the initial bid was >= 110 and the attacking team already played one tour of trump
                                                (played a trump as a first card of a trick)
                                    and 2 or more  trumps are for sure not from the hand of the taker outside the tours
                                                ( either in my hand or played by anoher player)
        then we consider that all remaining trump belong to the taker

        same rule apply with bid >= 120 et 1 trump outside.

 */
fun trumpRule7(atout: CardColor,
               allPlayerColor: MutableMap<PlayerPosition, Boolean>,
               cardsInHand: List<Card>,
               plis: List<List<CardPlayed>>,
               preneur: PlayerPosition,
               points: Int
): MutableMap<PlayerPosition, Boolean> {

    val nbTourAtout =
            plis.filter { pli -> pli[0].card.color == atout && (pli[0].position == preneur || pli[0].position == preneur + 2) }.size

    val nbAtoutsInMyHand = cardsInHand.filter { it.color == atout }.size
    val nbAtoutPlayedPlus =
            plis.flatten().filter { it.card.color == atout }.size + cardsInHand.filter { it.color == atout }.size

    // all cards , not from the first tour of trump initiated by the attacking team , not played by taker

    val nbTotalAtoutOutside = plis.flatten().filter { it.card.color == atout && it.position != preneur }.size
    val nbAtoutFirstPliAtout = plis.filter { pli -> pli[0].card.color == atout && (pli[0].position == preneur || pli[0].position == preneur + 2) }.first().filter{it.card.color == atout && it.position != preneur}.size
    val nbAtoutOutside = nbTotalAtoutOutside - nbAtoutFirstPliAtout

    val threshold = if (points >= 120) 1 else 2

    val nbBelotePlayed = plis.flatten()
            .filter { card -> card.belote != BeloteValue.NONE && card.position != preneur && card.position != preneur + 2 }.size

    // We apply the heuristic : no trump for the defense team
    // We eliminate the case where defense team had belote but not yet rebelote
    if (((nbAtoutOutside + nbAtoutsInMyHand) >= threshold) &&
        (nbAtoutPlayedPlus >= 6) &&
        (nbTourAtout >= 1) &&
        (nbBelotePlayed != 1)) {
        allPlayerColor[preneur + 1] = false
        allPlayerColor[preneur + 3] = false
    }
    return allPlayerColor

}

fun playersHaveColor(color: CardColor, atout: CardColor,
                     firstBid: Bid,
                     listPlis: List<List<CardPlayed>>,
                     myPosition: PlayerPosition,
                     cardsInHand: List<Card>): Map<PlayerPosition, Boolean> {

    val unk = CardInPlayerHand.UNK
    val yes = CardInPlayerHand.YES
    val no = CardInPlayerHand.NO
    val preneur = firstBid.position
    // By default all players have all colors
    var allPlayerColor = PlayerPosition.values().map { Pair(it, true) }.toMap().toMutableMap()
    val cR1 = true
    val cR2 = true // probably to disable for trump since rule 3 will do it in a better way
    val cR3 = (color != atout)
    val rR4 = (color != atout)
    val cR5 = (color != atout)
    val tR1 = (color == atout)
    val tR2 = (color == atout)
    val tR3 = (color == atout)
    val tR4 = (color == atout)
    //Rule 5 for trump is relevant only if partner took the first bid and only to evaluate opponents
    val tR5 = (color == atout)
    val tR6 = (color == atout) && (preneur == myPosition + 2) && (firstBid.curPoint() >= 100)
    val tR7 = (color == atout) && (preneur == myPosition + 2) && (firstBid.curPoint() >= 110)


    val realGamePlayers = PlayerPosition.values().map { position ->
        Pair(position, CardValue.values().map { value -> Pair(value, unk) }.toMap().toMutableMap())
    }.toMap()
    for (card in cardsInHand.filter { it.color == color }) {
        for (position in PlayerPosition.values()) {
            realGamePlayers[position]!![card.value] = when (position) {
                myPosition -> yes
                else -> no
            }
        }
    }

    // remove the cards played from the list of card in game

    for (pli in listPlis) {
        for (card in pli.filter { it.card.color == color }) {
            for (position in PlayerPosition.values()) {
                realGamePlayers[position]!![card.card.value] = when (position) {
                    card.position -> yes
                    else -> no
                }
                // special case for belote and rebelote : if a plyer did announce belote with Queen nobody else can have the KING
                // and vice-versa
                if (card.belote != BeloteValue.NONE) {
                    // this player did declare belote --> nobody else can have king or queen of atout
                    for (oPosition in PlayerPosition.values().filter { it != card.position }) {
                        realGamePlayers[oPosition]!![CardValue.KING] = no
                        realGamePlayers[oPosition]!![CardValue.QUEEN] = no
                    }
                    val other = if (card.card.value == CardValue.QUEEN) CardValue.KING else CardValue.QUEEN
                    if (realGamePlayers[card.position]!![other] == unk)
                        realGamePlayers[card.position]!![other] = yes

                }
            }
        }
    }
    // R2 - If all cards ( including mine) of color C were played, than all X player are missing the color
    if (cR2) {
        allPlayerColor = colorRule2(color, allPlayerColor, listPlis, cardsInHand)
    }

    // R3 Trump
    if (tR3) {
        allPlayerColor = trumpRule3(atout, allPlayerColor, listPlis, myPosition, cardsInHand)
    }
    // Now we use heuristic rules ( not 100% right but good enough to play using this "a priori"
    // Generally heuristic rules need to be applied for all plis one after the other

    val presenceInGameCardValue: MutableMap<CardValue, Boolean> =
            CardValue.values().map { Pair(it, true) }.toMap().toMutableMap()
    cardsInHand.filter { it.color == color }.forEach { e -> presenceInGameCardValue[e.value] = false }

    for (pli in listPlis) {
        var master: CardValue?
        var nextMaster: CardValue? = null
        val listRemainingCardinColor: List<CardValue>

        when (atout) {
            color -> {
                listRemainingCardinColor = presenceInGameCardValue.filterValues { it }.keys.toList()
                        .sortedBy { e -> -e.dominanceAtout }
                master = listRemainingCardinColor.firstOrNull()
                if (listRemainingCardinColor.size >= 2) {
                    nextMaster = listRemainingCardinColor[1]
                }
            }
            else -> {
                listRemainingCardinColor = presenceInGameCardValue.filterValues { it }.keys.toList()
                        .sortedBy { e -> -e.dominanceCouleur }

                master = listRemainingCardinColor.firstOrNull()
                if (listRemainingCardinColor.size >= 2) {
                    nextMaster = listRemainingCardinColor[1]
                }
            }
        }
        val winnerTrick = calculateWinnerTrick(pli, atout)
        val pliColor = pli.first().card.color
        if (cR1) {
            allPlayerColor = colorRule1(color, allPlayerColor, pli, pliColor)
        }
        if (cR3) {
            allPlayerColor = colorRule3(color, allPlayerColor, pli, master, nextMaster, winnerTrick)
        }
        if (cR5) {
            allPlayerColor = colorRule5(color, atout, allPlayerColor, pli, pliColor)
        }
        if (tR1) {
            trumpRule1(atout, allPlayerColor, pli, pliColor, winnerTrick)
        }
        if (tR4) {
            allPlayerColor = trumpRule4(color, allPlayerColor, realGamePlayers, cardsInHand, pli,
                                        presenceInGameCardValue.filter { it.key.dominanceAtout >= CardValue.QUEEN.dominanceAtout && it.value }
                                                .map { it.key })
        }
        if (tR5) {
            allPlayerColor = trumpRule5(color, allPlayerColor, preneur, realGamePlayers, cardsInHand, pli,
                                        presenceInGameCardValue.filter { it.value }
                                                .map { it.key })
        }

        // the card was played , it's not anymore a master card

        pli.filter { it.card.color == color }.forEach { e -> presenceInGameCardValue[e.card.value] = false }
        if (presenceInGameCardValue.filter { it.value }.none()) {
            PlayerPosition.values().forEach { e -> allPlayerColor[e] = false }
            break
        }
    }
    for (position in PlayerPosition.values()) {

        if (realGamePlayers[position]!!.none { (it.value == yes || it.value == unk) && (presenceInGameCardValue[it.key]!!) }) {
            allPlayerColor[position] = false
        }
    }
    if (tR6) {
        allPlayerColor = trumpRule6(allPlayerColor, preneur, firstBid.curPoint(), realGamePlayers,
                                    presenceInGameCardValue.filter { it.value }
                                            .map { it.key })
    }
    if (tR7) {
        allPlayerColor = trumpRule7(atout, allPlayerColor, cardsInHand, listPlis, preneur, firstBid.curPoint())
    }
    // for my self I know my cards .... so no need to use any heuristic
    allPlayerColor[myPosition] = cardsInHand.any { it.color == color }
    return allPlayerColor

}

fun playRandom(cardsInHand: List<Card>): Card {
    val nameFunction = object {}.javaClass.enclosingMethod.name
    val aCard: Card = cardsInHand.filter { it.playable == true }.random()
    debugPrintln(dbgLevel.REGULAR,
                 "$nameFunction:${getLineNumber()} we play $aCard  OOPS this is randomly played **************************************")

    return aCard
}

fun whatToPlay(myPosition: PlayerPosition,
               cardsInHand: MutableList<Card>,
               bids: MutableList<Bid>,
               atout: CardColor,
               onTable: MutableList<CardPlayed>,
               plisNS: MutableMap<Int, List<CardPlayed>>,
               plisEW: MutableMap<Int, List<CardPlayed>>): Card? {

    val nameFunction = object {}.javaClass.enclosingMethod.name
    if (cardsInHand.size == 1) return cardsInHand.first()

    // Create the list of all cards played ( on table + pliNS + pliEW
    // They can be filtered by player position etc ....

    val nbPlis = plisEW.size + plisNS.size
    var currentPli: Map<Int, MutableList<CardPlayed>> = mapOf()
    if (onTable.isNotEmpty()) {
        currentPli = listOf(Pair(nbPlis, onTable)).toMap()
    }
    val allCardPli = (plisEW + plisNS + currentPli).toSortedMap().map { it.value }
    val allRealPli = (plisEW + plisNS).toSortedMap().map { it.value }
    val currBid = getCurrentBid(bids)

    // Compute what cards can make the trick (used my firstToPlay and nthToPlay

    val list3Dominantes = CardColor.values().filter { it != atout }
            .map { col -> Pair(col, masterColor(col, atout, allRealPli.flatten())) }.toMap()
    val maxAtout = masterColor(atout, atout, allRealPli.flatten())
    //This is the list of cards which make a tricks for sure and which are in my hand
    val listMyDominantes =
            cardsInHand.filter { (list3Dominantes[it.color] != null) && it.isSimilar(list3Dominantes[it.color]!!) }
    // Check where we are in terms of points :
    val currentScore = when (myPosition) {
        PlayerPosition.SOUTH,
        PlayerPosition.NORTH -> calculateScoreTricks(plisNS.toList().map { it.second },
                                                     plisEW.toList().map { it.second },
                                                     myPosition + 1,
                                                     currBid).northSouth
        else -> calculateScoreTricks(plisNS.toList().map { it.second }, plisEW.toList().map { it.second },
                                     myPosition + 1,
                                     currBid).eastWest
    }
    val missingPoints = currBid.curPoint() - currentScore
    // Decide what function to call

    // first : who took first this color and with how many points ?
    val bidPreneur = bids.first { !(it is Pass) && it.curColor() == atout }
    val preneur = bidPreneur.position
    val pointsPreneur = bidPreneur.curPoint()

    when (preneur) {
        // My Partner is the first announcer for this color
        myPosition + 2 -> {
            if (onTable.size == 0) {
                //it's my turn to play
                debugPrintln(dbgLevel.DEBUG, "$nameFunction:${getLineNumber()} calling first to play ")
                return firstToPlay(myPosition, cardsInHand, bidPreneur, pointsPreneur, bids, atout,
                                   allCardPli, list3Dominantes, listMyDominantes)
            } else {
                debugPrintln(dbgLevel.DEBUG, "$nameFunction:${getLineNumber()} calling nth to play ")
                return nthToPlay(myPosition, cardsInHand, bidPreneur, atout, allCardPli,
                                 missingPoints, currBid, list3Dominantes, listMyDominantes, maxAtout)

            }
        }
        // I am the first announce for this color
        myPosition -> {

        }
        // The other team took the announce
        else -> {

        }
    }


    // playersHaveColor(CardColor.DIAMOND, atout, allCardPli, myPosition, cardsInHand)
    return null
}

/*
       ************Si je suis le 1er du pli à jouer une carte

Si 1 adversaire ou plus a encore de l’atout (déterminé avec la fct)
Si j’ai de l’atout {
    Si annonce initiale partenaire == 80 {
    Je joue le plus petit atout possible
       }
Sinon Si le valet est tombé {
    Je joue l’atout le plus fort
       }
Sinon je joue l’atout le plus fort, 9 excepté, si je n’ai plus que le 9 je considère que je n’ai pas d’atout, je sors du si j’ai de l’atout
   }

Sinon (=je n’ai pas d’atout) {
    Si je possède une carte dominante dans une couleur où il reste 5 cartes dehors ou plus (mes cartes sont considérées tombées donc PAS dehors) ou que c’est une couleur préalablement annoncée par un des adversaires {
            Je joue cette carte. Si j’ai plusieurs choix, je choisis en priorité la couleur annoncée par l’adversaire puis celle où il y a le plus de cartes dehors puis si égalité celle qui vaut le plus de points.
}
    Sinon (= je n’ai ni atout ni carte dominante qui pourrait passer) {
            Je joue une carte selon l’ordre de priorité suivant (le but ici est de choisir une carte qui a potentiellement plus de chance de faire couper, sans pour autant sacrifier trop de points)  :
            1. 10 sec c’est un non, j’élimine
            2. Si j’ai une carte d’une couleur c où selon mes estimations, le joueur suivant n’a plus de la couleur c je joue la carte la plus faible de la couleur c.
            3. J’élimine les cartes d’une couleur ayant préalablement été annoncée par un des adversaires si elle existe.
            4. Si je n’ai pas encore choisi ma carte, plusieurs cas s’offrent à moi : [
Soit il me reste 0 cartes, dans ce cas je reconsidère toutes mes cartes, je joue la carte valide de la couleur adversaire la plus forte, non 10, en priorité, si je n’en ai pas je joue la carte dominante où il reste le plus de cartes dehors, si je n’en ai pas je joue au hasard parmi toutes mes cartes restantes.
Soit il me reste 1 carte ou plus, je trie mes cartes restantes selon 2 critères (tri de type lexicographique), 1er critère d’importance la couleur, si une couleur possède moins de cartes restantes dehors qu’une autre elle passe devant. En cas d’égalité, 2e critère, je choisis la couleur dont je possède la carte la plus forte relativement aux cartes restantes. Puis dans cette couleur je choisis la carte la plus forte. Exemple : Parmi mes cartes non-éliminées il me reste 9 et Dame de pique et Valet de carreau. D’après les calculs on a trouvé qu’il reste 3 carreaux dehors et 3 piques dehors. On doit donc les départager selon le 2e critère. Les calculs nous ont par ailleurs dit qu’il restait Roi, 9 et 7 de carreau dehors et 7, 10 et As de pique dehors. Puisque notre valet de carreau et à 1 cran d’écart de la carte dominante alors que la dame de pique et à 2 crans de la carte dominante, on choisit la couleur carreau. On prend alors la carte la plus forte de la couleur ici on a que le valet donc voilà.
]

}
    }

Sinon (= les adversaires n’ont a priori plus d’atouts)
Si il existe une couleur c non-atout à laquelle je possède la carte dominante {
    Parmi toutes les couleurs répondant à ce critère, je joue en priorité :
1. une couleur où mon partenaire n’a a priori plus de cartes
2. Celle qui vaut le + de points
3. Celle où il reste le moins de cartes dehors
}

Sinon (=je n’ai pas de maîtres non atout) {
J’élimine les 10
J’élimine les couleurs où le partenaire n’a a priori plus de cartes
J’élimine les couleurs annoncées par les adversaires

    Après ces étapes d’élimination, si il me reste 0 cartes, je reprends les étapes précédentes à l’envers une par une c’est à dire si en ne faisant que 1. et 2. J’ai encore des cartes je continue à l’étape en dessous, sinon je ne fais que 2. Etc…

Il me reste donc maintenant des cartes (soit en ayant fait les 3 critères ou juste une partie de ces critères), Parmi les couleurs restante je choisis celle où je possède la carte la plus forte relativement aux cartes restantes (voir l’exemple 1 page avant)
}

 */
fun firstToPlay(myPosition: PlayerPosition,
                cardsInHand: MutableList<Card>,
                bidPreneur: Bid,
                pointsFirstAnnounce: Int,
                bids: MutableList<Bid>,
                atout: CardColor,
                allCardPli: List<List<CardPlayed>>,
                list3Dominantes: Map<CardColor, Card?>,
                listMyDominantes: List<Card>
): Card? {
/*
Si 1 adversaire ou plus a encore de l’atout (déterminé avec la fct)
Si j’ai de l’atout {
    Si annonce initiale partenaire == 80 {
    Je joue le plus petit atout possible
}
Sinon Si le valet est tombé {
    Je joue l’atout le plus fort
}
Sinon je joue l’atout le plus fort, 9 excepté, si je n’ai plus que le 9 je considère que je n’ai pas d’atout, je sors du si j’ai de l’atout
}

 */
    val nameFunction = object {}.javaClass.enclosingMethod.name

    // Determine who has still what color from differents heuristics + data
    val mapColorPlayerRemain =
            CardColor.values()
                    .map { e -> Pair(e, playersHaveColor(e, atout, bidPreneur, allCardPli, myPosition, cardsInHand)) }
                    .toMap()

    // this will be the card we will return
    var aCard: Card?

    // These are the colors announced by the other team ...
    val colorOfEnemy = CardColor.values()
            .filter { color -> (bids.any { bid -> (color == bid.curColor()) && (bid.position == (myPosition + 1) || (bid.position == (myPosition + 3))) }) && (color != atout) }

    val pAtouts = mapColorPlayerRemain[atout]!!
    // We will use this function a lot ... so lets call it once for all and store the results for all colors
    // this is a map by color of remaining cards in each color
    val remainingColorMap =
            CardColor.values().map { Pair(it, totalRemainingNumber(it, allCardPli.flatten(), cardsInHand)) }.toMap()

    // the other team has still trumps ?
    if (pAtouts[myPosition + 1] ?: error("Impossible Error in  $nameFunction 1") || pAtouts[myPosition + 3] ?: error(
                    "$nameFunction:${getLineNumber()} Impossible")) {
        // Do I have trumps ?
        if (pAtouts[myPosition] ?: error("$nameFunction:${getLineNumber()} Impossible")) {
            // first announce was 80 ?
            if (pointsFirstAnnounce == 80) {
                // let's the smallest trump in my cards
                aCard = cardsInHand.filter { it.color == atout }.minBy { it.value.dominanceAtout }!!
                debugPrintln(dbgLevel.DEBUG,
                             "$nameFunction:${getLineNumber()} we play $aCard with criteria smallest trump we have")
                return aCard
            } else {
                //If the jack was already played
                if (allCardPli.flatten().any { (it.card.color == atout) && (it.card.value == CardValue.JACK) }) {
                    // we play the strongest card
                    aCard = cardsInHand.filter { it.color == atout }.maxBy { it.value.dominanceAtout }!!
                    debugPrintln(dbgLevel.DEBUG,
                                 "$nameFunction:${getLineNumber()} : we play $aCard with criteria strongest trump we have")
                    return aCard
                } else {
                    // we play astrongest atrump but the 9
                    aCard = cardsInHand.filter { (it.color == atout) && (it.value != CardValue.NINE) }
                            .maxBy { it.value.dominanceAtout }
                    if (aCard != null) {
                        debugPrintln(dbgLevel.DEBUG,
                                     "$nameFunction:${getLineNumber()} : we play $aCard with criteria strongest trump we have but the 9")
                        return aCard
                    }
                }
            }
        }


        // Here we consider we dont have any trump ( or only the nine with the JACK still in play and our partner choose 80 pts
        /*
    Sinon (=je n’ai pas d’atout) {
    Si je possède une carte dominante dans une couleur où il reste 5 cartes dehors ou plus (mes cartes sont considérées tombées donc PAS dehors) ou que c’est une couleur préalablement annoncée par un des adversaires {
            Je joue cette carte. Si j’ai plusieurs choix, je choisis en priorité la couleur annoncée par l’adversaire puis celle où il y a le plus de cartes dehors puis si égalité celle qui vaut le plus de points.}

     */

        //Triple with card, Bid (1==bid, else 0), nb remaining
        val candidateOrdered: MutableList<Triple<Card, Int, Int>> = mutableListOf()
        for (card in listMyDominantes) {
            if (bids.any { (it.curColor() == card.color) && ((it.position == myPosition + 1) || (it.position == myPosition + 3)) }) {
                candidateOrdered.add(element = Triple(card, 0, 0), index = 0)
            } else {

                val nbRemain = remainingColorMap[card.color] ?: error("$nameFunction:${getLineNumber()} Impossible")
                if (nbRemain >= 5) {
                    candidateOrdered.add(element = Triple(card, 1, -nbRemain))
                }
            }

        }
        // candidateOrdered is the list of dominant in my hand with less than 5 cards outside or bid was done by the other team
        if (candidateOrdered.isNotEmpty()) {
            val myTriple = candidateOrdered.sortedWith(
                    compareBy({ it.second }, { it.third }, { -it.first.value.dominanceCouleur })).first()
            debugPrintln(dbgLevel.DEBUG,
                         "$nameFunction:${getLineNumber()} we play $myTriple.first  with criteria first to play - no trump $myTriple")
            return myTriple.first
        } else {
            // we dont have any card which can make the next trick for sure
            /*
                Sinon (= je n’ai ni atout ni carte dominante qui pourrait passer) {
                Je joue une carte selon l’ordre de priorité suivant (le but ici est de choisir une carte qui a potentiellement plus de chance de faire couper, sans pour autant sacrifier trop de points)  :
                    1. 10 sec c’est un non, j’élimine
                    2. Si j’ai une carte d’une couleur c où selon mes estimations, le joueur suivant n’a plus de la couleur c je joue la carte la plus faible de la couleur c.
                    3. J’élimine les cartes d’une couleur ayant préalablement été annoncée par un des adversaires si elle existe.
                    4. Si je n’ai pas encore choisi ma carte, plusieurs cas s’offrent à moi : [
            * Soit il me reste 0 cartes,
                dans ce cas je reconsidère toutes mes cartes,
                je joue la carte valide de la couleur adversaire la plus forte, non 10, en priorité,
                si je n’en ai pas je joue la carte dominante où il reste le plus de cartes dehors,
                si je n’en ai pas je joue au hasard parmi toutes mes cartes restantes.

            * Soit il me reste 1 carte ou plus,
                je trie mes cartes restantes selon 2 critères (tri de type lexicographique), 1er critère d’importance
                la couleur, si une couleur possède moins de cartes restantes dehors qu’une autre elle passe devant.
                En cas d’égalité, 2e critère, je choisis la couleur dont je possède la carte la plus forte relativement aux cartes restantes.
                Puis dans cette couleur je choisis la carte la plus forte.

                Exemple : Parmi mes cartes non-éliminées il me reste 9 et Dame de pique et Valet de carreau.
                D’après les calculs on a trouvé qu’il reste 3 carreaux dehors et 3 piques dehors. On doit donc les départager selon le 2e critère.
                Les calculs nous ont par ailleurs dit qu’il restait Roi, 9 et 7 de carreau dehors et 7, 10 et As de pique dehors.
                Puisque notre valet de carreau et à 1 cran d’écart de la carte dominante alors que la dame de pique et à 2 crans de la carte dominante,
                on choisit la couleur carreau. On prend alors la carte la plus forte de la couleur ici on a que le valet donc voilà.
                ]

            }

 */
            val mapColor = colorInMyGame(cardsInHand)
            // let's copy cards to remove the one we dont want to play from the list
            // e.g. we dont want the ten if alone in their color
            val localCards =
                    cardsInHand.filter { e -> ((e.value != CardValue.TEN) || (mapColor[e.color] != 1)) }.toMutableList()


            //map of color present in the hand of next player but trump
            val otherPlayerColor = localCards.filter { it.color != atout }.map { card ->
                Pair(card.color,
                     playersHaveColor(color = card.color, atout = atout, firstBid = bidPreneur,
                                      listPlis = allCardPli, myPosition = myPosition,
                                      cardsInHand = cardsInHand)[myPosition + 1])
            }.toMap()

            // this is the list of color candidate for playing in this case ( color not in hand of next player)
            val listColorPlayable = mapColor.filter {
                (it.key != atout) && (it.value != 0) && (!(otherPlayerColor[it.key] ?: error(
                        "Impossible $nameFunction 6")))
            }.map { it.key }
            // and we take the first candidate in order ( smallest card)
            aCard = listColorPlayable.map { localCards.filter { e -> it == e.color } }.flatten()
                    .minBy { it.value.dominanceCouleur }
            if (aCard != null) {
                debugPrintln(dbgLevel.DEBUG,
                             "$nameFunction:${getLineNumber()} we play $aCard it is a color cards where probably next player will have to play trump")
                return aCard
            }
            //
            //  lets remove cards from color referenced in bids by the other team

            // first determine these color(s)


            // check the cards we have in hand ( except 10 alone and trump) that are not in these colors
            var newCandidate = localCards.filter { e -> !colorOfEnemy.contains(e.color) }
            if (newCandidate.isNotEmpty()) {
                val tempList = newCandidate.sortedWith(
                        compareBy({ remainingColorMap[it.color] },
                                  { (list3Dominantes[it.color]!!.value.dominanceCouleur - it.value.dominanceCouleur) }))
                aCard = tempList.first()
                debugPrintln(dbgLevel.DEBUG,
                             "$nameFunction:${getLineNumber()} we play $aCard , not a 10 alone, less cards / strongest ")

                return aCard

            }

            // Reevaluate all cards in hand but trump and all 10
            newCandidate = localCards.filter { e ->
                (colorOfEnemy.contains(e.color) && (e.value != CardValue.TEN) && (list3Dominantes[e.color]!!.isSimilar(
                        e)))
            }.sortedWith(
                    compareBy { -remainingColorMap[it.color]!! })
            return if (newCandidate.isNotEmpty()) {
                aCard = newCandidate.first()
                debugPrintln(dbgLevel.DEBUG,
                             "$nameFunction:${getLineNumber()} we play $aCard , not a 10 color with the most cards outside ")
                aCard
            } else {
                // just play randomly
                aCard = localCards.random()
                debugPrintln(dbgLevel.DEBUG,
                             "$nameFunction:${getLineNumber()} we play $aCard , randomly ")
                aCard
            }
        }
    } else {
        // The other team has no trumps
        /*
        Si il existe une couleur c non-atout à laquelle je possède la carte dominante {
            Parmi toutes les couleurs répondant à ce critère, je joue en priorité :
            1. une couleur où mon partenaire n’a a priori plus de cartes
            2. Celle qui vaut le + de points
                    3. Celle où il reste le moins de cartes dehors
        }

        Sinon (=je n’ai pas de maîtres non atout) {
            J’élimine les 10
            J’élimine les couleurs où le partenaire n’a a priori plus de cartes
            J’élimine les couleurs annoncées par les adversaires

            Après ces étapes d’élimination, si il me reste 0 cartes, je reprends les étapes précédentes à l’envers une par une c’est à dire si en ne faisant que 1. et 2. J’ai encore des cartes je continue à l’étape en dessous, sinon je ne fais que 1. Etc…

            Il me reste donc maintenant des cartes (soit en ayant fait les 3 critères ou juste une partie de ces critères), Parmi les couleurs restante je choisis celle où je possède la carte la plus forte relativement aux cartes restantes (voir l’exemple 1 page avant)
        }
        */
        //let's see the list of card who make the tricks in the differents colors


        // check if I own one of those cards


        // it exists a color non atout where I have a "master" card
        // sort it by color where my partner has no more cards, the card with the more points , the color where there is the less cards outside
        // We use a temp variable for debugging purpose
        val listMyDominantesSorted = listMyDominantes.sortedWith(
                compareBy({ (!mapColorPlayerRemain[it.color]!![myPosition + 2]!!) }, { -it.value.dominanceCouleur },
                          { remainingColorMap[it.color] }))
        aCard =
                listMyDominantesSorted.firstOrNull()
        if (aCard != null) {
            debugPrintln(dbgLevel.DEBUG,
                         "$nameFunction:${getLineNumber()} we play $aCard which is probably the strongest card")

            return aCard
        }

        // If we are here it means that there have no master cards outside trump

        var listCandidate =
                cardsInHand.filter { e -> (e.value != CardValue.TEN) && colorOfEnemy.none { it == e.color } && mapColorPlayerRemain[e.color]!![myPosition + 2]!! }
        if (listCandidate.isEmpty()) {
            listCandidate =
                    cardsInHand.filter { e -> (e.value != CardValue.TEN) && mapColorPlayerRemain[e.color]!![myPosition + 2]!! }
            if (listCandidate.isEmpty()) {
                listCandidate = cardsInHand.filter { e -> (e.value != CardValue.TEN) }
                if (list3Dominantes.isEmpty()) {
                    listCandidate = cardsInHand
                }
            }
        }
        aCard = listCandidate.sortedWith(compareBy({ remainingColorMap[it.color] },
                                                   { (list3Dominantes[it.color]!!.value.dominanceCouleur - it.value.dominanceCouleur) }))
                .first()
        debugPrintln(dbgLevel.DEBUG,
                     "$nameFunction:${getLineNumber()} we play $aCard there are no mastercards in our hand")

        return aCard
    }
}


/*
            III Si je ne suis pas le 1er à jouer
                    A) Si la 1ère carte est de l’atout et que j’en ai :
                        Si le partenaire a déjà mis le valet dans ce pli et que j’ai l’As ou le 10, je le pose (l’As si
                        j’ai les 2). Sinon si j’ai le 9 et que le V est déjà tombé, je le joue.
                        Sinon je joue l’atout le + bas autorisé.
                    B) Si la 1ère carte est de la couleur et que j’en ai
                        1er cas : Si je suis le dernier à jouer
                                        - Si je possède l’As de ladite couleur + au moins une autre carte,
                                        que le 10 n’est pas tombé, que les adversaires n’ont a priori plus d’atout
                                        et que les points que me rapporterait ce pli si je mettais l’As ne me font pas
                                        directement réussir mon contrat alors je joue la carte la plus forte non as de la couleur
                                        - Si je possède un maître, je le joue
                                        - Si mon partenaire est maître : si les adversaires n’ont a priori plus d’atouts,
                                         je joue la 2e carte la plus forte (si j’en ai au moins 2) sinon je joue la plus forte.

                                        - Sinon je joue la carte la moins forte possible.


                        2e cas : Je suis avant dernier à jouer
                            a) DSi l’adversaire suivant n’a a priori plus d’atout :
                                    - si j’ai un maître (absolu) je le joue
                                    - si le partenaire est maître absolu, je joue le + de points possible
                                    - si j’ai une carte au-dessus de la carte maîtresse actuellement je joue
                                      la plus basse répondant à ce critère sauf si c’est un 10 (je peux quand même jouer
                                      le 10 si le joueur suivant n’a a priori plus de la couleur)
                                    - Sinon je joue la plus basse

                            b) Sinon (= l’adversaire suivant a a priori encore de l’atout)
                                    -  Si j’ai un maître absolu je le joue sauf si le joueur suivant n’a pas de la couleur
                                    - Si le partenaire est maître absolu, je mets le plus de points sauf si le suivant n’a pas de la couleur
                                    - Si j’ai une carte au-dessus de la carte maîtresse actuellement je joue la plus basse
                                      répondant à ce critère sauf si c’est un 10 (je peux quand même jouer le 10 si le
                                      joueur suivant n’a a priori plus de la couleur)
                                    - Sinon je joue la plus basse
                        3e cas : Je suis 2e à jouer
                                - Si j’ai un maître absolu je le joue
                                - Si j’ai une carte au-dessus de la carte sur la table et que ce n’est pas un 10 je la joue
                                - Sinon je joue la carte la plus basse
                    C) Si je dois défausser
                        1er cas : Si le partenaire a déjà joué et est maître
                            - Si j’ai un As et un 10 dans une couleur et que le partenaire est maître absolu, je joue l’As
                            - Si j’ai un As non sec, je donne la + petite carte de la couleur
                            - Sinon si le partenaire est maître absolu, je donne le max de points
                            - Sinon je donne la plus petite carte possible sans rendre un 10 sec
                        2e cas : Mon partenaire n’est pas maître (ou n’a pas encore joué)
                            - Je donne la plus petite carte possible sans rendre un 10 sec

                    D) Si je dois couper
                       1er cas : Si le partenaire est maître absolu, ou maître et qu’il y a moins de 9 points
                         sur les cartes en jeu je défausse (go catégorie C)

                       Sinon on coupe :
                        - Si j’ai le 9 ou l’As d’atout second ou moins, que ce n’est pas l’atout le plus élevé restant,
                        et que l’annonce est de 100 ou moins, on joue l’atout le plus élevé qu’on possède.

                        - Sinon on joue l’atout le plus bas.




 */

fun nthToPlay(myPosition: PlayerPosition,
              cardsInHand: MutableList<Card>,
              bidPreneur: Bid,
              atout: CardColor,
              allCardPli: List<List<CardPlayed>>,
              missingPoints: Int,
              currBid: Bid,
              list3Dominantes: Map<CardColor, Card?>,
              listMyDominantes: List<Card>,
              maxAtout: Card?): Card? {

    val nameFunction = object {}.javaClass.enclosingMethod.name

    // Will be used to return the card to play
    var aCard: Card?

    val mapColorPlayerRemain =
            CardColor.values()
                    .map { e -> Pair(e, playersHaveColor(e, atout, bidPreneur, allCardPli, myPosition, cardsInHand)) }
                    .toMap()

    // allCardPli cannot be empty since we are not the first player to play
    val onTable = allCardPli[allCardPli.size - 1]
    val myPlayableCards = cardsInHand.filter { it.playable == true }
    val currentTableColor = onTable[0].card.color
    val myPlayableNoDefausse = myPlayableCards.filter { it.color == currentTableColor }
    val partnerIsActualWinner = calculateWinnerTrick(onTable, atout) == (myPosition + 2)

    if ((currentTableColor == atout) && myPlayableNoDefausse.isNotEmpty()) {
        /*
           Case A)
            first card is trump, and I have some
                if my partner did put the Jack, and I have Ace or 10 I play it
                if I have the nine and Jack already played, I play it
                else I play the lowest trump allowed ( I choose my cards in the playable cards anyway.

        */
        val jackPlayed = allCardPli.flatten()
                .firstOrNull { card -> card.card.color == atout && card.card.value == CardValue.JACK }
        val jackOwner = jackPlayed?.position
        val jackOnTable = onTable.any { card -> card.card.color == atout && card.card.value == CardValue.JACK }

        /*
        first card is trump, and I have some
                if my partner did put the Jack, and I have Ace or 10 I play it
         */
        if (jackOnTable && (jackOwner == (myPosition + 2))) {
            aCard = myPlayableCards.filter { it.value == CardValue.TEN || it.value == CardValue.ACE }
                    .sortedByDescending { e -> e.value.dominanceAtout }.firstOrNull()
            if (aCard != null) {
                debugPrintln(dbgLevel.DEBUG,
                             "$nameFunction:${getLineNumber()} we play $aCard my partner did play the Jack")

                return aCard
            }
        }

        /*
           if I have the nine and Jack already played, I play it
         */
        if ((jackPlayed != null) && (!jackOnTable) && (myPlayableCards.any { it.value == CardValue.NINE })) {
            aCard = myPlayableCards.firstOrNull { it.value == CardValue.NINE }
            debugPrintln(dbgLevel.DEBUG,
                         "$nameFunction:${getLineNumber()} we play $aCard Nine and Jack of trump already played ")

            return aCard
        }

        /*
             else I play the lowest trump allowed ( I choose my cards in the playable cards anyway.
         */

        if (onTable.size == 3) {
            // if we are last to play , lets save some points since it is the same to play Ace or 9 for example
            //TODO take into account cards already played in the for loop - perhaps too complex to program compared to value added ?
            val onTableHypothetic = onTable.toMutableList()
            val listCandidates = myPlayableCards.sortedBy { it.value.dominanceAtout }

            aCard = listCandidates.first()
            onTableHypothetic.add(CardPlayed(aCard, position = myPosition))
            val iWillWinTheTrick = calculateWinnerTrick(onTableHypothetic, atout) == myPosition
            if (iWillWinTheTrick) {
                for (myCard in listCandidates) {
                    if ((myCard.value.dominanceAtout - aCard!!.value.dominanceAtout) == 1) {
                        aCard = myCard
                    }
                }
            }
        } else {
            aCard = myPlayableCards.minBy { it.value.dominanceAtout }!!
        }
        debugPrintln(dbgLevel.DEBUG,
                     "$nameFunction:${getLineNumber()} we play $aCard which is probably the strongest card")

        return aCard
    }

    /*
            Case B)
                if the first Card is Color and I have this color in my cards color

     */
    if ((currentTableColor != atout) && myPlayableNoDefausse.isNotEmpty()) {
        val maxCardCurrColorOnTable =
                onTable.filter { it.card.color == currentTableColor }.maxBy { e -> e.card.value.dominanceCouleur }!!
        val weakestCardMyGame =
                myPlayableCards.filter { it.color == currentTableColor }.minBy { e -> e.value.dominanceCouleur }

        aCard = when (onTable.size) {
            3 -> {
                /*
                1) I am the last to play:
                         If own As + another card and 10 is not down , and the other team has not anymore trump
                           and with the points of the ACE I still dont win the contract yet : I play strongest card, but ACE

                         If I own a master card I play it.
                         If my partner is master and if enemy have no more trump : I play the 2nd card more strong or the strongest if only one
                         other case : I play the weakest card.

                 */
                // Last to play
                val iOwnAcePlus =
                        myPlayableCards.any { it.value == CardValue.ACE } and myPlayableCards.any { it.value != CardValue.ACE }
                val tenIsStillUp = allCardPli.flatten()
                                           .none { it.card.color == currentTableColor && it.card.value == CardValue.TEN } && myPlayableCards.none { it.value == CardValue.TEN }
                val othersCanStillCut =
                        (mapColorPlayerRemain[atout] ?: error(
                                "$nameFunction:${getLineNumber()} Impossible"))[myPosition + 1] ?: error(
                                "$nameFunction:${getLineNumber()} Impossible") || (mapColorPlayerRemain[atout] ?: error(
                                "$nameFunction:${getLineNumber()} Impossible"))[myPosition + 3]!!
                val otherTeamBeatUsTrump: Boolean =
                        onTable.any { it.card.color == atout && it.position != myPosition + 2 } && onTable.filter { it.card.color == atout }
                                .maxBy { e -> e.card.value.dominanceAtout }!!.position != (myPosition + 2)

                // in case of Capot we fall here only if it's first trick or we already lost the game - we are the 3rd to play
                // so it s ok not to play Ace
                when {
                    iOwnAcePlus and tenIsStillUp and !othersCanStillCut and !otherTeamBeatUsTrump and (missingPoints > CardValue.ACE.colorPoints) -> {
                        myPlayableCards.filter { it.value != CardValue.ACE }.maxBy { it.value.dominanceCouleur }
                    }
                    listMyDominantes.isNotEmpty() -> {
                        listMyDominantes.first()
                    }
                    (partnerIsActualWinner) and (!othersCanStillCut) -> {
                        var index = 0
                        if (myPlayableCards.size >= 2) {
                            index = 1
                        }
                        myPlayableCards.sortedByDescending { it.value.dominanceCouleur }[index]
                    }
                    else -> {
                        // check if we wil win the trick
                        // if yes, then for 2 consecutive cards we would choose to play, choose the bigger
                        val onTableHypothetic = onTable.toMutableList()
                        val listCandidates = myPlayableCards.sortedBy { it.value.dominanceCouleur }

                        var minMyCardsPlayable = listCandidates.first()
                        onTableHypothetic.add(CardPlayed(minMyCardsPlayable, position = myPosition))
                        val iWillWinTheTrick = calculateWinnerTrick(onTableHypothetic, atout) == myPosition
                        if (iWillWinTheTrick) {
                            for (myCard in listCandidates) {
                                if ((myCard.value.dominanceCouleur - minMyCardsPlayable.value.dominanceCouleur) == 1) {
                                    minMyCardsPlayable = myCard
                                }
                            }
                        }
                        minMyCardsPlayable

                    }

                }
            }
            2 -> {
                /*
                     2e cas : I am the third to play

                 */
                val nextPlayerHasStilTrump =
                        (mapColorPlayerRemain[atout] ?: error(
                                "I$nameFunction:${getLineNumber()} Impossible"))[(myPosition + 1)] ?: error(
                                "$nameFunction:${getLineNumber()} Impossible")

                //Next player has no trumps
                if (onTable[1].card.color == atout) {
                    //already cut / we dont cut , our partner already played, the trick is lost anyway
                    weakestCardMyGame
                } else {
                    if (!nextPlayerHasStilTrump) {
                        /*
                               a) If next player has not anymore trump
                                    - If I have an absolute  master card I play it
                                    - If my partner is absolute master I give more point possible
                                    - If I have a better card :
                                        I play the weakest matching this criteria but a Ten (I stil can play the Ten if next player has not the color
                                    - I play the weakest card
                         */
                        //Master Card ? I play it
                        if (listMyDominantes.any { it.color == currentTableColor }) {
                            listMyDominantes.first { it.color == currentTableColor }
                        } else {
                            if (list3Dominantes[currentTableColor]!!.isSimilar(onTable[0].card)) {
                                // My Partner is the master of this trick
                                myPlayableCards.maxBy { it.value.dominanceCouleur }!!
                            } else {
                                val playableBetterCards =
                                        myPlayableCards.filter { card -> (card.value.dominanceCouleur > maxCardCurrColorOnTable.card.value.dominanceCouleur) }
                                if (playableBetterCards.isNotEmpty()) {
                                    //next player has not trump and no color, we take the max whatever
                                    if (!mapColorPlayerRemain[currentTableColor]!![myPosition + 1]!!) {
                                        playableBetterCards.maxBy { it.value.dominanceCouleur }
                                    } else {
                                        if (playableBetterCards.filter { it.value != CardValue.TEN }.any()) {
                                            playableBetterCards.first { it.value != CardValue.TEN }
                                        } else {
                                            weakestCardMyGame
                                        }
                                    }
                                } else {
                                    weakestCardMyGame
                                }
                            }
                        }
                    } else {
                        // Next player has still trump
                        /*
                            b) Else (= next player has still some trumps)
                                - If have a master and next player still has color I play it.
                                - If my partner is the absolute master and next player has still color I play max point.
                                - If i have a card above current max card I play the lowest of these if not a TEN ,
                                - Else I play the weakest card
                        */
                        if ((listMyDominantes.any { it.color == currentTableColor }) && (mapColorPlayerRemain[currentTableColor]!![myPosition + 1]!!)) {
                            listMyDominantes.first { it.color == currentTableColor }
                        } else {
                            if ((list3Dominantes[currentTableColor]!!.isSimilar(
                                            onTable[0].card)) && (mapColorPlayerRemain[currentTableColor]!![myPosition + 1]!!)) {
                                // My Partner is the master of this trick, next player does not cut
                                myPlayableCards.maxBy { it.value.dominanceCouleur }!!
                            } else {
                                val playableBetterCardsNotTen =
                                        myPlayableCards.filter { card -> (card.value != CardValue.TEN) and (card.value.dominanceCouleur > maxCardCurrColorOnTable.card.value.dominanceCouleur) }
                                if (playableBetterCardsNotTen.isNotEmpty()) {
                                    playableBetterCardsNotTen.minBy { it.value.dominanceCouleur }
                                } else {
                                    weakestCardMyGame
                                }
                            }
                        }
                    }
                }

            }
            else -> {
                /*
                    3e cas : I am second to play
                                    - if I have a master card I play it.
                                    - If I have a better card than table and it's not a TEN
                                    - else I play the weakiest card

                 */
                if (listMyDominantes.any { it.color == currentTableColor }) {
                    listMyDominantes.first { it.color == currentTableColor }
                } else {
                    val playableBetterCardsNotTen =
                            myPlayableCards.filter { card -> (card.value != CardValue.TEN) and (card.value.dominanceCouleur > maxCardCurrColorOnTable.card.value.dominanceCouleur) }
                    if (playableBetterCardsNotTen.isNotEmpty()) {
                        playableBetterCardsNotTen.minBy { it.value.dominanceCouleur }
                    } else {
                        weakestCardMyGame
                    }
                }
            }
        }




        if (aCard != null) {
            debugPrintln(dbgLevel.DEBUG,
                         "$nameFunction:${getLineNumber()} we play $aCard  the first Card was  Color  I have this color in my cards color")

            return aCard
        }
    }
    // this is a map ( with all color defined) which gives the number of 10,Ace per color in my cards
    val aceAndTenInAColor = CardColor.values().filter { e ->
        (myPlayableCards.filter { card -> card.color == e && ((card.value == CardValue.ACE) || (card.value == CardValue.TEN)) }.size == 2)
    }

    val listColorWithTenAndNoAces = CardColor.values().filter { color ->
        myPlayableCards.none { e -> e.color == color && e.value == CardValue.ACE } && myPlayableCards.any { e -> e.color == color && e.value == CardValue.TEN }

    }
    val listColor2CardsInMyHandsTenNoAce =
            listColorWithTenAndNoAces.filter { color -> myPlayableCards.filter { it.color == color }.size == 2 }

    val listMyCardsColorWithAceNoTen = CardColor.values().filter { color ->
        myPlayableCards.none { e -> e.color == color && e.value == CardValue.TEN } && myPlayableCards.any { e -> e.color == color && e.value == CardValue.ACE }

    }
    val mapColorMin2CardsInMyHandsAmongAce = listMyCardsColorWithAceNoTen.map { color ->
        Pair(color, myPlayableCards.filter { it.color == color }.size)
    }.toMap()


    /*
    C) if first asked for something I dont have and I dont have any trump (defausse)
            1er cas :
                        Si le partenaire a déjà joué et est maître
                        Si j’ai un As et un 10 dans une couleur et que le partenaire est maître absolu, je joue l’As
                        Si j’ai un As non sec, je donne la + petite carte de la couleur
                        Sinon si le partenaire est maître absolu, je donne le max de points
                        Sinon je donne la plus petite carte possible sans rendre un 10 sec

            2e cas : Mon partenaire n’est pas maître (ou n’a pas encore joué)
                     Je donne la plus petite carte possible sans rendre un 10 sec
    */
    if (myPlayableCards.none { currentTableColor == it.color || atout == it.color }) {
        // I can play anything
        aCard = defausse(onTable,
                         partnerIsActualWinner,
                         atout,
                         maxAtout,
                         list3Dominantes,
                         currentTableColor,
                         aceAndTenInAColor,
                         myPlayableCards,
                         mapColorMin2CardsInMyHandsAmongAce,
                         missingPoints,
                         listColor2CardsInMyHandsTenNoAce)
        debugPrintln(dbgLevel.DEBUG, "$nameFunction:${getLineNumber()} we play $aCard  tThis is the result of defausse")
        return aCard
    }
/*

D) Si je dois couper
        1er cas :
                Si le partenaire est maître absolu, ou maître et qu’il y a moins de 9 points sur les cartes en jeu je défausse (go catégorie C)
        2eme Cas : On coupe :
                    Si j’ai le 9 ou l’As d’atout second ou moins, que ce n’est pas l’atout le plus élevé restant, et que l’annonce est de 100 ou moins,
                    on joue l’atout le plus élevé qu’on possède.
                   Sinon on joue l’atout le plus bas.
*/

    // The first card on table is no trump since it would have addressed by the code in the beginning of the function
    // so here we use trump to beat color we dont have
    if (myPlayableCards.none { it.color != atout }) {
        aCard = coupeAtout(myPlayableCards,
                           atout,
                           currentTableColor,
                           partnerIsActualWinner,
                           onTable,
                           list3Dominantes,
                           aceAndTenInAColor,
                           maxAtout,
                           mapColorMin2CardsInMyHandsAmongAce,
                           missingPoints,
                           listColor2CardsInMyHandsTenNoAce,
                           currBid)
        debugPrintln(dbgLevel.DEBUG,
                     "$nameFunction:${getLineNumber()} we play $aCard  it's probably a trump since we have to use trump with the exception of partner owning the trick")
        return aCard
    }

    return null
}


/*
D) I have to use trump
        1st case
                If partner is absolute Master , or "master  and there is less than 9 points on the trick"  I give points to the tricks - similar to defausse function
        2nd case : we use trump
                    If I have the 9 or the Ace of trump 2nd or less , and it is not the higher trump remaining and announce is more or equal to 100
                    we play trump the higher
                   else we play the lower trump possible.
 */

fun coupeAtout(myPlayableCards: List<Card>,
               atout: CardColor,
               currentTableColor: CardColor,
               partnerIsActualWinner: Boolean,
               onTable: List<CardPlayed>,
               list3Dominantes: Map<CardColor, Card?>,
               aceAndTenInAColor: List<CardColor>,
               maxAtout: Card?,
               mapColorMin2CardsInMyHandsAmongAce: Map<CardColor, Int>,
               missingPoints: Int,
               listColor2CardsInMyHandsTenNoAce: List<CardColor>,
               currBid: Bid): Card? {
    val aCard: Card?
    var partnerAbsolute = false
    var partnerCard: Card? = null
    if (onTable.size >= 2) {
        partnerCard = onTable[onTable.size - 2].card
    }
    //if partner played an atout .... there is a max value existing
    if (partnerCard != null) {
        if ((partnerCard.color == atout) && (partnerCard.value == maxAtout!!.value))
            partnerAbsolute = true


        //if partner played a color .... there is a max value for this color
        if ((partnerCard.color != atout) && list3Dominantes[currentTableColor]!!.isSimilar(partnerCard))
            partnerAbsolute = true
    }
    var pointTricks = 0
    for (card in onTable) {
        pointTricks += if (card.card.color == atout) {
            card.card.value.atoutPoints
        } else {
            card.card.value.dominanceCouleur
        }
    }
    aCard = if ((partnerAbsolute) || (partnerIsActualWinner && pointTricks <= 9)) {
        defausse(onTable,
                 partnerIsActualWinner,
                 atout,
                 maxAtout,
                 list3Dominantes,
                 currentTableColor,
                 aceAndTenInAColor,
                 myPlayableCards,
                 mapColorMin2CardsInMyHandsAmongAce,
                 missingPoints,
                 listColor2CardsInMyHandsTenNoAce)
    } else {
        val myAtouts = myPlayableCards.filter { it.color == atout }

        if ((myAtouts.size <= 2) && (myAtouts.any { it.value == CardValue.ACE || it.value == CardValue.NINE }) && (currBid.curPoint() >= 100)) {
            myAtouts.maxBy { it.value.dominanceAtout }
        } else {
            myAtouts.minBy { it.value.dominanceAtout }
        }

    }
    return aCard
}


fun defausse(onTable: List<CardPlayed>,
             partnerIsActualWinner: Boolean,
             atout: CardColor,
             maxAtout: Card?, list3Dominantes: Map<CardColor, Card?>,
             currentTableColor: CardColor,
             aceAndTenInAColor: List<CardColor>,
             myPlayableCards: List<Card>,
             mapColorMin2CardsInMyHandsAmongAce: Map<CardColor, Int>,
             missingPoints: Int,
             listColor2CardsInMyHandsTenNoAce: List<CardColor>): Card? {

    val nameFunction = object {}.javaClass.enclosingMethod.name

    val aCard: Card?

    if ((onTable.size > 1) && partnerIsActualWinner) {
        // my partner played and is apparently winning
        val partnerCard = onTable[onTable.size - 2].card
        var partnerAbsolute = false
        //if partner played an atout .... there is a max value existing
        if ((partnerCard.color == atout) && (partnerCard.value == maxAtout!!.value))
            partnerAbsolute = true
        //if partner played a color .... there is a max value for this color
        if ((partnerCard.color != atout) && (list3Dominantes[currentTableColor] ?: error(
                        "$nameFunction:${getLineNumber()} Impossible")).isSimilar(partnerCard))
            partnerAbsolute = true


        /*
        If Partner already played and is the  master
                    If I have a Ace and a Ten in a color and the partner is absolute I play Ace
                    If i have an ACE not alone I give the smallest card in the color
                    Else if partner is absolute I give a max of points
                    if not I give minimum of points, make sure a Ten does not become alone
         */

        if (partnerAbsolute) {
            // We have an Ace + ten, we give the Ace
            if (aceAndTenInAColor.isNotEmpty()) {
                val mapColorMyPlayableCardSize =
                        CardColor.values().map { Pair(it, myPlayableCards.filter { e -> e.color == it }.size) }.toMap()
                val selectedColor = aceAndTenInAColor.maxBy { e ->
                    mapColorMyPlayableCardSize[e] ?: error("$nameFunction:${getLineNumber()} Impossible")
                }
                aCard =
                        myPlayableCards.first { e ->
                            selectedColor == e.color && e.value == CardValue.ACE
                        }
                return aCard
            }
            // We have an Ace + small cards we give the smallest card
            for (entry in mapColorMin2CardsInMyHandsAmongAce) {
                if (entry.value >= 2) {
                    return myPlayableCards.filter { e -> e.color == entry.key }
                            .minBy { card -> card.value.dominanceCouleur }
                }
            }
            /*
                Else I give a max of points - even an Ace if I can make the game
             */

            return if (((missingPoints <= CardValue.ACE.colorPoints) && (missingPoints > 0)) || myPlayableCards.none { it.value != CardValue.ACE }) {
                aCard = myPlayableCards.maxBy { it.value.dominanceCouleur }!!
                aCard
            } else {

                aCard = myPlayableCards.filter { it.value != CardValue.ACE }
                        .maxBy { it.value.dominanceCouleur }!!
                aCard
            }
        } else {
            val listCandidate =
                    myPlayableCards.filter { e -> listColor2CardsInMyHandsTenNoAce.none { it == e.color } }
            aCard = if (listCandidate.isNotEmpty()) {

                listCandidate.minBy { f -> f.value.dominanceCouleur }
            } else {
                myPlayableCards.minBy { it.value.dominanceCouleur }

            }
            return aCard
        }

    } else {
        /*
        2e cas : Mon partenaire n’est pas maître (ou n’a pas encore joué)
                 Je donne la plus petite carte possible sans rendre un 10 sec
         */
        val listCandidate =
                myPlayableCards.filter { e -> listColor2CardsInMyHandsTenNoAce.none { it == e.color } }
        aCard = if (listCandidate.isNotEmpty()) {

            listCandidate.minBy { f -> f.value.dominanceCouleur }
        } else {
            myPlayableCards.minBy { it.value.dominanceCouleur }

        }
        return aCard
    }


}