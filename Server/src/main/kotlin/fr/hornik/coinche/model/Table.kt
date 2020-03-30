package fr.hornik.coinche.model

import fr.hornik.coinche.model.values.PlayerPosition
import fr.hornik.coinche.model.values.TableState

data class Table(val id: String, val nicknames: Nicknames, val cards: List<Card>, val played: List<CardPlayed>,
                 var state: TableState, val nextPlayer: PlayerPosition, val bids: List<Bid>)
