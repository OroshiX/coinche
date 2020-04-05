package fr.hornik.coinche.model

import fr.hornik.coinche.model.values.CardColor
import fr.hornik.coinche.model.values.PlayerPosition

sealed class Bid(val position: PlayerPosition)

sealed class Annonce(val color: CardColor, position: PlayerPosition) : Bid(position)

class SimpleBid(color: CardColor, val points: Int, position: PlayerPosition) :
        Annonce(color, position) {
    override fun toString(): String {
        return "$points $color by $position"
    }
}

class General(color: CardColor, position: PlayerPosition,
              val belote: Boolean = false) : Annonce(color, position) {
    override fun toString(): String {
        return "General${if (belote) " belote" else ""} of $position at $color"
    }
}

class Capot(color: CardColor, position: PlayerPosition, val belote: Boolean = false) :
        Annonce(color, position) {
    override fun toString(): String {
        return "Capot${if (belote) " belote" else ""} of $position at $color"
    }
}

class Coinche(val annonce: Annonce, position: PlayerPosition, val surcoinche: Boolean = false) : Bid(position) {
    override fun toString(): String {
        return "$position ${if (surcoinche) "sur" else ""}coinche the bid $annonce"
    }
}

class Pass(position: PlayerPosition) : Bid(position) {
    override fun toString(): String {
        return "PASS of $position"
    }
}