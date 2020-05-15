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
    val colorRemaining = 8
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
    return cardsInHand.filter { it.playable == true }.random()
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
    val allCardsPlayedFlat =
            plisEW.toList().map { it.second }.flatten() + plisNS.toList().map { it.second }.flatten() + onTable
    val nbPlis = plisEW.size + plisNS.size
    var currentPli: Map<Int, MutableList<CardPlayed>> = mapOf()
    if (onTable.isNotEmpty()) {
        currentPli = listOf(Pair(nbPlis, onTable)).toMap()
    }
    val allCardPli = (plisEW + plisNS + currentPli).toSortedMap().toList().map { it.second }
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
                return firstToPlay(myPosition, cardsInHand, preneur, pointsPreneur, bids, atout, allCardPli)
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
                preneur: PlayerPosition,
                pointsFirstAnnounce: Int,

                bids: MutableList<Bid>,
                atout: CardColor,
                allCardPli: List<List<CardPlayed>>): Card? {
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
    val mapColorPlayerRemain =
            CardColor.values().map { e -> Pair(e, playersHaveColor(e, atout, allCardPli, myPosition, cardsInHand)) }
                    .toMap()

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
                val aCard = cardsInHand.filter { it.color == atout }.minBy { it.value.dominanceAtout }!!
                debugPrintln(dbgLevel.DEBUG, "$nameFunction : we play $aCard with criteria smallest trump we have")
                return aCard
            } else {
                //If the jack was already played
                if (allCardPli.flatten().any { (it.card.color == atout) && (it.card.value == CardValue.JACK) }) {
                    // we play the strongest card
                    val aCard = cardsInHand.filter { it.color == atout }.maxBy { it.value.dominanceAtout }!!
                    debugPrintln(dbgLevel.DEBUG, "$nameFunction : we play $aCard with criteria strongest trump we have")
                    return aCard
                } else {
                    // we play astrongest atrump but the 9
                    val aCard = cardsInHand.filter { (it.color == atout) && (it.value != CardValue.NINE) }
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
        val list3Dominantes = CardColor.values().filter { it != atout }
                .map { col -> Pair(col, masterColor(col, atout, allCardPli.flatten())) }.toMap()
        //This is the list of cards which make a tricks for sure and which are in my hand
        val listMyDominantes =
                cardsInHand.filter { (list3Dominantes[it.color] != null) && it.isSimilar(list3Dominantes[it.color]!!) }

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
            val aCard = listColorPlayable.map { localCards.filter { e -> it == e.color } }.flatten()
                    .minBy { it.value.dominanceCouleur }
            if (aCard != null) {
                debugPrintln(dbgLevel.DEBUG,
                             "$nameFunction : we play $aCard it is a color cards where probably next player will have to play trump")
                return aCard
            }
            //
            //  lets remove cards from color referenced in bids by the other team

            // first determine these color(s)
            val colorOfEnemy = CardColor.values()
                    .filter { color -> (bids.any { bid -> (color == bid.curColor()) && (bid.position == (myPosition + 1) || (bid.position == (myPosition + 3))) }) && (color != atout) }

            // check the cards we have in hand ( except 10 alone and trump) that are not in these colors
            var newCandidate = localCards.filter { e -> !colorOfEnemy.contains(e.color) }
            if (newCandidate.isNotEmpty()) {
                val tempList = newCandidate.sortedWith(
                        compareBy({ remainingColorMap[it.color] },
                                  { (list3Dominantes[it.color]!!.value.dominanceCouleur - it.value.dominanceCouleur) }))
                val aCard = tempList.first()
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
                val aCard: Card = newCandidate.first()
                debugPrintln(dbgLevel.DEBUG,
                             "$nameFunction : we play $aCard , not a 10 color with the most cards outside ")
                aCard
            } else {
                // just play randomly
                val aCard = localCards.random()
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

            Après ces étapes d’élimination, si il me reste 0 cartes, je reprends les étapes précédentes à l’envers une par une c’est à dire si en ne faisant que 1. et 2. J’ai encore des cartes je continue à l’étape en dessous, sinon je ne fais que 2. Etc…

            Il me reste donc maintenant des cartes (soit en ayant fait les 3 critères ou juste une partie de ces critères), Parmi les couleurs restante je choisis celle où je possède la carte la plus forte relativement aux cartes restantes (voir l’exemple 1 page avant)
        }
        */
        //let's see the list of card who make the tricks in the differents colors


        val list3Dominantes = CardColor.values().filter { it != atout }
                .map { col -> Pair(col, masterColor(col, atout, allCardPli.flatten())) }.toMap()
        // check if I own one of those cards
        val listMyDominantes =
                cardsInHand.filter { (list3Dominantes[it.color] != null) && it.isSimilar(list3Dominantes[it.color]!!) }

        // it exists a color non atout where I have a "master" card
        // sort it by color where my partner has no more cards, the card with the more points , the color where there is the less cards outside
        val l1 = listMyDominantes.sortedWith(
                compareBy({ (!mapColorPlayerRemain[it.color]!![myPosition + 2]!!) }, { -it.value.dominanceCouleur },
                          { remainingColorMap[it.color] }))
        val aCard =
                l1.firstOrNull()
        if (aCard != null) {
            return aCard
        }
    }
    return null

}

