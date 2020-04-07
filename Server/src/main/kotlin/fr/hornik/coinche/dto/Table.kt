package fr.hornik.coinche.dto

import fr.hornik.coinche.model.Bid
import fr.hornik.coinche.model.Card
import fr.hornik.coinche.model.CardPlayed
import fr.hornik.coinche.model.Nicknames
import fr.hornik.coinche.model.values.PlayerPosition
import fr.hornik.coinche.model.values.TableState

data class Table(val id: String, val nicknames: Nicknames, val cards: List<Card>, val played: List<CardPlayed>,
                 var state: TableState, val nextPlayer: PlayerPosition, val bids: List<Bid>)
