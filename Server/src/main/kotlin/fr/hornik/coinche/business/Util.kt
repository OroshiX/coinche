package fr.hornik.coinche.business

import fr.hornik.coinche.model.SetOfGames
import fr.hornik.coinche.model.User

fun inGame(game: SetOfGames, user: User): Boolean {
    return game.players.any { it.uid == user.uid }
}