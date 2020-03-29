package fr.hornik.coinche.rest

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class GameController {
    @RequestMapping("/home")
    fun home(): String {
        return "TOTO"
    }
}