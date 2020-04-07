package fr.hornik.coinche.rest

import fr.hornik.coinche.DataManagement
import fr.hornik.coinche.exception.GameNotExistingException
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
    fun createGame(model: Model): String {
        if (user.uid.isEmpty()) throw NotAuthenticatedException()
        val newGame = dataManagement.createGame(SetOfGames(user))
        return newGame.id
    }

    @GetMapping("/allGames", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun allGames(): List<Game> {
        if (user.uid.isEmpty()) throw NotAuthenticatedException()
        return dataManagement.allMyGames(user.uid)
                .map { setOfGames ->
                    Game(setOfGames.id,
                         setOfGames.players.size,
                         setOfGames.players.first().nickname)
                }
    }

    @PostMapping("/joinGame", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun joinGame(@RequestParam(required = true, name = "gameId") gameId: String,
                 @RequestParam nickname: String?, model: Model): Pair<String, PlayerPosition> {
        if (user.uid.isBlank()) throw NotAuthenticatedException()
        val set = dataManagement.getGame(gameId)
                ?: throw GameNotExistingException(gameId)
        nickname?.let {
            user.nickname = it
        }
        val player = dataManagement.joinGame(set, user)
        return "position" to player.position
    }

    @PostMapping("/setNickname")
    fun setNickname(@RequestParam(required = true) nickname: String, model: Model) {
        val user = model.getAttribute(User.ATTRIBUTE_NAME) as User?
                ?: throw NotAuthenticatedException()
        user.nickname = nickname
        model.addAttribute(User.ATTRIBUTE_NAME, user)
    }
}