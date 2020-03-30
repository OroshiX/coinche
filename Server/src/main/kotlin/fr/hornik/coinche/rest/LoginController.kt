package fr.hornik.coinche.rest

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class LoginController {
    @PostMapping("/login")
    fun login(username: String, password: String) {

    }
}