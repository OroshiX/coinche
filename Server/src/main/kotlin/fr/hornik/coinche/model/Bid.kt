package fr.hornik.coinche.model

import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeName
import fr.hornik.coinche.model.values.CardColor
import fr.hornik.coinche.model.values.PlayerPosition

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
//@JsonSubTypes(JsonSubTypes.Type(SimpleBid::class, name = "SimpleBid"),
//              JsonSubTypes.Type(Capot::class, name = "Capot"),
//              JsonSubTypes.Type(General::class, name = "General"),
//              JsonSubTypes.Type(Pass::class, name = "Pass"))
sealed class Bid

@JsonTypeName("Annonce")
sealed class Annonce : Bid()

@JsonTypeName("SimpleBid")
class SimpleBid(val color: CardColor = CardColor.HEART, val points: Int = 0,
                val position: PlayerPosition = PlayerPosition.NORTH) :
        Annonce() {
    override fun toString(): String {
        return "$points $color by $position"
    }
}

@JsonTypeName("General")
class General(val color: CardColor = CardColor.HEART, val position: PlayerPosition = PlayerPosition.NORTH,
              val belote: Boolean = false) : Annonce() {
    override fun toString(): String {
        return "General${if (belote) " belote" else ""} of $position at $color"
    }
}

@JsonTypeName("Capot")
class Capot(val color: CardColor = CardColor.HEART, val position: PlayerPosition = PlayerPosition.NORTH,
            val belote: Boolean = false) :
        Annonce() {
    override fun toString(): String {
        return "Capot${if (belote) " belote" else ""} of $position at $color"
    }
}


@JsonTypeName("Coinche")
class Coinche(val annonce: Annonce = SimpleBid(), val position: PlayerPosition = PlayerPosition.NORTH,
              val surcoinche: Boolean = false) :
        Bid() {
    override fun toString(): String {
        return "$position ${if (surcoinche) "sur" else ""}coinche the bid $annonce"
    }
}

@JsonTypeName("Pass")
class Pass(val position: PlayerPosition = PlayerPosition.NORTH) : Bid() {
    override fun toString(): String {
        return "PASS of $position"
    }
}
