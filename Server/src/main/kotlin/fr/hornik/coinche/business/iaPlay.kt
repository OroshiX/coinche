package fr.hornik.coinche.business

import fr.hornik.coinche.model.Bid
import fr.hornik.coinche.model.Card
import fr.hornik.coinche.model.CardPlayed
import fr.hornik.coinche.model.values.CardColor
import fr.hornik.coinche.model.values.CardValue
import fr.hornik.coinche.model.values.PlayerPosition
import fr.hornik.coinche.util.dbgLevel
import fr.hornik.coinche.util.debugPrintln

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
    if (remainColorCards.isEmpty()) {
        return null
    } else {
        return Card(remainColorCards.first(), color)
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

fun allCards(): List<CardPlayed> {
    return listOf(CardPlayed())
}

/*

    Pour un une couleur donnée, renvoie pour l’ensemble des 3 autres joueurs s’il possèdent la couleur ou non. Possède des éléments heuristiques. Fonctionne différemment pour les couleurs ou pour l’atout.
Couleur :
R1 - Pour une couleur c, si un joueur X a joué d’une couleur différente de c alors que c’était la première carte jouée à un tour précédent, le joueur X ne possède pas la couleur c.
R2 - Si toutes les cartes (en comptant celles présentes dans mon jeu) de la couleur c sont tombées, le joueur X en particulier ne possède pas la couleur c.
*R3 - (règle heuristique) Si le joueur X a mis un 10 ou un Roi de la couleur c alors que c’était une carte adjacente à la carte maîtresse, et que cette carte maîtresse était à l’adversaire alors on considère que le joueur X ne possède plus de la couleur c Exemple : Au tour 2, Ouest s’est défaussé de son 10 de pique. Au tour 4, Nord met l’As de pique, puis Est met le roi de pique. Puisque, le Roi et l’As de pique sont adjacents (le 10 de pique avait été défaussé), et que Nord est maître, on considère que Est n’a dorénavant plus de pique. N.B. : On ne prend (arbitrairement) en compte cette règle que pour le 10 et le Roi, Dame et Valet ne valent pas assez de points pour que ce soit significatif. N.B. 2 : L’exemple ci-dessus fonctionnerait aussi si le 10 de pique n’était pas tombé mais qu’il se trouvait dans la main du joueur qui est en train de calculer.
*R4 - (règle heuristique), Si le joueur X a mis un 10 de la couleur c sans que l’As ne soit tombé au préalable dans le pli actuel ou les plis précédents, on considère que le joueur X ne possède plus de la couleur c.
Atout :
    *R1 - Si le joueur X n’a pas joué de l’atout alors que l’atout était demandé ou qu’il a joué d’une couleur différente de celle demandée alors que son partenaire n’était pas maître alors le joueur X n’a plus d’atout.
    *R2 - Si tous les atouts sont tombés (ceux que j’ai en main sont considérés tombés) alors le joueur X n’a plus d’atout.
    *R3 - Si le joueur X a joué un atout de valeur inférieure à l’atout noté a le plus fort du pli (a doit uniquement être parmi les cartes jouées avant le joueur X lors de ce pli), alors on sait qu’il n’a pas d’atout supérieur à a, on peut donc appliquer la règle R2 avec un plus petit nombre d’atouts. Exemple : C’est atout Coeur. Au tour 1 Nord a joué 10 de Coeur, Est a joué 8 de Coeur, Sud a joué Valet de Coeur, Ouest a joué 7 de Coeur. Plus tard au tour 3, la Dame de Coeur est jouée. De plus Nord (= le joueur qui calcule) possède dans son jeu le roi de Coeur. En parcourant les plis on voit qu’au tour 1, Est rentre dans les conditions de R3, on applique donc R2 avec les atouts inférieurs au 10 de Coeur. Or tous les coeurs inférieurs au 10 sont tombés (Roi dans la main, 8 et 7 au tour 1, Dame au tour 3), on en conclut que Est n’a plus d’atout.
    *R4 - (règle heuristique similaire à R3 couleur) Si le joueur X a mis un 9, un As ou un 10 d’atout alors que c’était une carte adjacente à la carte maîtresse et que cette carte maîtresse est à l’adversaire alors on considère que le joueur X n’a plus d’atout. Vraiment la même règle que R3 couleur, simplement elle s’applique pour 9, As et 10.
    *R5 - (règle heuristique légèrement similaire à R4 couleur) À n’appliquer que pour les adversaires, par pour le partenaire. Si le joueur X a mis un 9 d’atout, que la première carte du pli était de l’atout (ne pas faire si la première carte était de la couleur et que le joueur X devait couper) et que le valet d’atout n’est pas préalablement tombé, alors on considère que le joueur X ne possède plus que des atouts inférieurs au seuil auquel il était tenu. On applique alors R2 sur ces atouts inférieurs. Exemple : L’atout est Coeur. Au tour 1 Nord joue la Dame de Coeur, Est joue le 9 de Coeur, Sud joue le Valet de Coeur, Ouest joue le 7 de Coeur. Au tour 2 le 8 de Coeur est joué. En parcourant les plis on voit qu’au tour 1 Est rentre dans les conditions de R5, on applique donc R2 avec les atouts inférieurs à la Dame. Or tous les coeurs inférieurs à la Dame sont tombés (7 au tour 1 et 8 au tour 2), on en conclut que Est n’a plus d’atout.
    *R6 - (règle heuristique) Si l’annonce initiale du partenaire est supérieure ou égale à 100, qu’il ne reste plus qu’un seul atout dehors (en comptant comme tombés les atouts dans notre main) et que l’on a pas estimé avec les règles précédentes que le partenaire n’a plus d’atout (on estime qu’il lui reste de l’atout donc), alors on considère que c’est le partenaire qui possède ce dernier atout, on dit que les deux adversaires n’ont plus d’atouts. R6’ avec 2 atouts dehors et l’annonce initiale du partenaire >= 120.
    *R7 - (règle heuristique) Si l’annonce initiale du partenaire est supérieure ou égale à 110, que le partenaire (ou moi) a déjà lancé un tour d’atout (joué un atout en première carte d’un pli) et que 2 atouts ou plus ne provenant pas du partenaire sont tombés hors de ce tour d’atout (mes atouts en main font partie des cartes tombées), alors on considère que seul le partenaire possède les atouts restants, on estime que les deux adversaires n’ont plus d’atout. R7’ avec une annonce >= 120 et 1 atout non partenaire.


 */
fun playersHaveColor(color: CardColor, atout: CardColor, listPlis: List<List<CardPlayed>>, myPosition: PlayerPosition,
                     cardsInHand: List<Card>): Map<PlayerPosition, Boolean> {

    // By default all players have all colors
    val allPlayerColor = PlayerPosition.values().map { Pair(it, true) }.toMap().toMutableMap()
    val allCardsPlayed = listPlis.toList().flatten()

    //R1 - Pour une couleur c, si un joueur X a joué d’une couleur différente de c
    //     alors que c’était la première carte jouée à un tour précédent, le joueur X ne possède pas la couleur c.
    for (pli in listPlis) {
        if (pli.first().card.color == color) {
            for (position in pli.filter { it.card.color != color }.map { it.position })
                allPlayerColor[position] = false
        }
    }

    // R2 - Si toutes les cartes (en comptant celles présentes dans mon jeu)
    // de la couleur c sont tombées, le joueur X en particulier ne possède pas la couleur c.
    val nbCardPlayed = allCardsPlayed.filter { it.card.color == color }.size

    val nbMyCard = cardsInHand.filter { it.color == color }.size
    when {
        (nbCardPlayed == 8) -> PlayerPosition.values().forEach { allPlayerColor[it] = false }

        ((nbCardPlayed + nbMyCard) == 8) -> PlayerPosition.values().filter { it != myPosition }
                .forEach { e -> allPlayerColor[e] = false }
    }
    //R3 - (règle heuristique) Si le joueur X a mis un 10 ou un Roi de la couleur c alors que c’était une carte adjacente à la carte maîtresse,
    // et que cette carte maîtresse était à l’adversaire alors on considère que le joueur X ne possède plus de la couleur c
    // Exemple : Au tour 2, Ouest s’est défaussé de son 10 de pique. Au tour 4, Nord met l’As de pique, puis Est met le roi de pique.
    // Puisque, le Roi et l’As de pique sont adjacents (le 10 de pique avait été défaussé), et que Nord est maître,
    // on considère que Est n’a dorénavant plus de pique. N.B. : On ne prend (arbitrairement) en compte cette règle
    // que pour le 10 et le Roi, Dame et Valet ne valent pas assez de points pour que ce soit significatif.
    // N.B. 2 : L’exemple ci-dessus fonctionnerait aussi si le 10 de pique n’était pas tombé mais qu’il se trouvait dans la main du joueur qui est en train de calculer.
    // TODO

    // for my self I know my cards .... so no need to use any heuristic
    allPlayerColor[myPosition] = cardsInHand.any { it.color == color }
    return allPlayerColor
}

fun playRandom(cardsInHand: List<Card>): Card {
    val nameFunction = object {}.javaClass.enclosingMethod.name
    val aCard: Card = cardsInHand.filter { it.playable == true }.random()
    debugPrintln(dbgLevel.DEBUG,
                 "$nameFunction : we play $aCard  OOPS this is randomly played **************************************")

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
        PlayerPosition.NORTH -> calculateScoreGame(plisNS.toList().map { it.second }, plisEW.toList().map { it.second },
                                                   myPosition,
                                                   currBid).northSouth
        else -> calculateScoreGame(plisNS.toList().map { it.second }, plisEW.toList().map { it.second }, myPosition,
                                   currBid).eastWest
    }
    val missingPoints = currBid.curPoint() - currentScore
    // Decide what function to call

    // first : who took first this color and with how many points ?
    val pairPreneur = bids.filter { it.curColor() == atout }.map { Pair(it.position, it.curPoint()) }.first()
    val preneur = pairPreneur.first
    val pointsPreneur = pairPreneur.second

    when (preneur) {
        // My Partner is the first announcer for this color
        myPosition + 2 -> {
            if (onTable.size == 0) {
                //it's my turn to play
                debugPrintln(dbgLevel.DEBUG, "in $nameFunction calling first to play ")
                return firstToPlay(myPosition, cardsInHand, pointsPreneur, bids, atout, allCardPli,
                                   list3Dominantes, listMyDominantes)
            } else {
                debugPrintln(dbgLevel.DEBUG, "in $nameFunction calling nth to play ")
                return nthToPlay(myPosition, cardsInHand, atout, allCardPli,
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
            CardColor.values().map { e -> Pair(e, playersHaveColor(e, atout, allCardPli, myPosition, cardsInHand)) }
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
                    "Impossible Error in $nameFunction 2")) {
        // Do I have trumps ?
        if (pAtouts[myPosition] ?: error("Impossible Error in $nameFunction 3")) {
            // first announce was 80 ?
            if (pointsFirstAnnounce == 80) {
                // let's the smallest trump in my cards
                aCard = cardsInHand.filter { it.color == atout }.minBy { it.value.dominanceAtout }!!
                debugPrintln(dbgLevel.DEBUG, "$nameFunction : we play $aCard with criteria smallest trump we have")
                return aCard
            } else {
                //If the jack was already played
                if (allCardPli.flatten().any { (it.card.color == atout) && (it.card.value == CardValue.JACK) }) {
                    // we play the strongest card
                    aCard = cardsInHand.filter { it.color == atout }.maxBy { it.value.dominanceAtout }!!
                    debugPrintln(dbgLevel.DEBUG, "$nameFunction : we play $aCard with criteria strongest trump we have")
                    return aCard
                } else {
                    // we play astrongest atrump but the 9
                    aCard = cardsInHand.filter { (it.color == atout) && (it.value != CardValue.NINE) }
                            .maxBy { it.value.dominanceAtout }
                    if (aCard != null) {
                        debugPrintln(dbgLevel.DEBUG,
                                     "$nameFunction : we play $aCard with criteria strongest trump we have but the 9")
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

                val nbRemain = remainingColorMap[card.color] ?: error("$nameFunction : Impossible nbRemain is null")
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
                         "$nameFunction : we play $myTriple.first  with criteria first to play - no trump $myTriple")
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
                     playersHaveColor(color = card.color, atout = atout, listPlis = allCardPli,
                                      myPosition = myPosition, cardsInHand = cardsInHand)[myPosition + 1])
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
                             "$nameFunction : we play $aCard it is a color cards where probably next player will have to play trump")
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
                             "$nameFunction : we play $aCard , not a 10 alone, less cards / strongest ")

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
                             "$nameFunction : we play $aCard , not a 10 color with the most cards outside ")
                aCard
            } else {
                // just play randomly
                aCard = localCards.random()
                debugPrintln(dbgLevel.DEBUG,
                             "$nameFunction : we play $aCard , randomly ")
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
            debugPrintln(dbgLevel.DEBUG, "$nameFunction : we play $aCard which is probably the strongest card")

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
        debugPrintln(dbgLevel.DEBUG, "$nameFunction : we play $aCard there are no mastercards in our hand")

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
            CardColor.values().map { e -> Pair(e, playersHaveColor(e, atout, allCardPli, myPosition, cardsInHand)) }
                    .toMap()

    // allCardPli cannot be empty since we are not the first player to play
    val onTable = allCardPli[allCardPli.size - 1]
    val myPlayableCards = cardsInHand.filter { it.playable == true }
    val currentTableColor = onTable[0].card.color
    val myPlayableNoDefausse = myPlayableCards.filter { it.color == currentTableColor }
    val partnerIsActualWinner = calculateWinnerTrick(onTable, currBid) == (myPosition + 2)

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
                debugPrintln(dbgLevel.DEBUG, "$nameFunction : we play $aCard my partner did play the Jack")

                return aCard
            }
        }

        /*
           if I have the nine and Jack already played, I play it
         */
        if ((jackPlayed != null) && (myPlayableCards.firstOrNull { it.value == CardValue.NINE } != null)) {
            aCard = myPlayableCards.firstOrNull { it.value == CardValue.NINE }
            debugPrintln(dbgLevel.DEBUG, "$nameFunction : we play $aCard Nine and Jack of trump already played ")

            return aCard
        }

        /*
             else I play the lowest trump allowed ( I choose my cards in the playable cards anyway.
         */
        aCard = myPlayableCards.minBy { it.value.dominanceAtout }!!
        debugPrintln(dbgLevel.DEBUG, "$nameFunction : we play $aCard which is probably the strongest card")

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
                        mapColorPlayerRemain[atout]!![myPosition + 1]!! || mapColorPlayerRemain[atout]!![myPosition + 3]!!
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
                        myPlayableCards.minBy { it.value.dominanceCouleur }!!
                    }
                }
            }
            2 -> {
                /*
                     2e cas : I am the third to play

                 */
                val nextPlayerHasStilTrump = mapColorPlayerRemain[atout]!![(myPosition + 1)]!!

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
                         "$nameFunction : we play $aCard  the first Card was  Color  I have this color in my cards color")

            return aCard
        }
    }
    // this is a map ( with all color defined) which gives the number of 10,Ace per color in my cards
    val aceAndTenInAColor = CardColor.values().filter { e ->
        (myPlayableCards.filter { card -> card.color == e && ((card.value == CardValue.ACE) || (card.value == CardValue.TEN)) }.size == 2)
    }

    val listColorWithTenAndNoAces = CardColor.values().filter { color ->
        myPlayableCards.filter { e -> e.color == color && e.value != CardValue.ACE }
                .any { it.value == CardValue.TEN }
    }
    val listColor2CardsInMyHandsTenNoAce = listColorWithTenAndNoAces.map { color ->
        Pair(color, myPlayableCards.filter { it.color == color }.size)
    }.toMap().toList().filter { e -> e.second == 2 }.map { it.first }


    val listMyCardsColorWithAceNoTen = CardColor.values().filter { color ->
        myPlayableCards.filter { e -> e.color == color && e.value != CardValue.TEN }
                .any { it.value == CardValue.ACE }
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
        debugPrintln(dbgLevel.DEBUG, "$nameFunction : we play $aCard  tThis is the result of defausse")
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
                     "$nameFunction : we play $aCard  it's probably a trump since we have to use trump with the exception of partner owning the trick")
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

    val aCard: Card?

    if ((onTable.size > 1) && partnerIsActualWinner) {
        // my partner played and is apparently winning
        val partnerCard = onTable[onTable.size - 2].card
        var partnerAbsolute = false
        //if partner played an atout .... there is a max value existing
        if ((partnerCard.color == atout) && (partnerCard.value == maxAtout!!.value))
            partnerAbsolute = true
        //if partner played a color .... there is a max value for this color
        if ((partnerCard.color != atout) && list3Dominantes[currentTableColor]!!.isSimilar(partnerCard))
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
                aCard =
                        myPlayableCards.first { e ->
                            aceAndTenInAColor.contains(e.color) and (e.value == CardValue.ACE)
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