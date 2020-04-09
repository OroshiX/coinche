package fr.hornik.coinche.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.BAD_REQUEST)
class EmptyNameException : Exception("You should provide a non empty nickname")