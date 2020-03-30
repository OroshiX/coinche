package fr.hornik.coinche.model

data class Table(val id: String, val nicknames: Nicknames, val cards: List<Card>, val played: List<CardPlayed>, val state: String, val nextPlayer: String, val bids: List<Bid>)
