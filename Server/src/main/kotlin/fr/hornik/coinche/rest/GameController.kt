package fr.hornik.coinche.rest

import fr.hornik.coinche.model.*
import fr.hornik.coinche.model.values.*
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/games")
class GameController {
    @GetMapping("/home")
    fun home(): String {
        return "TOTO"
    }

    @GetMapping("/getTable", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getTable(@RequestParam(required = true) gameId: String, httpServletRequest: HttpServletRequest): Table {
        print(httpServletRequest.remoteAddr)
        // TODO with real values
        return Table(
                id = gameId,
                nicknames = Nicknames("Sacha", "Jessica", "Yustina", "Armand"),
                bids = listOf(Bid(CardColor.SPADE, 80, CoincheValues.NONE)),
                cards = listOf(Card(CardValue.EIGHT, CardColor.CLUB)),
                nextPlayer = PlayerPosition.EAST,
                played = listOf(
                        CardPlayed(Card(CardValue.NINE, CardColor.DIAMOND), BeloteValue.BELOTE, PlayerPosition.NORTH)),
                state = TableState.PLAYING
        )
    }

    @PostMapping("/playCard")
    fun playCard(@RequestParam gameId: String, @RequestBody card: Card) {
    }

    @PostMapping("/announceBid")
    fun announceBid(@RequestBody bid: Bid) {

    }

    @GetMapping("/showLastTrick", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun showLastTrick(@RequestParam gameId: String): List<CardPlayed> {
        // TODO real values
        return listOf(CardPlayed(Card(CardValue.NINE, CardColor.DIAMOND), BeloteValue.NONE, PlayerPosition.NORTH),
                      CardPlayed(Card(CardValue.EIGHT, CardColor.DIAMOND), BeloteValue.NONE, PlayerPosition.EAST),
                      CardPlayed(Card(CardValue.SEVEN, CardColor.DIAMOND), BeloteValue.NONE, PlayerPosition.SOUTH),
                      CardPlayed(Card(CardValue.KING, CardColor.DIAMOND), BeloteValue.BELOTE, PlayerPosition.WEST))
    }

    @GetMapping("/getScore", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getScore(@RequestParam(required = true) gameId: String): Score {
        // TODO
        return Score(80, 160)
    }

    @GetMapping("/showAllTricks", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun showAllTricks(@RequestParam(required = true) gameId: String): List<List<CardPlayed>> {
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