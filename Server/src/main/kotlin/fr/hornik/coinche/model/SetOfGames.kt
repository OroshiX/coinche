package fr.hornik.coinche.model

import fr.hornik.coinche.model.values.PlayerPosition
import fr.hornik.coinche.model.values.TableState

data class SetOfGames(var score: Score, val players: List<Player>, val plisCampNS: List<List<CardPlayed>>,
                      val plisCampEW: List<List<CardPlayed>>, val bids: List<Bid>, var whoseTurn: PlayerPosition,
                      val onTable: List<CardPlayed>, var state: TableState, var currentFirstPlayer: PlayerPosition,
                      var currentBid: Bid) {

}