package fr.hornik.coinche.dto

import fr.hornik.coinche.model.SetOfGames

data class Game(val id: String, val nbJoined: Int, val name: String,
                val nicknameCreator: String, val inRoom: Boolean) {
    constructor(set: SetOfGames, inRoom: Boolean) : this(set.id,
                                                         set.players.size,
                                                         set.name,
                                                         set.players.first().nickname,
                                                         inRoom);
}