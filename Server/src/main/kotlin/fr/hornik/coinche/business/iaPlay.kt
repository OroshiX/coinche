package fr.hornik.coinche.business

import fr.hornik.coinche.model.Bid
import fr.hornik.coinche.model.Card
import fr.hornik.coinche.model.CardPlayed
import fr.hornik.coinche.model.values.CardColor
import fr.hornik.coinche.model.values.CardValue
import fr.hornik.coinche.model.values.PlayerPosition

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
    if (remainColorCards.isEmpty() ){
        return null
    } else {
        return Card(remainColorCards.first(), color)
    }
}


fun totalRemainingNumber(color: CardColor,allCardsPlayed: List<CardPlayed>,myCards:List<Card>): Int {
    /*
        Pour une couleur donnée, selon les cartes qui sont tombées dans les plis précédents,
        renvoie le nombre de cartes restantes (inclut nos cartes) de la couleur
     */
    val colorCardsPlayed = allCardsPlayed.filter { it.card.color == color }.size
    val colorCardsHand = myCards.filter {it.color == color}.size

    return 8 - colorCardsHand - colorCardsPlayed
}


fun allCards():List<CardPlayed> {
    return listOf(CardPlayed())
}

/*

    Pour un une couleur donnée, renvoie pour l’ensemble des 3 autres joueurs s’il possèdent la couleur ou non. Possède des éléments heuristiques. Fonctionne différemment pour les couleurs ou pour l’atout.
Couleur :
R1 - Pour une couleur c, si un joueur X a joué d’une couleur différente de c alors que c’était la première carte jouée à un tour précédent, le joueur X ne possède pas la couleur c.
R2 - Si toutes les cartes (en comptant celles présentes dans mon jeu) de la couleur c sont tombées, le joueur X en particulier ne possède pas la couleur c.
R3 - (règle heuristique) Si le joueur X a mis un 10 ou un Roi de la couleur c alors que c’était une carte adjacente à la carte maîtresse, et que cette carte maîtresse était à l’adversaire alors on considère que le joueur X ne possède plus de la couleur c Exemple : Au tour 2, Ouest s’est défaussé de son 10 de pique. Au tour 4, Nord met l’As de pique, puis Est met le roi de pique. Puisque, le Roi et l’As de pique sont adjacents (le 10 de pique avait été défaussé), et que Nord est maître, on considère que Est n’a dorénavant plus de pique. N.B. : On ne prend (arbitrairement) en compte cette règle que pour le 10 et le Roi, Dame et Valet ne valent pas assez de points pour que ce soit significatif. N.B. 2 : L’exemple ci-dessus fonctionnerait aussi si le 10 de pique n’était pas tombé mais qu’il se trouvait dans la main du joueur qui est en train de calculer.
R4 - (règle heuristique), Si le joueur X a mis un 10 de la couleur c sans que l’As ne soit tombé au préalable dans le pli actuel ou les plis précédents, on considère que le joueur X ne possède plus de la couleur c.
Atout :
    R1 - Si le joueur X n’a pas joué de l’atout alors que l’atout était demandé ou qu’il a joué d’une couleur différente de celle demandée alors que son partenaire n’était pas maître alors le joueur X n’a plus d’atout.
    R2 - Si tous les atouts sont tombés (ceux que j’ai en main sont considérés tombés) alors le joueur X n’a plus d’atout.
    R3 - Si le joueur X a joué un atout de valeur inférieure à l’atout noté a le plus fort du pli (a doit uniquement être parmi les cartes jouées avant le joueur X lors de ce pli), alors on sait qu’il n’a pas d’atout supérieur à a, on peut donc appliquer la règle R2 avec un plus petit nombre d’atouts. Exemple : C’est atout Coeur. Au tour 1 Nord a joué 10 de Coeur, Est a joué 8 de Coeur, Sud a joué Valet de Coeur, Ouest a joué 7 de Coeur. Plus tard au tour 3, la Dame de Coeur est jouée. De plus Nord (= le joueur qui calcule) possède dans son jeu le roi de Coeur. En parcourant les plis on voit qu’au tour 1, Est rentre dans les conditions de R3, on applique donc R2 avec les atouts inférieurs au 10 de Coeur. Or tous les coeurs inférieurs au 10 sont tombés (Roi dans la main, 8 et 7 au tour 1, Dame au tour 3), on en conclut que Est n’a plus d’atout.
    R4 - (règle heuristique similaire à R3 couleur) Si le joueur X a mis un 9, un As ou un 10 d’atout alors que c’était une carte adjacente à la carte maîtresse et que cette carte maîtresse est à l’adversaire alors on considère que le joueur X n’a plus d’atout. Vraiment la même règle que R3 couleur, simplement elle s’applique pour 9, As et 10.
    R5 - (règle heuristique légèrement similaire à R4 couleur) À n’appliquer que pour les adversaires, par pour le partenaire. Si le joueur X a mis un 9 d’atout, que la première carte du pli était de l’atout (ne pas faire si la première carte était de la couleur et que le joueur X devait couper) et que le valet d’atout n’est pas préalablement tombé, alors on considère que le joueur X ne possède plus que des atouts inférieurs au seuil auquel il était tenu. On applique alors R2 sur ces atouts inférieurs. Exemple : L’atout est Coeur. Au tour 1 Nord joue la Dame de Coeur, Est joue le 9 de Coeur, Sud joue le Valet de Coeur, Ouest joue le 7 de Coeur. Au tour 2 le 8 de Coeur est joué. En parcourant les plis on voit qu’au tour 1 Est rentre dans les conditions de R5, on applique donc R2 avec les atouts inférieurs à la Dame. Or tous les coeurs inférieurs à la Dame sont tombés (7 au tour 1 et 8 au tour 2), on en conclut que Est n’a plus d’atout.
    R6 - (règle heuristique) Si l’annonce initiale du partenaire est supérieure ou égale à 100, qu’il ne reste plus qu’un seul atout dehors (en comptant comme tombés les atouts dans notre main) et que l’on a pas estimé avec les règles précédentes que le partenaire n’a plus d’atout (on estime qu’il lui reste de l’atout donc), alors on considère que c’est le partenaire qui possède ce dernier atout, on dit que les deux adversaires n’ont plus d’atouts. R6’ avec 2 atouts dehors et l’annonce initiale du partenaire >= 120.
    R7 - (règle heuristique) Si l’annonce initiale du partenaire est supérieure ou égale à 110, que le partenaire (ou moi) a déjà lancé un tour d’atout (joué un atout en première carte d’un pli) et que 2 atouts ou plus ne provenant pas du partenaire sont tombés hors de ce tour d’atout (mes atouts en main font partie des cartes tombées), alors on considère que seul le partenaire possède les atouts restants, on estime que les deux adversaires n’ont plus d’atout. R7’ avec une annonce >= 120 et 1 atout non partenaire.


 */
fun playersHaveColor(color:CardColor, atout:CardColor,listPlis:List<List<CardPlayed>>) : Map<PlayerPosition,Boolean> {


    return PlayerPosition.values().map{Pair(it,true)}.toMap()
}


fun whatToPlay(myPosition:PlayerPosition,
               cardsInHand: MutableList<Card>,
               bids:MutableList<Bid>,
               atout:CardColor,
               onTable:MutableList<CardPlayed>,
               plisNS:MutableMap<Int,List<CardPlayed>>,
               plisEW:MutableMap<Int,List<CardPlayed>>) : Card {

    // Create the list of all cards played ( on table + pliNS + pliEW
    // They can be filtered by player position etc ....
    val allCardsPlayedFlat = plisEW.toList().map{it.second}.flatten() + plisNS.toList().map{it.second}.flatten() + onTable
    val allCardPli = plisEW.toList() .map {it.second} + plisNS.toList() .map {it.second} + listOf(onTable)
    return Card()
}
