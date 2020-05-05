package fr.hornik.coinche.rest

import fr.hornik.coinche.business.inGame
import fr.hornik.coinche.component.DataManagement
import fr.hornik.coinche.component.FireApp
import fr.hornik.coinche.dto.Table
import fr.hornik.coinche.exception.*
import fr.hornik.coinche.model.*
import fr.hornik.coinche.model.values.CardColor
import fr.hornik.coinche.model.values.PlayerPosition
import fr.hornik.coinche.model.values.TableState
import fr.hornik.coinche.util.dbgLevel
import fr.hornik.coinche.util.debugPrintln
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
        if (!inGame(set, user)) throw NotInGameException(gameId)
        return set.toTable(user.uid)
    }

    @PostMapping("/{gameId}/playCard",
            produces = [MediaType.APPLICATION_JSON_VALUE])
    fun playCard(@PathVariable gameId: String, @RequestBody card: Card) : CardPlayed {
        val game = data.getGameOrThrow(gameId)
        if (!inGame(game, user)) throw NotInGameException(gameId)
        if (game.state != TableState.PLAYING) throw NotValidStateException(
                game.state, TableState.PLAYING)
        if (game.players.first { it.uid == user.uid }.position != game.whoseTurn) throw NotYourTurnException()
        game.whoseTurnTimeLastChg = System.currentTimeMillis()

        return data.playCard(game, card, user)

    }

    @PostMapping("/{gameId}/announceBid")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    fun announceBid(@PathVariable gameId: String, @RequestBody bid: Bid) {
        val game = data.getGameOrThrow(gameId)
        game.whoseTurnTimeLastChg = System.currentTimeMillis()
        data.announceBid(game, bid, user)
        // no need to save after announceBid ( done at the end of the function
    }

    @PostMapping("/{gameId}/deleteGame")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    fun deleteGame(@PathVariable gameId: String) {
        val game = data.getGameOrThrow(gameId)
        if (user.uid.isBlank()) throw NotAuthorizedOperation("You need to be authenticated for any modifiation action")
        if (! data.deleteGame(game, user) ) {
            // the fire delete has been done in data.deleteGame as well as the refresh.
            throw NotAuthorizedOperation(
                    "You need to be registered in a game to delete it")
        }
    }


    @GetMapping("/{gameId}/showLastTrick",
                produces = [MediaType.APPLICATION_JSON_VALUE])
    fun showLastTrick(@PathVariable gameId: String): List<CardPlayed> {
        val game = data.getGameOrThrow(gameId)
        if ((game.plisCampNS.size + game.plisCampEW.size) == 0) {
            throw IllegalStateException(
                    "Nobody won a trick, you can't see the last trick")
        }

        game.whoWonLastTrick?.let {
            return when (it) {
                PlayerPosition.NORTH,
                PlayerPosition.SOUTH -> game.plisCampNS[game.plisCampNS.size -1]!!
                PlayerPosition.EAST,
                PlayerPosition.WEST  -> game.plisCampEW[game.plisCampEW.size -1]!!
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

    @GetMapping("/{gameId}/checkCoherency",
            produces = [MediaType.APPLICATION_JSON_VALUE])
    fun checkCoherency(@PathVariable gameId: String) {
        val gameOK = data.checkCoherency(data.getGameOrThrow(gameId))
        debugPrintln(dbgLevel.REGULAR,"Game $gameId coherency os $gameOK")
    }

    @GetMapping("/{gameId}/showAllTricks",
                produces = [MediaType.APPLICATION_JSON_VALUE])
    fun showAllTricks(@PathVariable gameId: String): Map<Int,List<CardPlayed>> {
        val game = data.getGameOrThrow(gameId)
        // If not finished, not allowed
        when (game.state) {
            TableState.PLAYING      -> throw NotAuthorizedOperation(
                    "Show all tricks is not allowed during a game")
            TableState.JOINING,
            TableState.BIDDING,
            TableState.DISTRIBUTING -> throw IllegalStateException(
                    "Game has not started yet")
            TableState.ENDED,
            TableState.BETWEEN_GAMES        -> return game.getAllTricks()
        }
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/{gameId}/setNickName")
    fun setNickNameGame(@RequestBody(required = true) nickname: String,
                        @PathVariable(required = true) gameId: String) {
        if (user.uid.isBlank()) throw NotAuthenticatedException()
        val set = data.getGameOrThrow(gameId)
        if (!inGame(set, user)) throw NotInGameException(gameId)
        if (nickname.isBlank()) throw EmptyNameException()
        user.nickname = nickname
        data.changeNickname(set, user)
    }
}