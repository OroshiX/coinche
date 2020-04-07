package fr.hornik.coinche.model

import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeName
import fr.hornik.coinche.model.values.CardColor
import fr.hornik.coinche.model.values.PlayerPosition

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type",
        include = JsonTypeInfo.As.PROPERTY
)
//@JsonSubTypes(JsonSubTypes.Type(SimpleBid::class, name = "SimpleBid"),
//              JsonSubTypes.Type(Capot::class, name = "Capot"),
//              JsonSubTypes.Type(General::class, name = "General"),
//              JsonSubTypes.Type(Pass::class, name = "Pass"))
sealed class Bid(val position: PlayerPosition = PlayerPosition.NORTH) {
    fun curColor(): CardColor {
        return when (this) {
            is Annonce -> this.color
            else       -> CardColor.HEART
            // TODO treat error case ( last else)
        }
    }

    fun positionAnnouncer(): PlayerPosition {
        return when (this) {
            is Coinche -> this.annonce.positionAnnouncer()
            else       -> this.position
        }
    }
}

@JsonTypeName("Annonce")
sealed class Annonce(position: PlayerPosition, val color: CardColor) :
        Bid(position)

@JsonTypeName("SimpleBid")
class SimpleBid(color: CardColor = CardColor.HEART, val points: Int = 0,
                position: PlayerPosition = PlayerPosition.NORTH) :
        Annonce(position, color) {
    override fun toString(): String {
        return "$points $color by $position"
    }
}

@JsonTypeName("General")
class General(color: CardColor = CardColor.HEART,
              position: PlayerPosition = PlayerPosition.NORTH,
              val belote: Boolean = false) : Annonce(position, color) {
    override fun toString(): String {
        return "General${if (belote) " belote" else ""} of $position at $color"
    }
}

@JsonTypeName("Capot")
class Capot(color: CardColor = CardColor.HEART,
            position: PlayerPosition = PlayerPosition.NORTH,
            val belote: Boolean = false) :
        Annonce(position, color) {
    override fun toString(): String {
        return "Capot${if (belote) " belote" else ""} of $position at $color"
    }
}

@JsonTypeName("Coinche")
class Coinche(val annonce: Annonce = SimpleBid(),
              position: PlayerPosition = PlayerPosition.NORTH,
              val surcoinche: Boolean = false) :
        Bid(position) {
    override fun toString(): String {
        return "$position ${if (surcoinche) "sur" else ""}coinche the bid $annonce"
    }
}

@JsonTypeName("Pass")
class Pass(position: PlayerPosition = PlayerPosition.NORTH) : Bid(position) {
    override fun toString(): String {
        return "PASS of $position"
    }
}
