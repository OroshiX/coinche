package fr.hornik.coinche.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.BAD_REQUEST)

class GameFullException(gameId: String) :
        Exception("The game $gameId is full, you cannot join it.") {

}
