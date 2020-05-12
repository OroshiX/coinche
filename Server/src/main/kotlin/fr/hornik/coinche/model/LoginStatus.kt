package fr.hornik.coinche.model

data class LoginStatus(

        val isLoggedIn: Boolean = false,
        val nickName: String? = null,
        val listActiveGame: List<SetOfGames>? = null
)