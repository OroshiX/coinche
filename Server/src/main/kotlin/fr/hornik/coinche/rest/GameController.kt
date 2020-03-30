package fr.hornik.coinche.rest

import fr.hornik.coinche.model.*
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class GameController {
    @RequestMapping("/home")
    fun home(): String {
        return "TOTO"
    }

    @RequestMapping("/getTable", method = [RequestMethod.GET], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getTable(@RequestParam(required = true) gameId: String): Table {
        // TODO with real values
        return Table(
                id = gameId,
                nicknames = Nicknames("Sacha", "Jessica", "Yustina", "Armand"),
                bids = listOf(Bid("SPADES", 80)),
                cards = listOf(Card(8, "CLUB")),
                nextPlayer = "EAST",
                played = listOf(CardPlayed(Card(9, "DIAMOND"), null, "NORTH")),
                state = "PLAYING"
        )
    }
}