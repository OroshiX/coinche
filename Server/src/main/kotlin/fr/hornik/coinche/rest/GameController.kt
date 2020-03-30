package fr.hornik.coinche.rest

import fr.hornik.coinche.model.*
import fr.hornik.coinche.model.values.*
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*

@RestController
class GameController {
    @GetMapping("/home")
    fun home(): String {
        return "TOTO"
    }

    @GetMapping("/getTable", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getTable(@RequestParam(required = true) gameId: String): Table {
        // TODO with real values
        return Table(
                id = gameId,
                nicknames = Nicknames("Sacha", "Jessica", "Yustina", "Armand"),
                bids = listOf(Bid("SPADES", 80, CoincheValues.NONE)),
                cards = listOf(Card(CardValue.EIGHT, CardColor.CLUB)),
                nextPlayer = PlayerPosition.EAST,
                played = listOf(CardPlayed(Card(CardValue.NINE, CardColor.DIAMOND), BeloteValue.BELOTE, PlayerPosition.NORTH)),
                state = TableState.PLAYING
        )
    }
}