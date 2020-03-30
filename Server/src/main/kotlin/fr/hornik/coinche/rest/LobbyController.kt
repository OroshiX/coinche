package fr.hornik.coinche.rest

import fr.hornik.coinche.model.Game
import fr.hornik.coinche.model.values.PlayerPosition
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController("/lobby")
class LobbyController {
    @PostMapping("/createGame", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun createGame(): String {
        // TODO to implement
        return "toto"
    }

    @GetMapping("/allGames", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun allGames(): List<Game> {

        // TODO to implement
        return listOf(Game("toto", 3, "Sacha"),
                      Game("ashtmc3", 1, "Jessica")
        )
    }

    @PostMapping("/joinGame", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun joinGame(@RequestParam(required = true) gameId: String,
                 @RequestParam(required = true) nickname: String): Pair<String, PlayerPosition> {
        // TODO change this
        return "position" to PlayerPosition.WEST
    }

    @PostMapping("/setNickname")
    fun setNickname(@RequestParam(required = true) nickname: String) {
        // TODO implement this
    }
}