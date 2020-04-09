package fr.hornik.coinche.rest

import fr.hornik.coinche.component.DataManagement
import fr.hornik.coinche.exception.GameFullException
import fr.hornik.coinche.exception.NotAuthenticatedException
import fr.hornik.coinche.model.Game
import fr.hornik.coinche.model.SetOfGames
import fr.hornik.coinche.model.User
import fr.hornik.coinche.model.values.PlayerPosition
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/lobby")
class LobbyController(@Autowired val dataManagement: DataManagement,
                      @Autowired val user: User) {

    /**
     * Create a game and return its id
     */
    @PostMapping("/createGame", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun createGame(): String {
        if (user.uid.isEmpty()) throw NotAuthenticatedException()
        val newGame = SetOfGames(user)
        dataManagement.createGame(newGame, user)
        return newGame.id
    }

    @GetMapping("/allGames", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun allGames(): List<Game> {
        if (user.uid.isEmpty()) throw NotAuthenticatedException()
        return dataManagement.allMyGames(user.uid)
    }

    @PostMapping("/joinGame", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun joinGame(@RequestParam(required = true, name = "gameId") gameId: String,
                 @RequestParam nickname: String?,
                 model: Model): Map<String, PlayerPosition> {
        if (user.uid.isBlank()) throw NotAuthenticatedException()
        val set = dataManagement.getGameOrThrow(gameId)
        if (set.isFull()) throw GameFullException(gameId)
        nickname?.let {
            user.nickname = it
        }
        val player = dataManagement.joinGame(set, user)
        return mapOf("position" to player.position)
    }

    @PostMapping("/setNickname")
    fun setNickname(@RequestParam(required = true) nickname: String,
                    model: Model) {
        val user = model.getAttribute(User.ATTRIBUTE_NAME) as User?
                ?: throw NotAuthenticatedException()
        user.nickname = nickname
        model.addAttribute(User.ATTRIBUTE_NAME, user)
    }
}