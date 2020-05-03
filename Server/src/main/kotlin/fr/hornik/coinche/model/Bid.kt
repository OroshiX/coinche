package fr.hornik.coinche.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeName
import fr.hornik.coinche.model.values.CardColor
import fr.hornik.coinche.model.values.PlayerPosition
import fr.hornik.coinche.serialization.JsonSerialize

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type",
        include = JsonTypeInfo.As.PROPERTY
)
@JsonSubTypes(
        JsonSubTypes.Type(SimpleBid::class, name = "SimpleBid"),
        JsonSubTypes.Type(Capot::class, name = "Capot"),
        JsonSubTypes.Type(General::class, name = "General"),
        JsonSubTypes.Type(Pass::class, name = "Pass"),
        JsonSubTypes.Type(Coinche::class, name = "Coinche")
)
sealed class Bid(val position: PlayerPosition = PlayerPosition.NORTH) {
    fun curColor(): CardColor {
        return when (this) {
            is SimpleBid -> this.color
            is Capot     -> this.color
            is General   -> this.color
            is Coinche -> this.annonce.curColor()
            else         -> CardColor.HEART
            // TODO treat error case ( last else)
        }
    }
    fun curPoint(): Int {
        return when (this) {
            is SimpleBid -> this.points
            is Capot     -> if (belote) 270 else 250 //TODO Use definition of CAPOT to select value
            is General   -> if (belote) 520 else 500 //TODO User definition of GENERAL to select value
            is Coinche -> {
                if (this.surcoinche) this.annonce.curPoint()*4 else { this.annonce.curPoint()*4}
            }

            else         -> 0
            // TODO treat error case ( last else)
        }
    }

    fun positionAnnouncer(): PlayerPosition {
        return when (this) {
            is Coinche -> this.annonce.positionAnnouncer()
            else       -> this.position
        }
    }

    companion object {
        @JsonCreator
        @JvmStatic
        private fun creator(name: String): Bid? {
            return Bid::class.sealedSubclasses.firstOrNull { it.simpleName == name }?.objectInstance
        }
    }

    fun toFirebase(): String = JsonSerialize.toJson(this)
}

class SimpleBid(val color: CardColor = CardColor.HEART, val points: Int = 0,
                position: PlayerPosition = PlayerPosition.NORTH) :
        Bid(position) {
    override fun toString(): String {
        return "$points $color by $position"
    }
}

class General(val color: CardColor = CardColor.HEART,
              position: PlayerPosition = PlayerPosition.NORTH,
              val belote: Boolean = false) : Bid(position) {
    override fun toString(): String {
        return "General${if (belote) " belote" else ""} of $position at $color"
    }
}

class Capot(val color: CardColor = CardColor.HEART,
            position: PlayerPosition = PlayerPosition.NORTH,
            val belote: Boolean = false) :
        Bid(position) {
    override fun toString(): String {
        return "Capot${if (belote) " belote" else ""} of $position at $color"
    }
}

@JsonTypeName("Coinche")
class Coinche(val annonce: Bid = SimpleBid(),
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