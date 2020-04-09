package fr.hornik.coinche.exception

import fr.hornik.coinche.model.Bid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.BAD_REQUEST)
class InvalidBidException(bid: Bid) : Exception("The bid $bid is invalid")
