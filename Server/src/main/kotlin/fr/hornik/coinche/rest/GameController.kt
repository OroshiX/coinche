package fr.hornik.coinche.rest

import fr.hornik.coinche.component.DataManagement
import fr.hornik.coinche.component.FireApp
import fr.hornik.coinche.dto.Table
import fr.hornik.coinche.exception.*
import fr.hornik.coinche.model.*
import fr.hornik.coinche.model.values.CardColor
import fr.hornik.coinche.model.values.PlayerPosition
import fr.hornik.coinche.model.values.TableState
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/games")
class GameController(@Autowired val data: DataManagement,
                     @Autowired val user: User,
                     @Autowired private val fire: FireApp) {
    @GetMapping("/home", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun home(): List<Bid> {
        return listOf(
                SimpleBid(CardColor.HEART, 80, PlayerPosition.NORTH),
                Pass(PlayerPosition.EAST),
                SimpleBid(CardColor.SPADE, 100, PlayerPosition.SOUTH),
                Coinche(SimpleBid(CardColor.SPADE, 100, PlayerPosition.SOUTH),
                        PlayerPosition.WEST),
                Coinche(SimpleBid(CardColor.SPADE, 100, PlayerPosition.SOUTH),
                        PlayerPosition.NORTH, surcoinche = true)
        )
    }

    @GetMapping("/triggerRefresh")
    fun triggerRefresh() {
        data.refresh()
    }

    @GetMapping("/{gameId}/getTable",
                produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getTable(@PathVariable("gameId") gameId: String): Table {
        val set = data.getGameOrThrow(gameId)
        if (!inGame(set)) throw NotInGameException(gameId)
        return set.toTable(user.uid)
    }

    @PostMapping("/{gameId}/playCard")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun playCard(@PathVariable gameId: String, @RequestBody card: Card) {
        val game = data.getGameOrThrow(gameId)
        if (!inGame(game)) throw NotInGameException(gameId)
        if (game.state != TableState.PLAYING) throw NotValidStateException(
                game.state, TableState.PLAYING)
        if (game.players.first { it.uid == user.uid }.position != game.whoseTurn) throw NotYourTurnException()
        data.playCard(game, card, user)
        fire.saveGame(game)
    }

    @PostMapping("/{gameId}/announceBid")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    fun announceBid(@PathVariable gameId: String, @RequestBody bid: Bid) {
        val game = data.getGameOrThrow(gameId)
        data.announceBid(game, bid, user)
        fire.saveGame(game)
    }

    @GetMapping("/{gameId}/showLastTrick",
                produces = [MediaType.APPLICATION_JSON_VALUE])
    fun showLastTrick(@PathVariable gameId: String): List<CardPlayed> {
        val game = data.getGameOrThrow(gameId)
        game.whoWonLastTrick?.let {
            return when (it) {
                PlayerPosition.NORTH,
                PlayerPosition.SOUTH -> game.plisCampNS.last()
                PlayerPosition.EAST,
                PlayerPosition.WEST  -> game.plisCampEW.last()
            }
        } ?: throw IllegalStateException(
                "Nobody won a trick, you can't see the last trick")
    }

    @GetMapping("/{gameId}/getScore",
                produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getScore(@PathVariable gameId: String): Score {
        val game = data.getGameOrThrow(gameId)
        return game.score
    }

    @GetMapping("/{gameId}/showAllTricks",
                produces = [MediaType.APPLICATION_JSON_VALUE])
    fun showAllTricks(@PathVariable gameId: String): List<List<CardPlayed>> {
        val game = data.getGameOrThrow(gameId)
        // If not finished, not allowed
        when (game.state) {
            TableState.PLAYING      -> throw NotAuthorizedOperation(
                    "Show all tricks is not allowed during a game")
            TableState.BETWEEN_GAMES,
            TableState.JOINING,
            TableState.BIDDING,
            TableState.DISTRIBUTING -> throw IllegalStateException(
                    "Game has not started yet")
            TableState.ENDED        -> return game.plisCampNS + game.plisCampEW
        }
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/{gameId}/setNickName")
    fun setNickNameGame(@RequestParam(required = true) nickname: String,
                        @PathVariable(required = true) gameId: String) {
        val set = data.getGameOrThrow(gameId)
        if (!inGame(set)) throw NotInGameException(gameId)
        if (nickname.isBlank()) throw EmptyNameException()
        user.nickname = nickname
        data.changeNickname(set, user)
        fire.saveGame(set)
    }

    private fun inGame(game: SetOfGames): Boolean {
        return game.players.any { it.uid == user.uid }
    }
}