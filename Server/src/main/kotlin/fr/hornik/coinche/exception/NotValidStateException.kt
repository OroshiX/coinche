package fr.hornik.coinche.exception

import fr.hornik.coinche.model.values.TableState
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.BAD_REQUEST)
class NotValidStateException(realState: TableState, expected: TableState) :
        Exception(
                "Illegal operation in $realState: we can do this only in state $expected")
