package fr.hornik.coinche

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CoincheApplication

fun main(args: Array<String>) {
    runApplication<CoincheApplication>(*args)
}
