package fr.hornik.coinche.model

import fr.hornik.coinche.model.values.CardColor
import fr.hornik.coinche.model.values.PlayerPosition

sealed class Bid

sealed class Annonce : Bid()
data class SimpleBid(val color: CardColor, val points: Int, val position: PlayerPosition) :
        Annonce() {
    override fun toString(): String {
        return "$points $color by $position"
    }
}

data class General(val color: CardColor, val position: PlayerPosition,
                   val belote: Boolean) : Annonce() {
    override fun toString(): String {
        return "General${if (belote) " belote" else ""} of $position at $color"
    }
}

data class Capot(val color: CardColor, val position: PlayerPosition, val belote: Boolean) :
        Annonce() {
    override fun toString(): String {
        return "Capot${if(belote) " belote" else ""} of $position at $color"
    }
}

data class Coinche(val annonce: Annonce, val surcoinche: Boolean, val position: PlayerPosition) : Bid() {
    override fun toString(): String {
        return "$position ${if (surcoinche) "sur" else ""}coinche the bid $annonce"
    }
}

object Pass : Bid() {
    override fun toString(): String {
        return "PASS"
    }
}