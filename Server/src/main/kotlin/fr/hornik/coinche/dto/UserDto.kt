package fr.hornik.coinche.dto

import fr.hornik.coinche.model.User

data class UserDto(val uid: String = "", val nickname: String = "") {
    constructor(user: User): this(user.uid, user.nickname)
}