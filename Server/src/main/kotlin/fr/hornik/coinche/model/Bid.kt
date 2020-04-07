package fr.hornik.coinche.model

import fr.hornik.coinche.model.values.CardColor
import fr.hornik.coinche.model.values.PlayerPosition
import kotlinx.serialization.Serializable

@Serializable
sealed class Bid {
    val toto: String = "hut"
}
//    companion object {
//        @JsonCreator
//        @JvmStatic
//        fun findBySimpleClassName(simpleName: String): Bid? {
//            return Bid::class.sealedSubclasses.firstOrNull {
//                it.simpleName == simpleName
//            }?.objectInstance
//        }
//    }

@Serializable
sealed class Annonce : Bid()

@Serializable
class SimpleBid(val color: CardColor = CardColor.HEART, val points: Int = 0,
                val position: PlayerPosition = PlayerPosition.NORTH) :
        Annonce() {
    override fun toString(): String {
        return "$points $color by $position"
    }
}

@Serializable
class General(val color: CardColor = CardColor.HEART, val position: PlayerPosition = PlayerPosition.NORTH,
              val belote: Boolean = false) : Annonce() {
    override fun toString(): String {
        return "General${if (belote) " belote" else ""} of $position at $color"
    }
}

@Serializable
class Capot(val color: CardColor = CardColor.HEART, val position: PlayerPosition = PlayerPosition.NORTH,
            val belote: Boolean = false) :
        Annonce() {
    override fun toString(): String {
        return "Capot${if (belote) " belote" else ""} of $position at $color"
    }
}


@Serializable
class Coinche(val annonce: Annonce = SimpleBid(), val position: PlayerPosition = PlayerPosition.NORTH,
              val surcoinche: Boolean = false) :
        Bid() {
    override fun toString(): String {
        return "$position ${if (surcoinche) "sur" else ""}coinche the bid $annonce"
    }
}

@Serializable
class Pass(val position: PlayerPosition = PlayerPosition.NORTH) : Bid() {
    override fun toString(): String {
        return "PASS of $position"
    }
}
