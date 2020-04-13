package fr.hornik.coinche.model

import fr.hornik.coinche.model.values.CardColor
import fr.hornik.coinche.model.values.CardValue

data class Card(val value: CardValue = CardValue.KING,
                val color: CardColor = CardColor.HEART,
                var playable: Boolean? = null) {
    fun isSimilar(Other:Card):Boolean {
        return ( (this.value == Other.value) && (this.color == Other.color))
    }
}