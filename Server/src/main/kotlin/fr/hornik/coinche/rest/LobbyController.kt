package fr.hornik.coinche.rest

import fr.hornik.coinche.business.inGame
import fr.hornik.coinche.component.DataManagement
import fr.hornik.coinche.component.FireApp
import fr.hornik.coinche.dto.Game
import fr.hornik.coinche.exception.AlreadyJoinedException
import fr.hornik.coinche.exception.GameFullException
import fr.hornik.coinche.exception.NotAuthenticatedException
import fr.hornik.coinche.exception.NotAuthorizedOperation
import fr.hornik.coinche.model.LoginStatus
import fr.hornik.coinche.model.SetOfGames
import fr.hornik.coinche.model.User
import fr.hornik.coinche.model.values.PlayerPosition
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/lobby")
class LobbyController(@Autowired val dataManagement: DataManagement,
                      @Autowired val user: User, @Autowired val fire: FireApp) {

    /**
     * Create a game and return its id
     */
    @PostMapping("/createGame", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun createGame(@RequestBody name: String): Game {
        if (user.uid.isBlank()) throw NotAuthenticatedException()
        val newGame = SetOfGames(user, name)
        dataManagement.createGame(newGame, user)
        return Game(newGame, true);
    }

    @GetMapping("/allGames", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun allGames(): List<Game> {
        if (user.uid.isBlank()) throw NotAuthenticatedException()
        return dataManagement.allMyGames(user.uid)
    }

    @PostMapping("/joinGame", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun joinGame(@RequestParam(required = true, name = "gameId") gameId: String,
                 @RequestParam nickname: String?): Map<String, PlayerPosition> {
        if (user.uid.isBlank()) throw NotAuthenticatedException()
        val set = dataManagement.getGameOrThrow(gameId)
        if (inGame(set, user)) throw AlreadyJoinedException(gameId)
        if (set.isFull()) throw GameFullException(gameId)
        val player = dataManagement.joinGame(set, user, nickname)
        return mapOf("position" to player.position)
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/setNickname")
    fun setNickname(@RequestBody(required = true) nickname: String) {
        if (user.uid.isBlank()) throw NotAuthenticatedException()
        if (nickname.isBlank()) throw  NotAuthorizedOperation("Nickname cannot be empty")

        user.nickname = nickname


        fire.setNewUsername(user)
        // Only for the games to come
    }

    @GetMapping("/isLoggedIn",  produces = [MediaType.APPLICATION_JSON_VALUE])
    fun isLoggedIn():LoginStatus {

        return LoginStatus(isLoggedIn = user.uid.isNotBlank(), nickName = user.nickname)

    }
}