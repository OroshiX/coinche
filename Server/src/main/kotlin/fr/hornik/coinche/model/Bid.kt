package fr.hornik.coinche.model

import com.fasterxml.jackson.annotation.JsonGetter
import com.fasterxml.jackson.annotation.JsonSetter
import fr.hornik.coinche.model.values.CoincheValues

data class Bid(val color: String, val points: Int, var coinche: CoincheValues) {
    @JsonGetter("coinche")
    fun getTheCoinche(): String? = coinche.value

    @JsonSetter("coinche")
    fun setTheCoinche(theCoinche: String?) {
        coinche = theCoinche?.let { CoincheValues.valueOf(it) } ?: CoincheValues.NONE
    }
}
