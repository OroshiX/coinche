package fr.hornik.coinche.model

import org.springframework.context.annotation.ScopedProxyMode
import org.springframework.stereotype.Component
import org.springframework.web.context.annotation.SessionScope

@Component
@SessionScope(proxyMode = ScopedProxyMode.TARGET_CLASS)
open class User(var uid: String = "", var nickname: String = "") {
    companion object {
        const val ATTRIBUTE_NAME = "user"
    }

    override fun toString(): String {
        return "uid: $uid, nickname: $nickname"
    }
}