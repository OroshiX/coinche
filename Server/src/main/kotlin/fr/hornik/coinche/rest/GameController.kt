package fr.hornik.coinche.rest

import fr.hornik.coinche.DataManagement
import fr.hornik.coinche.dto.Table
import fr.hornik.coinche.exception.GameNotExistingException
import fr.hornik.coinche.exception.NotInGameException
import fr.hornik.coinche.model.*
import fr.hornik.coinche.model.values.BeloteValue
import fr.hornik.coinche.model.values.CardColor
import fr.hornik.coinche.model.values.CardValue
import fr.hornik.coinche.model.values.PlayerPosition
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/games")
class GameController(@Autowired val data: DataManagement, @Autowired val user: User) {
    @GetMapping("/home")
    fun home(): String {
        return "TOTO"
    }

    @GetMapping("/{gameId}/getTable", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getTable(@PathVariable("gameId") gameId: String, httpServletRequest: HttpServletRequest): Table {
        print(httpServletRequest.remoteAddr)
        val set = data.getGame(gameId) ?: throw GameNotExistingException(gameId)
        if (!set.players.map { it.uid }.contains(user.uid)) {
            throw NotInGameException(gameId)
        }

        return Table(
                id = gameId,
                state = set.state,
                played = set.onTable,
                nextPlayer = set.whoseTurn,
                cards = set.players.first { player -> player.uid == user.uid }.cardsInHand,
                bids = set.bids,
                nicknames = Nicknames(set.players)
        )
    }

    @PostMapping("/{gameId}/playCard")
    fun playCard(@PathVariable gameId: String, @RequestBody card: Card) {
    }

    @PostMapping("/{gameId}/announceBid")
    fun announceBid(@PathVariable gameId: String, @RequestBody bid: Bid) {

    }

    @GetMapping("/{gameId}/showLastTrick", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun showLastTrick(@PathVariable gameId: String): List<CardPlayed> {
        // TODO real values
        return listOf(CardPlayed(Card(CardValue.NINE, CardColor.DIAMOND), BeloteValue.NONE, PlayerPosition.NORTH),
                      CardPlayed(Card(CardValue.EIGHT, CardColor.DIAMOND), BeloteValue.NONE, PlayerPosition.EAST),
                      CardPlayed(Card(CardValue.SEVEN, CardColor.DIAMOND), BeloteValue.NONE, PlayerPosition.SOUTH),
                      CardPlayed(Card(CardValue.KING, CardColor.DIAMOND), BeloteValue.BELOTE, PlayerPosition.WEST))
    }

    @GetMapping("/{gameId}/getScore", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getScore(@PathVariable gameId: String): Score {
        // TODO
        return Score(80, 160)
    }

    @GetMapping("/{gameId}/showAllTricks", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun showAllTricks(@PathVariable gameId: String): List<List<CardPlayed>> {
        // TODO
        // If not finished, not allowed
        return listOf(
                listOf(CardPlayed(Card(CardValue.NINE, CardColor.DIAMOND), BeloteValue.NONE, PlayerPosition.NORTH),
                       CardPlayed(Card(CardValue.EIGHT, CardColor.DIAMOND), BeloteValue.NONE, PlayerPosition.EAST),
                       CardPlayed(Card(CardValue.SEVEN, CardColor.DIAMOND), BeloteValue.NONE, PlayerPosition.SOUTH),
                       CardPlayed(Card(CardValue.KING, CardColor.DIAMOND), BeloteValue.BELOTE, PlayerPosition.WEST)),
                listOf(CardPlayed(Card(CardValue.NINE, CardColor.CLUB), BeloteValue.NONE, PlayerPosition.NORTH),
                       CardPlayed(Card(CardValue.EIGHT, CardColor.CLUB), BeloteValue.NONE, PlayerPosition.EAST),
                       CardPlayed(Card(CardValue.SEVEN, CardColor.CLUB), BeloteValue.NONE, PlayerPosition.SOUTH),
                       CardPlayed(Card(CardValue.KING, CardColor.CLUB), BeloteValue.NONE, PlayerPosition.WEST)))
    }

}