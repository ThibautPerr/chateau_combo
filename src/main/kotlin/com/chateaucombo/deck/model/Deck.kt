package com.chateaucombo.deck.model

data class Deck(
    val cartes: List<Carte>,
    val cartesDisponibles: List<Carte> = listOf()
)