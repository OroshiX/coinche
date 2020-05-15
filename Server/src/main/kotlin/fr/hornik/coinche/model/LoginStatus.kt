package fr.hornik.coinche.model

data class LoginStatus(

        val isLoggedIn: Boolean = false,
        val nickName: String? = null,
        val uid: String? = null
)