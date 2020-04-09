package fr.hornik.coinche.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.BAD_REQUEST)

class NotYourTurnException : Exception("It is not your turn")