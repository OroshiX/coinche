package fr.hornik.coinche.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.BAD_REQUEST)

class NotInGameException(gameId: String) : Exception("You are not a part of the game $gameId")
